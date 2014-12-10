package com.sawyer.easypgp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SingleEmailFragment extends Fragment {


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Define the xml file for this fragment
		View view = inflater
				.inflate(R.layout.fragment_single_email, container, false);

		Bundle bundle = this.getArguments();
		String message = bundle.getString("message", "Empty Message");
		String author = bundle.getString("author", "N/A");
		TextView tv = (TextView) view.findViewById(R.id.encryptedEmail);
		tv.setText(message);
		getActivity().getActionBar().setTitle(author);
		return view;
	}
}
