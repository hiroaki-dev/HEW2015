package me.hiroaki.hew.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import me.hiroaki.hew.model.RealmObject.Event;

/**
 * Created by hiroaki on 2016/02/16.
 */
public class EventResponse {
	@SerializedName("events")
	public List<Event> events;

	public List<Event> getEvent() {
		return events;
	}

	public void setEvent(List<Event> events) {
		this.events = events;
	}


}
