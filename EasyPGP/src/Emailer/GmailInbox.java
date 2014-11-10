package Emailer;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Scanner;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;

public class GmailInbox {

 public static String[] print10Messages (Message[] messages, int messageCount) {
    String[] list = new String[10];
    for(int i = messageCount-1, listI=0; i > messageCount-11; i--,listI++) {
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
  gmail.read();
 }

 public void read() {
  Properties props = new Properties();
  try {
   props.load(new FileInputStream(new File("./smtp.properties")));
   Session session = Session.getDefaultInstance(props, null);

   Store store = session.getStore("imaps");
   store.connect("smtp.gmail.com", "michaelsawyer92@gmail.com","wogywimdjubybtnk");

   Folder inbox = store.getFolder("inbox");
   inbox.open(Folder.READ_ONLY);
   int messageCount = inbox.getMessageCount();

   System.out.println("Total Messages:- " + messageCount);

   Message[] messages = inbox.getMessages();
   System.out.println("------------------------------");
   Scanner s = new Scanner(System.in);
   while (true) {
       String[] list = print10Messages (messages, messageCount);
       messageCount-=10;
       for (int emailIndex = 0; emailIndex <10; emailIndex++) {
           System.out.println(list[emailIndex]);
       }
       char input = s.next().charAt(0);
       if(input  == 'q') {
         inbox.close(true);
         store.close();
         System.exit(0);
       }
   }
  } catch (Exception e) {
   e.printStackTrace();
  }
 }
}