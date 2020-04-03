package com.symphony.certification.fxbot.notification;

import com.symphony.bdk.bot.sdk.lib.jsonmapper.JsonMapper;
import com.symphony.bdk.bot.sdk.notification.NotificationInterceptor;
import com.symphony.bdk.bot.sdk.notification.model.NotificationRequest;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Sample code. Demonstrates how to extend {@link NotificationInterceptor} to
 * process incoming requests. Refer to the CreateNotificationCommandHandler
 * sample code to see how to generate incoming requests.
 *
 */
public class SimpleNotificationInterceptor extends NotificationInterceptor {
  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleNotificationInterceptor.class);

  private JsonMapper jsonMapper;

  public SimpleNotificationInterceptor(JsonMapper jsonMapper) {
    this.jsonMapper = jsonMapper;
  }

  /**
   * Invoked by InterceptorChain on incoming requests
   */
  @Override
  public boolean process(NotificationRequest notificationRequest,
      SymphonyMessage notificationMessage) {
    LOGGER.debug("Notification received");

    // For simplicity of this sample code identifier == streamId
    String streamId = notificationRequest.getIdentifier();

    if (streamId != null) {
      notificationRequest.setStreamId(streamId);
      Map<String, String> data = jsonMapper.toObject(notificationRequest.getPayload(), Map.class);
      notificationMessage.setTemplateFile("fx-notification", data);
      return true; // true if notification interception chain should continue
    }

    return false; // false if notification interception chain should be stopped and notification request rejected
  }

}
