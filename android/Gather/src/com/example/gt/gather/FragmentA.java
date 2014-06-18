package com.example.gt.gather;

import java.util.ArrayList;

import com.example.gt.gather.FragmentA.Communicator;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentA extends Fragment{
	
	ListView list;
	static Communicator comm;
	
	String[] eventTitles, eventTimes, myTitles, myTimes;
	ArrayList<SingleRow> myList, eventsList, displayedList;
	double[][] locations = {{33.776795,-84.403817}, {33.775600,-84.404096}, {33.774530,-84.395770}, {33.787442,-84.373754}, {33.773843,-84.398838}, {33.751455,-84.385041}};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_a, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		comm = (Communicator) getActivity();
		list = (ListView) getActivity().findViewById(R.id.listView1);
		
		eventsList = new ArrayList<SingleRow>();
		Resources res = getResources();
		String[] eventTitles = res.getStringArray(R.array.eventTitles);
//		String[] eventTimes = res.getStringArray(R.array.eventTimes);
		String[] eventPlayers = res.getStringArray(R.array.eventPlayers);
		int[] eventImages ={R.drawable.football_icon, R.drawable.basketball_icon, R.drawable.book_icon, R.drawable.tennis_icon, R.drawable.chess_knight_icon, R.drawable.poker_icon};
				
		for (int i=0;i<6;i++)
		{
			eventsList.add(new SingleRow(eventTitles[i], eventPlayers[i], eventImages[i]));
			comm.populateMap(i);
		}
		final MyAdapter adapter = new MyAdapter(getActivity(), eventsList, comm, locations);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				comm.moveCameraToLocation(position);		
			}
		});
	}
	
	public interface Communicator {
		public void moveCameraToLocation(int eventNo);
		public void populateMap(int pos);
	}
	
	public void setCommunicator(Communicator c)
	{
		comm = c;
	}
}

class SingleRow
{
	String title, numPlayers;
	int image;
	SingleRow(String title, String players, int image)
	{
		this.title = title;
		this.image = image;
		this.numPlayers = players;
	}
}

class  MyAdapter extends BaseAdapter {
	Context context;
	ArrayList<SingleRow> myList;
		
	MyAdapter(Context c, ArrayList<SingleRow> list, Communicator comm, double[][] locations)
	{   
		context = c;
		myList = list;
	}
	
	class MyViewHolder
	{
		ImageView myImage;
		TextView myTitle;
		TextView numPlayers;
		Button joinBtn;
		MyViewHolder(View v)
		{
			myImage = (ImageView) v.findViewById(R.id.eventImage);
			myTitle = (TextView) v.findViewById(R.id.eventTitleText);
			numPlayers = (TextView) v.findViewById(R.id.numPlayersText);
			joinBtn = (Button) v.findViewById(R.id.joinButton);
		}
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent){
		View row = convertView;
		MyViewHolder holder = null;
		if (row == null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.single_row, parent,false);
			holder = new MyViewHolder(row);
			row.setTag(holder);
//			Log.d("JMK", "Creating a new row");
		}
		else
		{
			holder = (MyViewHolder) row.getTag();
//			Log.d("JMK", "Recycling stuff");
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

