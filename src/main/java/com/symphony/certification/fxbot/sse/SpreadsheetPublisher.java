package com.symphony.certification.fxbot.sse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.symphony.certification.fxbot.spreadsheet.model.SpreadsheetEvent;
import com.symphony.certification.fxbot.spreadsheet.service.SpreadsheetPresenceService;
import com.symphony.bdk.bot.sdk.sse.SsePublisher;
import com.symphony.bdk.bot.sdk.sse.SseSubscriber;
import com.symphony.bdk.bot.sdk.sse.model.SseEvent;
import com.symphony.bdk.bot.sdk.sse.model.SubscriptionEvent;
import com.symphony.bdk.bot.sdk.symphony.ConfigClient;
import com.symphony.bdk.bot.sdk.symphony.UsersClient;
import com.symphony.bdk.bot.sdk.symphony.exception.SymphonyClientException;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyUser;

/**
 * Sample code. Simple SsePublisher which waits for spreadsheet update events to send to the
 * clients.
 *
 * @author Gabriel Berberian
 */
public class SpreadsheetPublisher extends SsePublisher<SpreadsheetEvent> {
  private static final Logger LOGGER = LoggerFactory.getLogger(SpreadsheetPublisher.class);
  private static final long WAIT_INTERVAL = 1000L;

  private final UsersClient usersClient;
  private final SpreadsheetPresenceService presenceService;
  private final ConfigClient configClient;

  public SpreadsheetPublisher(UsersClient usersClient, SpreadsheetPresenceService presenceService,
      ConfigClient configClient) {
    this.usersClient = usersClient;
    this.presenceService = presenceService;
    this.configClient = configClient;
  }

  @Override
  public List<String> getEventTypes() {
    return Stream.of("spreadsheetUpdateEvent", "spreadsheetPresenceEvent")
        .collect(Collectors.toList());
  }

  @Override
  public void handleEvent(SseSubscriber subscriber, SpreadsheetEvent event) {
    String subscriberStreamId = subscriber.getMetadata().get("streamId");
    String eventStreamId = event.getStreamId();
    if (subscriberStreamId == null || eventStreamId == null || subscriberStreamId.equals(
        eventStreamId)) {
      LOGGER.debug("Sending updates to user {}", subscriber.getUserId());
      subscriber.sendEvent(SseEvent.builder()
          .id(event.getId())
          .retry(WAIT_INTERVAL)
          .event(event.getType())
          .data(event)
          .build());
    }
  }

  @Override
  protected void onSubscriberAdded(SubscriptionEvent subscriberAddedEvent) {
    String streamId = subscriberAddedEvent.getMetadata().get("streamId");
    Long userId = subscriberAddedEvent.getUserId();

    SymphonyUser user = getUserById(userId);
    if (configClient.getPodBaseUrl() != null && !configClient.getPodBaseUrl().isEmpty()) {
      completeAvatarUrls(user);
    }

    presenceService.beginSending(this, streamId, user);
  }

  @Override
  protected void onSubscriberRemoved(SubscriptionEvent subscriberRemovedEvent) {
    String streamId = subscriberRemovedEvent.getMetadata().get("streamId");
    Long userId = subscriberRemovedEvent.getUserId();

    presenceService.finishSending(streamId, userId);
  }

  private SymphonyUser getUserById(long userId) {
    try {
      SymphonyUser user = usersClient.getUserFromId(userId, true);
      return user != null ? user : usersClient.getUserFromId(userId, false);
    } catch (SymphonyClientException e) {
      LOGGER.error("Exception getting user by id {}", userId);
      return null;
    }
  }

  private void completeAvatarUrls(SymphonyUser user) {
    user.getAvatars().forEach(userAvatar -> {
      userAvatar.setUrl(completeAvatarUrl(userAvatar.getUrl()));
    });
  }

  private String completeAvatarUrl(String url) {
    return url.replace("..", configClient.getPodBaseUrl());
  }

}
