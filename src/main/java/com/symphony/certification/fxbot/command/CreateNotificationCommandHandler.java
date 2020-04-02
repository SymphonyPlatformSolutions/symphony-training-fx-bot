package com.symphony.certification.fxbot.command;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;

import com.symphony.bdk.bot.sdk.command.CommandHandler;
import com.symphony.bdk.bot.sdk.command.model.BotCommand;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyMessage;

/**
 * Sample code for a CommandHandler that generates instructions on how to receive notifications from
 * external systems.
 */
public class CreateNotificationCommandHandler extends CommandHandler {

  private static final String NOTIFICATION_PATH = "/notification";
  private static final String NOTIFICATION_BASE_URL = "http://localhost:8080";

  @Value("${server.servlet.context-path}")
  private String servletContext;

  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@" + getBotName() + " /create notification$")
        .asPredicate();
  }

  /**
   * Invoked when command matches
   */
  @Override
  public void handle(BotCommand command, SymphonyMessage commandResponse) {
    String notificationUrl = NOTIFICATION_BASE_URL + servletContext
    		+ NOTIFICATION_PATH + "/" + command.getMessageEvent().getStreamId();

    Map<String, String> data = new HashMap<>();
    data.put("notification_url", notificationUrl);

    commandResponse.setTemplateFile("create-notification", data);
  }

}
