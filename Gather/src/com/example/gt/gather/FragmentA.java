package com.example.gt.gather;

import java.util.ArrayList;


import com.firebase.client.Firebase;

import android.app.Fragment;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class FragmentA extends Fragment{
	
	ListView list;
	Communicator comm;
	Firebase dataRef;
	ArrayList<SingleRow> myList;
	MyAdapter adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_a, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		list = (ListView) getActivity().findViewById(R.id.listView1);
		myList = new ArrayList<SingleRow>();
	}
	
	public void refresh(User user){
		if (dataRef != null)
		{
			adapter = new MyAdapter(dataRef.child("events").limit(15), getActivity(), R.layout.single_row, getActivity(), user);
			list.setAdapter(adapter);
			
			adapter.registerDataSetObserver(new DataSetObserver() {
				@Override
				public void onChanged() {
					super.onChanged();
					comm.clearMap();
					for(int i = 0; i<adapter.getCount(); i++)
					{
						Event event = (Event) adapter.getItem(i);
						comm.addMarker(event);
					}
				}
			});
		}
		
		list.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Event event = (Event) parent.getAdapter().getItem(position);
				comm.moveCameraToLocation(event, position);
			}
		});
	}
	
//	public void refresh(User user){
//		if (adapter!=null)	adapter.refresh();
//	}
	
	public interface Communicator {
		public void moveCameraToLocation(Event event, int position);
		public void addMarker(Event event);
		public void clearMap();
	}
	
	public void setCommunicator(Communicator c)
	{
		comm = c;
	}
	
	public void setDataBaseReference(Firebase f){
		dataRef = f;
	}
}

