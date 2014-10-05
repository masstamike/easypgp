package com.sawyer.easypgp;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements
      NavigationDrawerFragment.NavigationDrawerCallbacks {

   static final String TAG = "AsymmetricAlgorithmRSA";

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

      // GmailInbox inbox = new GmailInbox(new Properties());

      mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
            .findFragmentById(R.id.navigation_drawer);
      mTitle = getTitle();

      // Set up the drawer.
      mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
            (DrawerLayout) findViewById(R.id.drawer_layout));

   }

   public void sendMessage(View view) {
      try {
         // Original text
         EditText tvorig = (EditText) findViewById(R.id.textToEncrypt);
         String text = tvorig.getText().toString();

         // Generate key pair for 1024-bit RSA encryption and decryption
         Key publicKey = null;
         Key privateKey = null;
         try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            KeyPair kp = kpg.genKeyPair();
            publicKey = kp.getPublic();
            privateKey = kp.getPrivate();
         } catch (Exception e) {
            Log.e(TAG, "RSA key pair error");
         }

         // Encode the original data with RSA private key
         byte[] encodedBytes = null;
         try {
            Cipher c = Cipher.getInstance("RSA");
            c.init(Cipher.ENCRYPT_MODE, privateKey);
            encodedBytes = c.doFinal(text.getBytes());
         } catch (Exception e) {
            Log.e(TAG, "RSA encryption error");
         }
         /*
          * TextView tvencoded = (TextView) findViewById(R.id.textView2);
          * tvencoded.setText("[ENCODED]:\n" +
          * Base64.encodeToString(encodedBytes, Base64.DEFAULT) + "\n");
          */

         // Decode the encoded data with RSA public key
         /*
          * byte[] decodedBytes = null; try { Cipher c =
          * Cipher.getInstance("RSA"); c.init(Cipher.DECRYPT_MODE, publicKey);
          * decodedBytes = c.doFinal(encodedBytes); } catch (Exception e) {
          * Log.e(TAG, "RSA decryption error"); } TextView tvdecoded =
          * (TextView) findViewById(R.id.textView3);
          * tvdecoded.setText("[DECODED]:\n" + new String(decodedBytes) +
          * "\nPublic Key: " + Base64.encodeToString(publicKey.getEncoded(),
          * Base64.DEFAULT) + "\nPrivate Key: " +
          * Base64.encodeToString(privateKey.getEncoded(), Base64.DEFAULT));
          */

         // Send an email
         Intent emailIntent = new Intent(Intent.ACTION_SEND);

         EditText recipientText = (EditText) findViewById(R.id.editText1);
         EditText subjectText = (EditText) findViewById(R.id.editText2);
         // EditText contentText = (EditText) findViewById(R.id.editText3);

         String messageEncrypted = Base64.encodeToString(encodedBytes,
               Base64.DEFAULT);
         emailIntent.setData(Uri.parse("mailto:"));
         emailIntent.setType("text/plain");
         emailIntent.putExtra(Intent.EXTRA_EMAIL,
               new String[] { recipientText.getText().toString() });
         emailIntent.putExtra(Intent.EXTRA_SUBJECT, subjectText.getText()
               .toString());
         // Log.d("com.sawyer.easypgp",contentEncrypted[0].toString());
         emailIntent.putExtra(Intent.EXTRA_TEXT, messageEncrypted);
         startActivity(Intent.createChooser(emailIntent, "Send mail..."));
         finish();
         Log.i("Finished sending email...", "");
      } catch (android.content.ActivityNotFoundException ex) {
         Toast.makeText(MainActivity.this,
               "There is no email client installed.", Toast.LENGTH_SHORT)
               .show();
      }
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
