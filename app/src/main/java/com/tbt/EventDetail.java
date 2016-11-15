package com.tbt;

/**
 * Created by bradley on 26-08-2016.
 */
public class EventDetail {
    private String eventName, eventDetails, eventStatus;

    public EventDetail(String name, String details, String status) {
        eventName = name;
        eventDetails = details;
        eventStatus = status;
    }

    String getEventName() {
        return eventName;
    }

    String getEventDetails() {
        return eventDetails;
    }

    String getEventStatus() {
        return eventStatus;
    }
}
