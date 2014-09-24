package com.sawyer.easypgp;

import java.util.ArrayList;
import java.util.Properties;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.sawyer.mail.utils.GmailInbox;

public class MainActivity extends ActionBarActivity implements
      NavigationDrawerFragment.NavigationDrawerCallbacks {

   /**
    * Fragment managing the behaviors, interactions and presentation of the
    * navigation drawer.
    */
   private NavigationDrawerFragment mNavigationDrawerFragment;

   /**
    * Used to store the last screen title. For use in
    * {@link #restoreActionBar()}.
    */
   private CharSequence mTitle;

   @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//GmailInbox inbox = new GmailInbox(new Properties());
		

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		
	// Get the reference of ListViewAnimals
      /*ListView animalList=(ListView)findViewById(R.id.listViewEmails);
     
     
       final String[] emailList = inbox.print10Messages(inbox.messages, inbox.messageCount);
       // Create The Adapter with passing ArrayList as 3rd parameter
       ArrayAdapter<String> arrayAdapter =     
       new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, emailList);
       // Set The Adapter
       animalList.setAdapter(arrayAdapter);
      
       // register onClickListener to handle click events on each item
       animalList.setOnItemClickListener(new OnItemClickListener()
          {
                   // argument position gives the index of item which is clicked
                  public void onItemClick(AdapterView<?> arg0, View v,int position, long arg3)
                  {
                     
                          String selectedEmail=emailList[position];
                          Toast.makeText(getApplicationContext(), "Animal Selected : "+selectedEmail,   Toast.LENGTH_LONG).show();
                       }
          });
	}

   public void sendMessage(View view) {
      try {
         // Send an email
         Intent emailIntent = new Intent(Intent.ACTION_SEND);

         EditText editText = (EditText) findViewById(R.id.editText1);
         String message = editText.getText().toString();
         emailIntent.setData(Uri.parse("mailto:"));
         emailIntent.setType("text/plain");
         emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { message });
         emailIntent.putExtra(Intent.EXTRA_SUBJECT, "TESTING");
         emailIntent.putExtra(Intent.EXTRA_TEXT, "This is a test email");
         startActivity(Intent.createChooser(emailIntent, "Send mail..."));
         finish();
         Log.i("Finished sending email...", "");
      } catch (android.content.ActivityNotFoundException ex) {
         Toast.makeText(MainActivity.this,
               "There is no email client installed.", Toast.LENGTH_SHORT)
               .show();
      }*/
   }

   @Override
   public void onNavigationDrawerItemSelected(int position) {
      // update the main content by replacing fragments
      FragmentManager fragmentManager = getSupportFragmentManager();
      fragmentManager
            .beginTransaction()
            .replace(R.id.container,
                  PlaceholderFragment.newInstance(position + 1)).commit();
   }

   public void onSectionAttached(int number) {
      switch (number) {
      case 1:
         mTitle = getString(R.string.title_section1);
         break;
      case 2:
         mTitle = getString(R.string.title_section2);
         break;
      case 3:
         mTitle = getString(R.string.title_section3);
         break;
      }
   }

   public void restoreActionBar() {
      ActionBar actionBar = getSupportActionBar();
      actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
      actionBar.setDisplayShowTitleEnabled(true);
      actionBar.setTitle(mTitle);
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      if (!mNavigationDrawerFragment.isDrawerOpen()) {
         // Only show items in the action bar relevant to this screen
         // if the drawer is not showing. Otherwise, let the drawer
         // decide what to show in the action bar.
         getMenuInflater().inflate(R.menu.main, menu);
         restoreActionBar();
         return true;
      }
      return super.onCreateOptionsMenu(menu);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      // Handle action bar item clicks here. The action bar will
      // automatically handle clicks on the Home/Up button, so long
      // as you specify a parent activity in AndroidManifest.xml.
      int id = item.getItemId();
      if (id == R.id.action_settings) {
         return true;
      }
      return super.onOptionsItemSelected(item);
   }

   /**
    * A placeholder fragment containing a simple view.
    */
   public static class PlaceholderFragment extends Fragment {
      /**
       * The fragment argument representing the section number for this
       * fragment.
       */
      private static final String ARG_SECTION_NUMBER = "section_number";

      /**
       * Returns a new instance of this fragment for the given section number.
       */
      public static PlaceholderFragment newInstance(int sectionNumber) {
         PlaceholderFragment fragment = new PlaceholderFragment();
         Bundle args = new Bundle();
         args.putInt(ARG_SECTION_NUMBER, sectionNumber);
         fragment.setArguments(args);
         return fragment;
      }

      public PlaceholderFragment() {
      }

      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
         View rootView = inflater.inflate(R.layout.fragment_main, container,
               false);
         return rootView;
      }

      @Override
      public void onAttach(Activity activity) {
         super.onAttach(activity);
         ((MainActivity) activity).onSectionAttached(getArguments().getInt(
               ARG_SECTION_NUMBER));
      }
   }

}
