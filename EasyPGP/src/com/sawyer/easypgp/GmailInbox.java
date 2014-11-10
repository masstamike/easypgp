package com.sawyer.easypgp;

import java.io.InputStream;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import android.os.AsyncTask;
import android.util.Log;


public class GmailInbox extends AsyncTask<Void, Void, Void>{

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


   public void read() {
      Properties props = System.getProperties();
      props.setProperty("mail.store.protocol", "imaps");
      props.setProperty("mail.imaps.host", "imaps.gmail.com");
      props.put("mail.imaps.auth", "true");
      props.put("mail.imaps.port", "993");
      props.put("mail.imaps.socketFactory.port", "993");
      props.put("mail.imaps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
      props.put("mail.imaps.socketFactory.fallback", "false");
      props.setProperty("mail.imaps.quitwait",  "false");

      
      try {
    	 Log.i("Emailer", "Just started read()");
         Session session = Session.getDefaultInstance(props, null);

         Log.i("Emailer", "Before store is created.");
         Store store = session.getStore("imaps");
         Log.i("Emailer", "After store is created.");

         store.connect("imap.gmail.com", "michaelsawyer92@gmail.com",
               "wogywimdjubybtnk");
         Log.i("Emailer", "After store is connected to gmail.");


         Folder inbox = store.getFolder("inbox");
         inbox.open(Folder.READ_ONLY);
         int messageCount = inbox.getMessageCount();

         //Message[] messages = inbox.getMessages();
         Log.i("Emailer", "------------------------------");
         Log.i("Emailer", "Total Messages:- " + messageCount);
         inbox.close(true);
         store.close();
         //System.exit(0);
      } catch(NoSuchProviderException e) {
         Log.d("Crap", "No Such Provider...");
      } catch (MessagingException e) {
    	  e.printStackTrace();
      } catch (Exception e) {
         e.toString();
      }
   }


@Override
protected Void doInBackground(Void... params) {
	read();
	return null;
}
}
