package com.symphony.certification.fxbot.command;

import com.symphony.bdk.bot.sdk.command.CommandHandler;
import com.symphony.bdk.bot.sdk.command.model.BotCommand;
import com.symphony.bdk.bot.sdk.lib.restclient.RestClient;
import com.symphony.bdk.bot.sdk.lib.restclient.model.RestResponse;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyMessage;
import com.symphony.certification.fxbot.command.dataservice.DataService;
import com.symphony.certification.fxbot.command.model.InternalQuote;
import com.symphony.certification.fxbot.command.model.QuoteResponse;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class AddToWatchlistCommandHandler extends CommandHandler {

  //NOTE: The following QUOTE_URL leverages a third-party API (AlphaVantage). Please consider any security concerns for the following
  //network request.  Additionally, this leverages a personal API key. To use, replace the apikey with your own.  Additionally, you may use the
  // @Value("${samples.quote-command.api-key}") annotation, by adding your key to the application.yaml file.
  private static final String QUOTE_URL = "https://www.alphavantage.co/query?function=CURRENCY_EXCHANGE_RATE&from_currency=USD"
          + "&to_currency=%s&apikey=C7G0Q2QOJ80OECGM";


  private RestClient restClient;
  private DataService dataService;
  private static final String ADD_QUOTE_COMMAND = "/addToWatchlist";

  public AddToWatchlistCommandHandler(RestClient restClient, DataService dataService){
    this.restClient = restClient;
    this.dataService = dataService;
  }
  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@"+ getBotName() + " " + ADD_QUOTE_COMMAND)
        .asPredicate();
  }
  /**
   * Invoked when command matches
   */
  @Override
  public void handle(BotCommand command, SymphonyMessage response) {
    Optional<String> currency = getCommandCurrency(command.getMessageEvent().getMessage());
    if (currency.isPresent()) {
      RestResponse<QuoteResponse> restResponse = requestQuote(currency.get());
      if (restResponse.getStatus() == 200){
        QuoteResponse quoteResponse = restResponse.getBody();
        InternalQuote iQuote = new InternalQuote(quoteResponse.getQuote());
        this.dataService.addQuote(iQuote);
        response.setEnrichedTemplateFile("add-quote", iQuote, "com.symphony.ms.currencyQuote",
                iQuote, "1.0");
      }
    }
    else {
      response.setMessage("Please provide the currency you want a quote for");
    }
  }
  private Optional<String> getCommandCurrency(String commandMessage){
    String[] commandSplit = commandMessage.split(" " + ADD_QUOTE_COMMAND + " ");
    if (commandSplit.length > 1) {
      String currency = commandSplit[1];
      if (currency != null){
        return Optional.of(currency);
      }
    }
    return Optional.empty();
  }

  private RestResponse<QuoteResponse> requestQuote(String currency){
    return restClient.getRequest(
            String.format(QUOTE_URL, currency), QuoteResponse.class);
  }

}
