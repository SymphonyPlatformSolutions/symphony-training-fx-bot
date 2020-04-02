package com.symphony.certification.fxbot.command.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.symphony.bdk.bot.sdk.command.AuthenticationProvider;
import com.symphony.bdk.bot.sdk.command.model.AuthenticationContext;
import com.symphony.bdk.bot.sdk.command.model.BotCommand;
import com.symphony.bdk.bot.sdk.lib.restclient.RestClient;
import com.symphony.bdk.bot.sdk.lib.restclient.RestClientConnectionException;
import com.symphony.bdk.bot.sdk.lib.restclient.model.RestResponse;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyMessage;

/**
 * Sample code. Implementation of {@link AuthenticationProvider} to offer OAuth authentication.
 * NOTICE: it is required to change oauth properties with valid ones that match what has been
 * configured in a OAuth server.
 */
public class OAuthAuthenticationProvider implements AuthenticationProvider {

  private static final String AUTHORIZE_URL =
      "https://OAUTH-SERVER.URL.COM/oauth2/default/v1/authorize";
  private static final String TOKEN_URL = "https://OAUTH-SERVER.URL.COM/oauth2/default/v1/token";
  private static final String REDIRECT_URI = "https://localhost:8080/myproject/oauth";
  private static final String CLIENT_ID = "CLIENT-ID";
  private static final String CLIENT_SECRET = "CLIENT-SECRET";
  private static final String RESPONSE_TYPE = "code";
  private static final String SCOPE = "read-write";
  private static final String GRANT_TYPE = "authorization_code";

  private Map<String, String> userAccessTokenMap = new HashMap<>();
  private Map<Long, BotCommand> commandCache = new HashMap<>();

  private RestClient restClient;

  public OAuthAuthenticationProvider(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public AuthenticationContext getAuthenticationContext(Long userId) {
    AuthenticationContext authContext = new AuthenticationContext();
    authContext.setAuthScheme("Bearer");
    authContext.setAuthToken(findCredentialsByUserId(userId));

    return authContext;
  }

  @Override
  public void handleUnauthenticated(BotCommand command, SymphonyMessage commandResponse) {
    // Cache command to retry it once user gets authenticated
    commandCache.put(command.getMessageEvent().getUserId(), command);

    commandResponse.setMessage("Hi <b>" + command.getUser().getDisplayName()
        + "</b>.  You still don't have linked your account.<br/><br/>"
        + getLinkAccountUrl(command.getMessageEvent().getUserId()));
  }

  public void authorizeCode(String code, String userId) {
    MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
    requestBody.add("grant_type", GRANT_TYPE);
    requestBody.add("client_id", CLIENT_ID);
    requestBody.add("client_secret", CLIENT_SECRET);
    requestBody.add("redirect_uri", REDIRECT_URI);
    requestBody.add("code", code);

    RestResponse<String> response = new RestResponse<>(null, 0);
    try {
      response = restClient.postRequest(TOKEN_URL, requestBody, String.class);
    } catch (RestClientConnectionException rcce) {
      // Handle connection error
    }

    userAccessTokenMap.put(userId, getToken(response.getBody()));

    // User is now authenticated. Retry original command.
    commandCache.get(userId).retry();

  }

  // Just a simple example. Ideally, implement a service to handle credentials
  // retrieval.
  private String findCredentialsByUserId(Long userId) {
    return userAccessTokenMap.get(userId);
  }

  private String getLinkAccountUrl(Long userId) {
    String linkAccount = AUTHORIZE_URL + "?"
        + "response_type=" + RESPONSE_TYPE + "&amp;"
        + "client_id=" + CLIENT_ID + "&amp;"
        + "redirect_uri=" + REDIRECT_URI + "&amp;"
        + "scope=" + SCOPE + "&amp;"
        + "state=" + userId;

    return "<a href='" + linkAccount + "' style='"
        + "border: 1px solid #767676;"
        + "padding: 7px 14px;"
        + "border-radius: 4px;"
        + "color: #767676;"
        + "font-size: 13px;"
        + "font-weight: bold;'>Link Account</a>";

  }

  private String getToken(String response) {
    // Add logic to pull OAuth access code off OAuth server response
    return "eyJraWQiOiJyZ0V1T2xnVFBQSDJnbmhoNTA3QjlwSjZqT05iS3pNX3NmSFphZ0";
  }

}
