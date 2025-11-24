package com.api_3.api_3.dto.response;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import lombok.Data;

import java.time.Instant;
import java.util.Date;

@Data
public class CalendarResponse {

    private String id;
    private String summary;
    private String description;
    private Instant start;
    private Instant end;
    private String htmlLink;

    public static CalendarResponse fromEvent(Event event) {
        CalendarResponse dto = new CalendarResponse();

        dto.setId(event.getId());
        dto.setSummary(event.getSummary());
        dto.setDescription(event.getDescription());
        dto.setHtmlLink(event.getHtmlLink());

        if (event.getStart() != null) {
            if (event.getStart().getDateTime() != null) {
                dto.setStart(Instant.ofEpochMilli(event.getStart().getDateTime().getValue()));
            } else if (event.getStart().getDate() != null) {
                dto.setStart(Instant.ofEpochMilli(event.getStart().getDate().getValue()));
            }
        }

        if (event.getEnd() != null) {
            if (event.getEnd().getDateTime() != null) {
                dto.setEnd(Instant.ofEpochMilli(event.getEnd().getDateTime().getValue()));
            } else if (event.getEnd().getDate() != null) {
                dto.setEnd(Instant.ofEpochMilli(event.getEnd().getDate().getValue()));
            }
        }

        return dto;
    }

    public Event toEvent() {
        Event event = new Event();

        event.setId(this.id);
        event.setSummary(this.summary);
        event.setDescription(this.description);

        // START
        if (this.start != null) {
            EventDateTime startTime = new EventDateTime();
            startTime.setDateTime(new com.google.api.client.util.DateTime(Date.from(this.start)));
            event.setStart(startTime);
        }

        // END
        if (this.end != null) {
            EventDateTime endTime = new EventDateTime();
            endTime.setDateTime(new com.google.api.client.util.DateTime(Date.from(this.end)));
            event.setEnd(endTime);
        }

        return event;
    }
}
