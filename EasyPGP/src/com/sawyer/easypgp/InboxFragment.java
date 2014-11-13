package com.sawyer.easypgp;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

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
import android.widget.ListView;

public class InboxFragment extends Fragment {

  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Define the xml file for this fragment
    View view = inflater.inflate(R.layout.fragment_inbox, container, false);
    new RetrieveEmails();
    return view;
  }

  private class RetrieveEmails extends AsyncTask<Void, Void, Message[]> {

    Store store = null;

    public RetrieveEmails() {
      this.execute();
    }

    public String[] print10Subjects(Message[] messages, int messageCount) {
      String[] list = new String[10];
      for (int i = messageCount - 1, listI = 0; i > messageCount - 11; i--, listI++) {
        try {
          list[listI] = messages[i].getSubject();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      return list;
    }

    public String[] print10Bodies(Message[] messages, int messageCount) {
      String[] list = new String[10];
      for (int i = messageCount - 1, listI = 0; i > messageCount - 11; i--, listI++) {
        try {
          list[listI] = (String) messages[i].getContent().toString();
        } catch (Exception e) {
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
        String[] subjects = print10Subjects(messages,
            messages.length);
        final String[] bodies = print10Bodies(messages,
            messages.length);
        for (int i = 0; i < subjects.length; i++)
          list.add(subjects[i]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
            android.R.layout.simple_list_item_1, list);
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

}
