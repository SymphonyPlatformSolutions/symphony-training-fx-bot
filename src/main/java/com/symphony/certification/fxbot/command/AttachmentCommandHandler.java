package com.symphony.certification.fxbot.command;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.symphony.bdk.bot.sdk.command.CommandHandler;
import com.symphony.bdk.bot.sdk.command.model.BotCommand;
import com.symphony.bdk.bot.sdk.event.model.MessageAttachmentFile;
import com.symphony.bdk.bot.sdk.symphony.MessageClient;
import com.symphony.bdk.bot.sdk.symphony.exception.SymphonyClientException;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyMessage;

/**
 * Sample code for CommandHandler that highlights the attachments of a Symphony message
 */
public class AttachmentCommandHandler extends CommandHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(AttachmentCommandHandler.class);

  private final MessageClient messageClient;

  public AttachmentCommandHandler(MessageClient messageClient) {
    this.messageClient = messageClient;
  }

  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@" + getBotName() + " /attachments?$")
        .asPredicate();
  }

  @Override
  public void handle(BotCommand command, SymphonyMessage commandResponse) {
    List<MessageAttachmentFile> attachments = null;
    try {
      attachments = messageClient.downloadMessageAttachments(command.getMessageEvent());
    } catch (SymphonyClientException sce) {
      LOGGER.error("SymphonyClientException thrown on AttachmentCommandHandler", sce);
    }
    int size = attachments == null ? 0 : attachments.size();
    if (size <= 0) {
      commandResponse.setMessage("<mention uid=\"" + command.getMessageEvent().getUserId()
          + "\"/> message has no attachment");
    } else {
      commandResponse.setMessage(
          "<mention uid=\"" + command.getMessageEvent().getUserId() + "\"/> message has " + size
              + " attachment(s):");
      commandResponse.setAttachments(attachments);
    }
  }

}
