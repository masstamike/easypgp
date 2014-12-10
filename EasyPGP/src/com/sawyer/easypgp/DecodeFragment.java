package com.sawyer.easypgp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DecodeFragment extends Fragment {

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Define the xml file for this fragment
    View view = inflater.inflate(R.layout.fragment_decode, container, false);

    // Setup handles to view objects here
    // etFoo = (EditText) view.findViewById(R.id.etFoo);

    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    // Set title
    getActivity().getActionBar().setTitle(
        R.string.title_activity_decode_fragment);
  }
}
