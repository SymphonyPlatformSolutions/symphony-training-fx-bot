package com.symphony.certification.fxbot.command;

import com.symphony.bdk.bot.sdk.command.CommandHandler;
import com.symphony.bdk.bot.sdk.command.model.BotCommand;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyMessage;
import com.symphony.certification.fxbot.command.dataservice.DataService;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class ShowWatchlistCommandHandler extends CommandHandler {

  private DataService dataService;

  public ShowWatchlistCommandHandler(DataService dataService){
    this.dataService = dataService;
  }

  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@"+ getBotName() + " /showWatchlist")
        .asPredicate();
  }
  
  /**
   * Invoked when command matches
   */
  @Override
  public void handle(BotCommand command, SymphonyMessage response) {

    response.setEnrichedTemplateFile("quotes", this.dataService.getQuotes(),
            "com.symphony.ms.currencyQuote", this.dataService.getQuotes(), "1.0");
  }
}
