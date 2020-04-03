package com.symphony.certification.fxbot.elements;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.symphony.bdk.bot.sdk.command.model.BotCommand;
import com.symphony.bdk.bot.sdk.elements.ElementsHandler;
import com.symphony.bdk.bot.sdk.event.model.SymphonyElementsEvent;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyMessage;

/**
 * Sample code. Implementation of {@link ElementsHandler} which renders a Symphony elements form and
 * handles its submission.
 */
public class FxelementHandler extends ElementsHandler {
  private static final String SAMPLE_INPUT_ID = "sampleInput";
  private static final String FORM_ID = "fxelement";

  /**
   * Used by CommandFilter to filter Symphony chat messages
   */
  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@"+ getBotName() + " /fxelement")
        .asPredicate();
  }

  @Override
  protected String getElementsFormId() {
    return FORM_ID;
  }

  /**
   * Invoked when command matches
   */
  @Override
  public void displayElements(BotCommand command,
      SymphonyMessage elementsResponse) {
    Map<String, String> data = new HashMap<>();
    data.put("form_id", getElementsFormId());
    elementsResponse.setTemplateFile("fxelement", data);
  }

  /**
   * Invoked when elements form is submitted
   */
  @Override
  public void handleAction(SymphonyElementsEvent event, SymphonyMessage elementsResponse) {
    Map<String, Object> formValues = event.getFormValues();
    String response = formValues.get(SAMPLE_INPUT_ID).toString();
    elementsResponse.setMessage("Form registered successfully, here's what you've typed: "+ response);
  }

}