package com.sawyer.easypgp;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class NfcActivity extends ActionBarActivity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_nfc);
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.nfc, menu);
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
   public void onResume() {
      super.onResume();
      if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
         processIntent(getIntent());
      }
   }

   @Override
   public void onNewIntent(Intent intent) {
      // onResume gets called after this to handle the intent
      setIntent(intent);
   }

   /**
    * Parses the NDEF Message from the intent
    */
   void processIntent(Intent intent) {
      TextView textView = (TextView) findViewById(R.id.nfc_tv);

      Parcelable[] rawMsgs = intent
            .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
      // Only one message for now
      // TODO: Add contact name to payload.
      NdefMessage msg = (NdefMessage) rawMsgs[0];
      // Record 0 contains the MIME type, record 1 is the AAR, if present
      textView.setText(new String(msg.getRecords()[0].getPayload()));
   }

}
