package com.sawyer.adapters;

import com.sawyer.easypgp.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EmailArrayAdapter extends ArrayAdapter<String> {
  
  private final Context context;
  private final String[] values;
  
  public EmailArrayAdapter(Context context, String[] objects) {
    super(context, R.layout.email_row_layout, objects);
    this.context = context;
    this.values = objects;
  }
  
  @Override
  public View getView (int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View rowView = inflater.inflate(R.layout.email_row_layout, parent, false);
    TextView textView = (TextView) rowView.findViewById(R.id.sender);
    ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
    textView.setText(values[position]);
    String s = values[position];
    if (s.startsWith("Not an EasyPGP encrypted message.")) {
      imageView.setImageResource(R.drawable.ic_waves);
    } else {
      imageView.setImageResource(R.drawable.ic_launcher);
    }
    return rowView;
  }

  

}
