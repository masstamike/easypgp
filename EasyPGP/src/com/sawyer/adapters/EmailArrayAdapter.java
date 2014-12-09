package com.sawyer.adapters;

import com.sawyer.easypgp.R;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EmailArrayAdapter extends ArrayAdapter<String> {

  private final Context context;
  private final String[] senders;
  private final String[] subjects;
  private final boolean[] types;

  public EmailArrayAdapter(Context context, String[] senders,
      String[] subjects, boolean[] types) {
    super(context, R.layout.email_row_layout, senders);
    this.context = context;
    this.senders = senders;
    this.subjects = subjects;
    this.types = types;
  }
  

  private static class EmailHolder {
    public TextView emailSenderView;
    public TextView emailSubjectView;
    public ImageView img;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View rowView = inflater.inflate(R.layout.email_row_layout, null);
    EmailHolder emailHolder = new EmailHolder();
    emailHolder.emailSubjectView = (TextView) rowView.findViewById(R.id.subject);
    emailHolder.emailSenderView = (TextView) rowView.findViewById(R.id.sender);
    emailHolder.img = (ImageView) rowView.findViewById(R.id.img);
    emailHolder.emailSubjectView.setText(subjects[position]);
    emailHolder.emailSenderView.setText(senders[position]);
    rowView.setTag(emailHolder);
    boolean s = types[position];
    if (s) {
      emailHolder.img.setImageResource(R.drawable.ic_launcher);
      rowView.setBackgroundColor(Color.rgb(98, 125, 222));
    } else {
      emailHolder.img.setImageResource(R.drawable.ic_waves);
      rowView.setBackgroundColor(Color.rgb(155, 171, 229));
    }
    return rowView;
  }

}
