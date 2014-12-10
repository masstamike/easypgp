package com.sawyer.easypgp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.sawyer.adapters.EmailArrayAdapter;

public class InboxFragment extends Fragment {

  String[] bodies = null;
  String[] list = null;
  String[] senders = null;
  boolean[] types = new boolean[10];
  RetrieveEmails emails;
  String userEmail;
  String userPassword;

  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    // Define the xml file for this fragment
    View view = inflater.inflate(R.layout.fragment_inbox, container, false);
    File file = new File("emailList");
    SharedPreferences sharedPrefs = PreferenceManager
        .getDefaultSharedPreferences(getActivity());
    userEmail = sharedPrefs.getString("email", "");
    userPassword = sharedPrefs.getString("password", "");

    // if (file.exists()) {
    // onResume();
    // } else {
    emails = new RetrieveEmails();
    // }
    return view;
  }

  private class RetrieveEmails extends AsyncTask<Void, Void, Message[]> {

    Store store = null;

    public RetrieveEmails() {
      this.execute();
    }

    public String[] print10Subjects(Message[] messages, int messageCount) {
      list = new String[10];
      for (int i = messageCount - 1, listI = 0; i > messageCount - 11; i--, listI++) {
        try {
          list[listI] = messages[i].getSubject();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      return list;
    }

    private String getStringFromInputStream(Object object) {

      BufferedReader br = null;
      StringBuilder sb = new StringBuilder();

      String line;
      try {

        br = new BufferedReader(new InputStreamReader((InputStream) object));
        while ((line = br.readLine()) != null) {
          sb.append(line);
        }

      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        if (br != null) {
          try {
            br.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }

      return sb.toString();

    }

    public String[] print10Bodies(Message[] messages, int messageCount) {
      String[] list = new String[10];
      Part[] m_mess = (Part[]) messages;
      for (int i = messageCount - 1, listI = 0; i > messageCount - 11; i--, listI++) {
        try {
          Log.d("Emailer", "-------Message " + i + "---------");
          Log.d("Emailer", messages[i].getContentType());
          if (m_mess[i].isMimeType("text/plain")) {
            Log.d("EasyPGP", "True");
            list[listI] = getStringFromInputStream(m_mess[i].getContent());
            if (list[listI].startsWith("---EasyPGP Message Begin---")) {
              types[listI] = true;
            } else {
              types[listI] = false;
            }
          } else {
            types[listI] = false;
            Log.d("EasyPGP", "False");
            list[listI] = "Not an EasyPGP encrypted message.";
          }
        } catch (Exception e) {
          Log.i("Emailer", "print10Bodies...");
          e.printStackTrace();
        }
      }
      return list;
    }

    public String[] print10Senders(Message[] messages, int messageCount) {
      String[] list = new String[10];

      for (int i = messageCount - 1, listI = 0; i > messageCount - 11; i--, listI++) {
        try {
          Log.d("Emailer", "-------Message " + i + "---------");
          Log.d("Emailer", messages[i].getContentType());
          list[listI] = InternetAddress.toString(messages[i].getFrom());
        } catch (Exception e) {
          Log.i("Emailer", "print10Bodies...");
          e.printStackTrace();
        }
      }
      return list;
    }

    @Override
    protected Message[] doInBackground(Void... arg0) {
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
      Folder inbox = null;
      Message[] messages = null;

      try {
        Session session = Session.getInstance(props, new GmailAuthenticator(
            userEmail, userPassword));

        store = session.getStore("imaps");

        store.connect("imap.gmail.com", userEmail,
            userPassword );

        inbox = store.getFolder("inbox");
        inbox.open(Folder.READ_ONLY);
        int messageCount = inbox.getMessageCount();

        Log.i("Emailer", "------------------------------");
        Log.i("Emailer", "Total Messages:- " + messageCount);
        Log.i("Emailer", "------------------------------");
        list = print10Subjects(inbox.getMessages(), inbox.getMessageCount());
        messages = inbox.getMessages();
        bodies = print10Bodies(messages, messages.length);
        senders = print10Senders(messages, messages.length);
        inbox.close(true);
        store.close();

      } catch (NoSuchProviderException e) {
        e.printStackTrace();
      } catch (MessagingException e) {
        e.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      }
      return (Message[]) messages;
    }

    @Override
    protected void onPostExecute(final Message[] messages) {
      super.onPostExecute(messages);
      ListView lv = (ListView) getActivity().findViewById(R.id.emailList);
      try {
        for (int i = 0; i < 10; i++) {
          Log.d("EasyPGP", "Type[" + i + "] is " + types[i]);
        }
        EmailArrayAdapter m_adapter = new EmailArrayAdapter(getActivity(),
            senders, list, types);
        lv.setAdapter(m_adapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> adapterView, View view,
              int position, long id) {
            Fragment frag = new SingleEmailFragment();
            Bundle bundle = new Bundle();
            bundle.putString("message", bodies[position]);
            frag.setArguments(bundle);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.container, frag).addToBackStack(null).commit();
          }
        });
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    // String filename = "emailList";
    // FileOutputStream outputStream;
    //
    // try {
    // outputStream = getActivity()
    // .openFileOutput(filename, Context.MODE_APPEND);
    // for (String s : list) {
    // outputStream.write(s.getBytes());
    // }
    // outputStream.close();
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
  }

  @Override
  public void onDetach() {
    emails.cancel(true);
    super.onDetach();
  }

  @Override
  public void onResume() {
    super.onResume();
    // String[] emailList = {};
    //
    // try {
    // getActivity().openFileInput("emailList");
    // Log.d("EasyPGP", "Before ObjectInputStream");
    // ObjectInputStream ois = new ObjectInputStream(getActivity()
    // .openFileInput("emailList"));
    // Log.d("EasyPGP", "After ObjectInputStream");
    // emailList = (String[]) ois.readObject();
    // Log.d("Email Inbox", "Email inbox cached contains:");
    // for (int i = 0; i < emailList.length; i++) {
    // Log.d("Email Inbox", emailList[i]);
    // }
    // } catch (StreamCorruptedException e) {
    // e.printStackTrace();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // // new FileInputStream(getActivity().openFileInput("EmailList"));
    // catch (ClassNotFoundException e) {
    // e.printStackTrace();
    // }
  }

  class GmailAuthenticator extends Authenticator {
    String email;
    String pass;

    public GmailAuthenticator(String username, String password) {
      super();
      this.email = username;
      this.pass = password;
    }

    public PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(email, pass);
    }
  }

}
