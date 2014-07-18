package com.example.gt.gather;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is a generic way of backing an Android ListView with a Firebase location.
 * It handles all of the child events at the given Firebase location. It marshals received data into the given
 * class type. Extend this class and provide an implementation of <code>populateView</code>, which will be given an
 * instance of your list item layout and an instance your class that holds your data. Simply populate the view however
 * you like and this class will handle updating the list as the data changes.
 * @param <T> The class type to use as a model for the data contained in the children of the given Firebase location
 */
public abstract class FirebaseEventListAdapter extends BaseAdapter {

    private Query ref;
    private int layout;
    private LayoutInflater inflater;
    private List<Event> events;
    private Map<String, Event> eventNames;
    private List<String> eventIds;
    private ChildEventListener listener;
    private User myUser;


    /**
     * @param ref The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *            combination of <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>,
     * @param modelClass Firebase will marshall the data at a location into an instance of a class that you provide
     * @param layout This is the layout used to represent a single list item. You will be responsible for populating an
     *               instance of the corresponding view with the data from an instance of modelClass.
     * @param activity The activity containing the ListView
     */
    public FirebaseEventListAdapter(Query ref, int layout, Activity activity, User user) {
        this.ref = ref;
        this.layout = layout;
        myUser = user;
        inflater = activity.getLayoutInflater();
        events = new ArrayList<Event>();
        eventNames = new HashMap<String, Event>();
        eventIds = new ArrayList<String>();
        // Look for all child events. We will then map them to our own internal ArrayList, which backs ListView
        setListener();
    }
    
    public void setListener(){
    	listener = ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                Event event = dataSnapshot.getValue(Event.class);
                String eventName = dataSnapshot.getName();
                eventNames.put(eventName, event);
                
                // check that it is within the user's radius
                if(myUser.getCoords() != null)
                {
                	double[] myCoords = myUser.getCoords();
                	double distance = event.findDistance(myCoords[0], myCoords[1]);
                	if (distance <= myUser.getRadius())
                	{           

		                // Insert into the correct location, based on previousChildName
		                if (previousChildName == null) {
		                    events.add(0, event);
		                    eventIds.add(0,eventName);
		                } else {
		                    Event previousEvent = eventNames.get(previousChildName);
		                    int previousIndex = events.indexOf(previousEvent);
		                    int nextIndex = previousIndex + 1;
		                    if (nextIndex == events.size()) {
		                        events.add(event);
		                        eventIds.add(eventName);
		                    } else {
		                        events.add(nextIndex, event);
		                        eventIds.add(nextIndex, eventName);
		                    }
		                }
		                notifyDataSetChanged();
                	}
                }
                else
                {
                	Log.d("JMK", "Could not get user location for onChildAdded in FirebaseAdapter");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                // One of the models changed. Replace it in our list and name mapping
                String eventName = dataSnapshot.getName();
                Event oldEvent = eventNames.get(eventName);
                Event newEvent = dataSnapshot.getValue(Event.class);
                int index = events.indexOf(oldEvent);
                
                // check that new event is within the user's radius
                double[] myCoords = myUser.getCoords();
                if(myCoords != null)
                {
                	double distance = newEvent.findDistance(myCoords[0], myCoords[1]);
                	if (distance <= myUser.getRadius())
                	{
		                events.set(index, newEvent);
		                eventIds.set(index, eventName);
		                eventNames.put(eventName, newEvent);
                	}
                	else
                	{
                		events.remove(index);
                		eventIds.remove(index);
                		eventNames.remove(eventName);
                	}
                	notifyDataSetChanged();
                }
                else
                {
                	Log.d("JMK", "Could not get location for onChildChanged in FirebaseAdapter");
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                // A model was removed from the list. Remove it from our list and the name mapping
                String eventName = dataSnapshot.getName();
                Event oldEvent = eventNames.get(eventName);
                events.remove(oldEvent);
                eventIds.remove(eventName);
                eventNames.remove(eventName);
//                onChildEvent("remove", oldModel);
                notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

                // A model changed position in the list. Update our list accordingly
                String eventName = dataSnapshot.getName();
                Event oldEvent = eventNames.get(eventName);
                Event newEvent = dataSnapshot.getValue(Event.class);
                int index = events.indexOf(oldEvent);
                events.remove(index);
                eventIds.remove(index);
                if (previousChildName == null) {
                    events.add(0, newEvent);
                    eventIds.add(0, eventName);
                } else {
                    Event previousEvent = eventNames.get(previousChildName);
                    int previousIndex = events.indexOf(previousEvent);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == events.size()) {
                        events.add(newEvent);
                        eventIds.add(eventName);
                    } else {
                        events.add(nextIndex, newEvent);
                        eventIds.add(nextIndex, eventName);
                    }
                }
                notifyDataSetChanged();
            }

			@Override
			public void onCancelled(FirebaseError arg0) {
				Log.e("FirebaseListAdapter", "Listen was cancelled, no more updates will occur");				
			}
        });
    }

    public void cleanup() {
        // We're being destroyed, let go of our listener and forget about all of the models
        ref.removeEventListener(listener);
        events.clear();
        eventNames.clear();
        eventIds.clear();
    }
    
    public void refresh() {
        // We're being destroyed, let go of our listener and forget about all of the models
    	ref.removeEventListener(listener);
    	events.clear();
        eventNames.clear();
        eventIds.clear();
        setListener();
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int i) {
        return events.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    
    public String getItemName(int i){
    	return eventIds.get(i);
    }

    class MyViewHolder {
		ImageView image;
		TextView title;
		TextView numberOfPeople;
		Button joinBtn;

		MyViewHolder(View v) {
			image = (ImageView) v.findViewById(R.id.eventImage);
			title = (TextView) v.findViewById(R.id.eventTitleText);
			numberOfPeople = (TextView) v.findViewById(R.id.numPeopleText);
			joinBtn = (Button) v.findViewById(R.id.joinButton);
		}
	}

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
    	MyViewHolder holder = null;
    	if (view == null) 
    	{
            view = inflater.inflate(layout, viewGroup, false);
            holder = new MyViewHolder(view);
			view.setTag(holder);
        }
    	else 
    	{
			holder = (MyViewHolder) view.getTag();
		}

        Event event = events.get(i);
        String eventIdName = eventIds.get(i);
        // Call out to subclass to marshall this model into the provided view
        populateView(holder, event, eventIdName);
        return view;
    }

    /**
     * Each time the data at the given Firebase location changes, this method will be called for each item that needs
     * to be displayed. The arguments correspond to the layout and modelClass given to the constructor of this class.
     *
     * Your implementation should populate the view using the data contained in the model.
     * @param v The view to populate
     * @param model The object containing the data used to populate the view
     */
    protected abstract void populateView(MyViewHolder holder, Event event, String idName);
//    protected abstract void onChildEvent(String type, T model);
}