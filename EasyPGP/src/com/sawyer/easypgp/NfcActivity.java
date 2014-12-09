package com.sawyer.easypgp;

import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class NfcActivity extends ActionBarActivity {

  public static final String TAG = "NfcDemo";

  private TextView mTextView;
  private NfcAdapter mNfcAdapter;
  private CheckBox checkBoxRead;
  public static final String MIME_TEXT_PLAIN = "text/plain";
  public static final String MIME_PUBLIC_KEY = "application/pubk.com.sawyer.easypgp";
  public byte[] publicKeyBytes;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_nfc);

    // Wiring up UI
    Intent intent = getIntent();
    publicKeyBytes = intent.getByteArrayExtra("com.sawyer.easypgp.PUBLIC_KEY");
    mTextView = (TextView) findViewById(R.id.nfc_tv);
    mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
    checkBoxRead = (CheckBox) findViewById(R.id.checkBoxRead);

    if (mNfcAdapter == null) {
      // Stop here, we definitely need NFC
      Toast.makeText(this, "This device doesn't support NFC.",
          Toast.LENGTH_LONG).show();
      finish();
      return;

    }

    if (!mNfcAdapter.isEnabled()) {
      mTextView.setText("NFC is disabled.");
    } else {
      mTextView.setText("NFC is enabled!");
    }

    handleIntent(getIntent());
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
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

  @Override
  protected void onResume() {
    super.onResume();

    /**
     * It's important, that the activity is in the foreground (resumed).
     * Otherwise an IllegalStateException is thrown.
     */
    setupForegroundDispatch(this, mNfcAdapter);
  }

  @Override
  protected void onPause() {
    /**
     * Call this before onPause, otherwise an IllegalArgumentException is thrown
     * as well.
     */
    stopForegroundDispatch(this, mNfcAdapter);

    super.onPause();
  }

  @Override
  protected void onNewIntent(Intent intent) {
    /**
     * This method gets called, when a new Intent gets associated with the
     * current activity instance. Instead of creating a new activity,
     * onNewIntent will be called. For more information have a look at the
     * documentation.
     * 
     * In our case this method gets called, when the user attaches a Tag to the
     * device.
     */
    handleIntent(intent);
  }

  private void handleIntent(Intent intent) {
    String action = intent.getAction();
    if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

      String type = intent.getType();
      if (MIME_PUBLIC_KEY.equals(type)) {

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (checkBoxRead.isChecked()) {
          new NdefReaderTask().execute(tag);
        } else {
          new NdefWriterTask().execute(tag);
        }

      } else {
        Log.d(TAG, "Wrong mime type: " + type);
      }
    } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

      // In case we would still use the Tech Discovered Intent
      Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
      String[] techList = tag.getTechList();
      String searchedTech = Ndef.class.getName();

      for (String tech : techList) {
        if (searchedTech.equals(tech)) {
          new NdefReaderTask().execute(tag);
          break;
        }
      }
    }
  }

  /**
   * @param activity
   *          The corresponding {@link Activity} requesting the foreground
   *          dispatch.
   * @param adapter
   *          The {@link NfcAdapter} used for the foreground dispatch.
   */
  public static void setupForegroundDispatch(final Activity activity,
      NfcAdapter adapter) {
    final Intent intent = new Intent(activity.getApplicationContext(),
        activity.getClass());
    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

    final PendingIntent pendingIntent = PendingIntent.getActivity(
        activity.getApplicationContext(), 0, intent, 0);

    IntentFilter[] filters = new IntentFilter[1];
    String[][] techList = new String[][] {};

    // Notice that this is the same filter as in our manifest.
    filters[0] = new IntentFilter();
    filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
    filters[0].addCategory(Intent.CATEGORY_DEFAULT);
    try {
      filters[0].addDataType(MIME_PUBLIC_KEY);
    } catch (MalformedMimeTypeException e) {
      throw new RuntimeException("Check your mime type.");
    }

    adapter
        .enableForegroundDispatch(activity, pendingIntent, filters, techList);
  }

  /**
   * @param activity
   *          The corresponding {@link BaseActivity} requesting to stop the
   *          foreground dispatch.
   * @param adapter
   *          The {@link NfcAdapter} used for the foreground dispatch.
   */
  public static void stopForegroundDispatch(final Activity activity,
      NfcAdapter adapter) {
    adapter.disableForegroundDispatch(activity);
  }

  private NdefRecord createRecord(String text)
      throws UnsupportedEncodingException {

    // create the message in according with the standard
    String lang = "en";
    byte[] textBytes = text.getBytes();
    byte[] langBytes = lang.getBytes("US-ASCII");
    int langLength = langBytes.length;
    int textLength = textBytes.length;

    byte[] payload = new byte[1 + langLength + textLength];
    payload[0] = (byte) langLength;

    // copy langbytes and textbytes into payload
    System.arraycopy(langBytes, 0, payload, 1, langLength);
    System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

    NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
        NdefRecord.RTD_TEXT, new byte[0], payload);
    return recordNFC;
  }

  private class NdefWriterTask extends AsyncTask<Tag, Void, String> {

    @Override
    protected String doInBackground(Tag... params) {
      Tag tag = params[0];

      Ndef ndef = Ndef.get(tag);
      if (ndef == null) {
        // NDEF is not supported by this Tag.
        return null;
      }
      try {
        // NdefRecord[] records = { createRecord(publicKeyString) };
        //
        // NdefMessage message = new NdefMessage(records);
        NdefMessage msg = new NdefMessage(
            new NdefRecord[] { NdefRecord.createMime(
                "application/pubk.com.sawyer.easypgp", publicKeyBytes) });
        ndef.connect();
        ndef.writeNdefMessage(msg);
        ndef.close();
        return "Success";
      } catch (Exception e) {
        e.printStackTrace();
      }

      return null;
    }

    @Override
    protected void onPostExecute(String result) {
      if (result == "Success") {
        Toast.makeText(NfcActivity.this,
            "Successfully wrote Public Key to NFC tag!", Toast.LENGTH_LONG)
            .show();
      } else {
        Toast.makeText(NfcActivity.this,
            "Failed to write Public Key to NFC tag!", Toast.LENGTH_LONG).show();
      }
    }
  }

  private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

    PublicKey pubkey;

    @Override
    protected String doInBackground(Tag... params) {
      Tag tag = params[0];

      Ndef ndef = Ndef.get(tag);
      if (ndef == null) {
        // NDEF is not supported by this Tag.
        return null;
      }

      NdefMessage ndefMessage = ndef.getCachedNdefMessage();

      NdefRecord[] records = ndefMessage.getRecords();
      for (NdefRecord ndefRecord : records) {
        try {
          pubkey = readKey(ndefRecord);
          return pubkey.toString();
        } catch (UnsupportedEncodingException e) {
          Log.e(TAG, "Unsupported Encoding", e);
        }
      }

      return null;
    }

    private PublicKey readKey(NdefRecord record)
        throws UnsupportedEncodingException {
      /*
       * See NFC forum specification for "Text Record Type Definition" at 3.2.1
       * 
       * http://www.nfc-forum.org/specs/
       * 
       * bit_7 defines encoding bit_6 reserved for future use, must be 0
       * bit_5..0 length of IANA language code
       */

      byte[] payload = record.getPayload();

      PublicKey publicKey = null;
      try {
        publicKey = KeyFactory.getInstance("RSA").generatePublic(
            new X509EncodedKeySpec(payload));
      } catch (InvalidKeySpecException e) {
        e.printStackTrace();
      } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      }

      // Get the PublicKey object
      return publicKey;

    }

    @Override
    protected void onPostExecute(String result) {
      if (result != null) {
        mTextView.setText("Public Key: " + result);
      }
    }
  }
}