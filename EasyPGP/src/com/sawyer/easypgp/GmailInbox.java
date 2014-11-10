package com.sawyer.easypgp;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class GmailInbox extends AsyncTask<Activity, Void, String[]> {
   Activity mainActivityContext;

   public static String[] print10Messages(Message[] messages, int messageCount) {
      String[] list = new String[10];
      for (int i = messageCount - 1, listI = 0; i > messageCount - 11; i--, listI++) {
         try {
            list[listI] = "Mail Subject:- " + messages[i].getSubject();
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      return list;
   }

   @Override
   public String[] doInBackground(Activity... params) {
      mainActivityContext = params[0];
      Properties props = System.getProperties();
      props.setProperty("mail.store.protocol", "imaps");
      props.setProperty("mail.imaps.host", "imaps.gmail.com");
      props.put("mail.imaps.auth", "true");
      props.put("mail.imaps.port", "993");
      props.put("mail.imaps.socketFactory.port", "993");
      props.put("mail.imaps.socketFactory.class",
            "javax.net.ssl.SSLSocketFactory");
      props.put("mail.imaps.socketFactory.fallback", "false");
      props.setProperty("mail.imaps.quitwait", "false");
      String[] tenMessages = null;

      try {
         Session session = Session.getDefaultInstance(props, null);

         Store store = session.getStore("imaps");

         store.connect("imap.gmail.com", "michaelsawyer92@gmail.com",
               "wogywimdjubybtnk");

         Folder inbox = store.getFolder("inbox");
         inbox.open(Folder.READ_ONLY);
         int messageCount = inbox.getMessageCount();

         Log.i("Emailer", "------------------------------");
         Log.i("Emailer", "Total Messages:- " + messageCount);
         Log.i("Emailer", "------------------------------");
         tenMessages = print10Messages(inbox.getMessages(),
               inbox.getMessageCount());

         inbox.close(true);
         store.close();
      } catch (NoSuchProviderException e) {
         e.printStackTrace();
      } catch (MessagingException e) {
         e.printStackTrace();
      } catch (Exception e) {
         e.printStackTrace();
      }
      return tenMessages;
   }

   @Override
   protected void onPostExecute(String[] result) {
      // Update activity with emails.
	      try {
	          ListView lv = (ListView) mainActivityContext.findViewById(R.id.emailList);
	          ArrayList<String> list = new ArrayList<String>();
	          for (int i = 0; i < result.length; i++)
	             list.add(result[i]);
	          ArrayAdapter<String> adapter = new ArrayAdapter<String>(mainActivityContext,
	                android.R.layout.simple_list_item_1, list);
	          lv.setAdapter(adapter);
	       } catch (Exception e) {
	          // TODO Auto-generated catch block
	          e.printStackTrace();
	       }
   }
}
