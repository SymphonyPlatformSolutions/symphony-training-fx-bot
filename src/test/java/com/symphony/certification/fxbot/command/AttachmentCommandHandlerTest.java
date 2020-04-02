package com.symphony.certification.fxbot.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.symphony.certification.fxbot.command.AttachmentCommandHandler;
import com.symphony.bdk.bot.sdk.command.model.BotCommand;
import com.symphony.bdk.bot.sdk.event.model.MessageAttachment;
import com.symphony.bdk.bot.sdk.event.model.MessageAttachmentFile;
import com.symphony.bdk.bot.sdk.event.model.MessageEvent;
import com.symphony.bdk.bot.sdk.symphony.MessageClient;
import com.symphony.bdk.bot.sdk.symphony.exception.SymphonyClientException;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyMessage;

@ExtendWith(MockitoExtension.class)
public class AttachmentCommandHandlerTest {

  @Mock
  private MessageClient messageClient;

  @InjectMocks
  private AttachmentCommandHandler attachmentCommandHandler;

  @Test
  public void shouldSetNoAttachment() {
    MessageEvent messageEvent = new MessageEvent();
    messageEvent.setUserId(123L);
    BotCommand command = mock(BotCommand.class);
    when(command.getMessageEvent()).thenReturn(messageEvent);
    SymphonyMessage commandResponse = new SymphonyMessage();

    attachmentCommandHandler.handle(command, commandResponse);

    assertNotNull(commandResponse);
    assertEquals("<mention uid=\"123\"/> message has no attachment",
        commandResponse.getMessage());
    assertNull(commandResponse.getAttachments());
  }

  @Test
  public void shouldSetOneAttachment() throws SymphonyClientException {
    MessageEvent message = new MessageEvent();
    message.setUserId(123L);
    message.setAttachments(Collections.singletonList(new MessageAttachment()));
    BotCommand command = mock(BotCommand.class);
    when(command.getMessageEvent()).thenReturn(message);
    SymphonyMessage commandResponse = new SymphonyMessage();
    when(messageClient.downloadMessageAttachments(any(MessageEvent.class)))
        .thenReturn(Collections.singletonList(mock(MessageAttachmentFile.class)));

    attachmentCommandHandler.handle(command, commandResponse);

    assertNotNull(commandResponse);
    assertEquals("<mention uid=\"123\"/> message has 1 attachment(s):",
        commandResponse.getMessage());
    assertNotNull(commandResponse.getAttachments());
    assertEquals(1, commandResponse.getAttachments().size());
  }

  @Test
  public void shouldSetManyAttachment() throws SymphonyClientException {
    MessageEvent message = new MessageEvent();
    message.setUserId(123L);
    message.setAttachments(Arrays.asList(
        new MessageAttachment(),
        new MessageAttachment(),
        new MessageAttachment()));
    BotCommand command = mock(BotCommand.class);
    when(command.getMessageEvent()).thenReturn(message);
    SymphonyMessage commandResponse = new SymphonyMessage();
    when(messageClient.downloadMessageAttachments(any(MessageEvent.class))).thenReturn(
        Arrays.asList(
            mock(MessageAttachmentFile.class),
            mock(MessageAttachmentFile.class),
            mock(MessageAttachmentFile.class)));

    attachmentCommandHandler.handle(command, commandResponse);

    assertNotNull(commandResponse);
    assertEquals("<mention uid=\"123\"/> message has 3 attachment(s):",
        commandResponse.getMessage());
    assertNotNull(commandResponse.getAttachments());
    assertEquals(3, commandResponse.getAttachments().size());
  }

}
