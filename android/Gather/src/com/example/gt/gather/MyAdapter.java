package com.example.gt.gather;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gt.gather.MyAdapter.MyViewHolder;

public class MyAdapter extends BaseAdapter {
	Context context;
	ArrayList<SingleRow> myList;

	MyAdapter(Context c, ArrayList<SingleRow> list, double[][] locations) {
		context = c;
		myList = list;
	}

	class MyViewHolder {
		ImageView myImage;
		TextView myTitle;
		TextView numPlayers;
		Button joinBtn;

		MyViewHolder(View v) {
			myImage = (ImageView) v.findViewById(R.id.eventImage);
			myTitle = (TextView) v.findViewById(R.id.eventTitleText);
			numPlayers = (TextView) v.findViewById(R.id.numPlayersText);
			joinBtn = (Button) v.findViewById(R.id.joinButton);
		}
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = convertView;
		MyViewHolder holder = null;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.single_row, parent, false);
			holder = new MyViewHolder(row);
			row.setTag(holder);
			// Log.d("JMK", "Creating a new row");
		} else {
			holder = (MyViewHolder) row.getTag();
			// Log.d("JMK", "Recycling stuff");
		}

		SingleRow temp = myList.get(position);

		holder.myImage.setImageResource(temp.image);
		holder.myTitle.setText(temp.title);
		holder.numPlayers.setText(temp.numPlayers);

		holder.joinBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, JoinActivity.class);
				intent.putExtra("index", position);
				context.startActivity(intent);
			}
		});

		return row;
	}

	@Override
	public int getCount() {
		return myList.size();
	}

	@Override
	public Object getItem(int position) {
		return myList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
