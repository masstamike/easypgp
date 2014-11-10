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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

	private class RetrieveEmails extends AsyncTask<Void, Void, String[]> {
		
		public RetrieveEmails() {
			this.execute();
		}

		public String[] print10Messages(Message[] messages,
				int messageCount) {
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

		@Override
		protected String[] doInBackground(Void... arg0) {
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
		protected void onPostExecute(String[] emails) {
			super.onPostExecute(emails);
			ListView lv = (ListView) getActivity().findViewById(R.id.emailList);
			try {
				ArrayList<String> list = new ArrayList<String>();
				for (int i = 0; i < emails.length; i++)
					list.add(emails[i]);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						getActivity(), android.R.layout.simple_list_item_1,
						list);
				lv.setAdapter(adapter);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
