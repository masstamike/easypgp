package com.sawyer.easypgp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sawyer.adapters.EmailArrayAdapter;

public class InboxFragment extends Fragment {

  String[] bodies = null;
  String[] list = null;
  String[] senders = null;
  boolean[] types = null;

  private static class EmailHolder {
    public TextView emailSenderView;
    public TextView emailSubjectView;
    public ImageView img;
  }

  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Define the xml file for this fragment
    View view = inflater.inflate(R.layout.fragment_inbox, container, false);
    File file = new File("emailList");
    if (file.exists()) {
      onResume();
    } else {
      new RetrieveEmails();
    }
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
            list[listI] = getStringFromInputStream(m_mess[i].getContent());
          } else {
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
        Session session = Session.getInstance(props, null);

        store = session.getStore("imaps");

        store.connect("imap.gmail.com", "michaelsawyer92@gmail.com",
            "wogywimdjubybtnk");

        inbox = store.getFolder("inbox");
        inbox.open(Folder.READ_ONLY);
        int messageCount = inbox.getMessageCount();

        Log.i("Emailer", "------------------------------");
        Log.i("Emailer", "Total Messages:- " + messageCount);
        Log.i("Emailer", "------------------------------");
        tenMessages = print10Subjects(inbox.getMessages(),
            inbox.getMessageCount());
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
        ArrayList<String> list = new ArrayList<String>();
        ArrayList<String> sender_list = new ArrayList<String>();
        String[] subjects = print10Subjects(messages, messages.length);
        for (int i = 0; i < subjects.length; i++) {
          //list.add(subjects[i]);
          list.add(senders[i]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
            R.layout.email_row_layout, R.id.subject, list);
        lv.setAdapter(adapter);
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
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    String filename = "emailList";
    FileOutputStream outputStream;

    try {
      outputStream = getActivity()
          .openFileOutput(filename, Context.MODE_APPEND);
      for (String s : list) {
        outputStream.write(s.getBytes());
      }
      outputStream.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    String[] emailList = {};

    try {
      getActivity().openFileInput("emailList");
      Log.d("EasyPGP", "Before ObjectInputStream");
      ObjectInputStream ois = new ObjectInputStream(getActivity()
          .openFileInput("emailList"));
      Log.d("EasyPGP", "After ObjectInputStream");
      emailList = (String[]) ois.readObject();
      Log.d("Email Inbox", "Email inbox cached contains:");
      for (int i = 0; i < emailList.length; i++) {
        Log.d("Email Inbox", emailList[i]);
      }
    } catch (StreamCorruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // new FileInputStream(getActivity().openFileInput("EmailList"));
    catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
