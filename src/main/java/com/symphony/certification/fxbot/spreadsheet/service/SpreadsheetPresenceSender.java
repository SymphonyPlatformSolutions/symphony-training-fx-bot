package com.symphony.certification.fxbot.spreadsheet.service;

import java.util.concurrent.atomic.AtomicLong;

import com.symphony.certification.fxbot.spreadsheet.model.SpreadsheetEvent;
import com.symphony.bdk.bot.sdk.sse.SsePublisher;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyUser;

import lombok.Builder;
import lombok.Getter;

/**
 * Sends presence events
 */
@Builder
public class SpreadsheetPresenceSender {
  private static final String SPREADSHEET_PRESENCE_EVENT = "spreadsheetPresenceEvent";

  @Getter private SymphonyUser user;
  @Getter private String streamId;
  private SsePublisher<SpreadsheetEvent> publisher;
  @Getter private long timeLastEvent;

  /**
   * Sends a presence event
   *
   * @param eventId the event id to send to
   */
  public void send(AtomicLong eventId) {
    publisher.publishEvent(buildPresenceEvent(streamId, user, eventId.getAndIncrement()));
    timeLastEvent = System.currentTimeMillis();
  }

  private SpreadsheetEvent buildPresenceEvent(String streamId, SymphonyUser user, long id) {
    return SpreadsheetEvent.builder()
        .type(SPREADSHEET_PRESENCE_EVENT)
        .id(Long.toString(id))
        .streamId(streamId)
        .userId(Long.toString(user.getUserId()))
        .user(user)
        .build();
  }

}
