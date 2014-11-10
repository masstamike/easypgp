package com.sawyer.easypgp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.TextView;
import android.widget.Toast;

// TODO: Move utilities to separate classes.
// TODO: Encrypt AES key with RSA encryption, then encrypt message with AES
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
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
    ObjectOutputStream oos = null;
    File file = getBaseContext().getFileStreamPath("publicKey");
    if (!file.exists()) {
      try {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        KeyPair kp = kpg.genKeyPair();
        PublicKey publicKey = kp.getPublic();
        PrivateKey privateKey = kp.getPrivate();
        FileOutputStream filePublic = openFileOutput("publicKey",
            Context.MODE_PRIVATE);
        oos = new ObjectOutputStream(filePublic);
        oos.writeObject(publicKey);
        FileOutputStream fileOutput = openFileOutput("privateKey",
            Context.MODE_PRIVATE);
        oos = new ObjectOutputStream(fileOutput);
        oos.writeObject(privateKey);
        Cipher c = Cipher.getInstance("RSA");
        c.init(Cipher.ENCRYPT_MODE, publicKey);
        FileOutputStream fileC = openFileOutput("cipher",
            Context.MODE_PRIVATE);
        oos = new ObjectOutputStream(fileC);
        oos.writeObject(c);
      } catch (Exception e) {
        Log.e(TAG, "RSA key pair error");
      }
    }

    mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
        .findFragmentById(R.id.navigation_drawer);
    mTitle = getTitle();

    // Set up the drawer.
    mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
        (DrawerLayout) findViewById(R.id.drawer_layout));

    // Log Emails...
    Log.i("Emails", "About to create GmailInbox");
    GmailInbox gmail = new GmailInbox();
    Log.i("Emails", "Created GmailInbox");
    gmail.read(getResources().openRawResource(R.raw.smtp_properties));
    Log.i("Emails", "After calling gmail.read()");
  }
  
  @Override
  public void onResume() {
    super.onResume();
    if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
      processIntent(getIntent());
    }
  }
  
  @Override
  public void onNewIntent (Intent intent) {
    // onResume gets called after this to handle the intent
    setIntent(intent);
  }

  public void sendMessage(View view) {
    try {
      // Retrieve Key
      // TODO: Move this to it's own function in utilities
      ObjectInputStream ois = null;
      PublicKey publicKey = null;

      try {
        FileInputStream file = this.getApplicationContext()
            .openFileInput("publicKey");
        ois = new ObjectInputStream(file);
        publicKey = (PublicKey) ois.readObject();
        Log.d("publicKey: ", publicKey.toString());
      } catch (FileNotFoundException e1) {
        e1.printStackTrace();
      } catch (StreamCorruptedException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }

      // Original text
      EditText tvorig = (EditText) findViewById(R.id.textToEncrypt);
      String text = tvorig.getText().toString();

      // Encode the original data with RSA private key
      byte[] encodedBytes = null;
      try {
        Cipher c = Cipher.getInstance("RSA");
        c.init(Cipher.ENCRYPT_MODE, publicKey);
        encodedBytes = c.doFinal(text.getBytes());
        Log.d("text", text);
      } catch (Exception e) {
        Log.e(TAG, "RSA encryption error");
      }

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
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    switch (number) {
    case 1:
      mTitle = getString(R.string.title_compose);
      break;
    case 2:
      mTitle = getString(R.string.title_decrypt);
      Fragment frag = new DecodeFragment();
      ft.replace(R.id.container, frag).commit();
      break;
    case 3:
      mTitle = getString(R.string.title_share_key);
      Fragment share_key_frag = new ShareKeyFragment();
      ft.replace(R.id.container, share_key_frag).commit();
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
    } else if (id == R.id.action_example) {
      sendMessage(findViewById(R.layout.activity_main));
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

  // Runs when click decode on decode fragment.
  // TODO: Move to separate class
  public void onClickDecode(View view) {

    ObjectInputStream ois = null;
    PrivateKey privateKey = null;

    try {
      FileInputStream file = this.getApplicationContext().openFileInput(
          "privateKey");
      ois = new ObjectInputStream(file);
      privateKey = (PrivateKey) ois.readObject();
      Log.d("privateKey:", privateKey.toString());
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
    } catch (StreamCorruptedException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (Exception e) {
      Log.d("Uncaught Exception", "Exception not caught.");
      e.printStackTrace();
    }
    TextView encodedTV = (TextView) findViewById(R.id.decryptText);
    String encodedBytes = encodedTV.getText().toString();
    byte[] messageEncrypted = Base64.decode(encodedBytes, Base64.DEFAULT);
    Log.d("Encoded Bytes:", "Encoded Bytes: " + encodedBytes);
    // Decode the encoded data with RSA public key
    byte[] decodedBytes = { (byte) 0 };
    try {
      Cipher c = Cipher.getInstance("RSA");
      c.init(Cipher.DECRYPT_MODE, privateKey);
      decodedBytes = c.doFinal(messageEncrypted);
      Log.d("decodedBytes", "decodedBytes " + new String(decodedBytes));
    } catch (Exception e) {
      Log.e(TAG, "RSA decryption error");
      e.printStackTrace();
    }
    TextView tvdecoded = (TextView) findViewById(R.id.decryptedText);
    tvdecoded.setText("[DECODED]:\n" + new String(decodedBytes));
  }

  // Runs when click send key button on ShareKey fragment.
  // TODO: Move to separate class
  public void onClickSendKey(View view) {

    // Declare variables;
    NfcAdapter mNfcAdapter;
    ObjectInputStream ois = null;
    PublicKey publicKey = null;
    NdefMessage message = null;

    // Check to see if NFC is available
    mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
    if (mNfcAdapter == null) {
      Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG)
          .show();
      finish();
      return;
    }

    // Read public key
    try {
      FileInputStream file = this.getApplicationContext().openFileInput(
          "publicKey");
      ois = new ObjectInputStream(file);
      publicKey = (PublicKey) ois.readObject();
      Log.d("publicKey: ", publicKey.toString());
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
    } catch (StreamCorruptedException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    // Create message
    try {
      message = new NdefMessage(publicKey.getEncoded());
    } catch (FormatException e) {
      e.printStackTrace();
    }

    mNfcAdapter.setNdefPushMessage(message, this, this);

  }
  
  /**
   * Parses the NDEF Message from the intent
   */
  void processIntent (Intent intent) {
    TextView textView = (TextView) findViewById(R.id.textView);
    
    Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
    // Only one message for now
    // TODO: Add contact name to payload.
    NdefMessage msg = (NdefMessage) rawMsgs[0];
    // Record 0 contains the MIME type, record 1 is the AAR, if present
    textView.setText(new String(msg.getRecords()[0].getPayload()));
  }

}

