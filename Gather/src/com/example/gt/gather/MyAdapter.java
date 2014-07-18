package com.example.gt.gather;

import java.util.ArrayList;

import android.app.Activity;
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
import com.firebase.client.Query;
import com.google.android.gms.maps.model.LatLng;

public class MyAdapter extends FirebaseEventListAdapter {
	
	Context context;
	
	MyAdapter(Query ref, Activity activity, int layout, Context c, User user) {
        super(ref, layout, activity, user);
        context = c;
    }
    /**
     * Bind an instance of the <code>Chat</code> class to our view. This method is called by <code>FirebaseListAdapter</code>
     * when there is a data change, and we are given an instance of a View that corresponds to the layout that we passed
     * to the constructor, as well as a single <code>Chat</code> instance that represents the current data to bind.
     * @param view A view instance corresponding to the layout we passed to the constructor.
     * @param chat An instance representing the current state of a chat message
     */
    @Override
    protected void populateView(MyViewHolder holder, final Event event, final String idName) {
        // Map an Event object to an entry in our listview
    	final String name = event.getName();
    	final int num = event.getUsers().size();
    	final int cap = event.getCapacity();
//    	holder.image.setImageResource(R.drawable.ic_launcher);
		holder.title.setText(name);
		holder.numberOfPeople.setText(""+num+" / "+cap);
		holder.joinBtn.setText("Join");
		
		holder.joinBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				double[] coords = {event.getCoords().getLatitude(), event.getCoords().getLongitude()};
				JoinEventDialog joinDialog = JoinEventDialog.newInstance(name, event.getDate(), idName, coords, num, cap);
				joinDialog.show(((Activity) v.getContext()).getFragmentManager(), "joinDialog");
			}
		});
    }
}