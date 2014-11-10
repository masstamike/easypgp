package com.sawyer.easypgp;

import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import android.app.Activity;
import android.util.Log;

public class GmailInbox extends Activity{

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

   public static void main(String[] args) {
      GmailInbox gmail = new GmailInbox();
      //gmail.read(new FileInputStream(null));
   }

   public void read(InputStream is) {
      Properties props = new Properties();
      
      try {
    	 Log.i("Emailer", "Just started read()");
         props.load(is);
         Session session = Session.getDefaultInstance(props, null);

         Log.i("Emailer", "Before store is created.");
         Store store = session.getStore("imaps");
         Log.i("Emailer", "After store is created.");

         store.connect("smtp.gmail.com", "michaelsawyer92@gmail.com",
               "wogywimdjubybtnk");


         Folder inbox = store.getFolder("inbox");
         inbox.open(Folder.READ_ONLY);
         int messageCount = inbox.getMessageCount();

         Log.d("Emailer","Total Messages:- " + messageCount);

         //Message[] messages = inbox.getMessages();
         Log.i("Emailer", "------------------------------");
         System.out.println("------------------------------");
         System.out.println("Total Messages:- " + messageCount);
         inbox.close(true);
         store.close();
         System.exit(0);
      } catch(NoSuchProviderException e) {
         Log.d("Crap", "No Such Provider...");
      } catch (MessagingException e) {
         Log.d("Shoot", "Messaging Exception...");
      } catch (Exception e) {
         e.toString();
      }
   }
}
