# Symphony Bot Template

[![Java](https://img.shields.io/badge/JDK-1.8-blue.svg)](https://www.oracle.com/technetwork/java/javase/documentation/index.html) [![Maven](https://img.shields.io/badge/MAVEN-3.0.5+-blue.svg)](https://maven.apache.org/guides/index.html)

Bot template project with code samples on how to use the features offered by Symphony Bot SDK.


## Summary

* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Defining RSA keys pair](#defining-rsa-keys-pair)  
  * [Setting the service account](#setting-the-service-account)
  * [POD configuration](#pod-configuration)
  * [Running locally](#running-locally)
  * [Verify your setup](#verify-your-setup)
* [Command handling](#command-handling)
  * [Help command](#help-command)
  * [Hello command](#hello-command)
  * [Create notification command](#create-notification-command)
  * [Login command](#login-command)
  * [Quote command](#quote-command)
  * [Attachment command](#attachment-command)
  * [Broadcast command](#broadcast-command)
  * [Default response](#default-response)
* [Event handling](#event-handling)
* [Receiving notifications](#receiving-notifications)
* [Symphony elements samples](#symphony-elements-samples)
  * [Register quote command](#register-quote-command)
  * [Template command](#template-command)
* [Plugging in an extension app](#plugging-in-an-extension-app)
  * [Streams details endpoint](#streams-details-endpoint)
  * [Users details endpoint](#users-details-endpoint)
  * [Static content](#static-content)
  * [Real-time events](#real-time-events)
 
* [Extending monitoring endpoints](#extending-monitoring-endpoints)


## Getting Started

These instructions will allow you to set up Symphony Bot Template application.


### Prerequisites

* JDK 1.8
* Maven 3.0.5+
* OpenSSL
* Service account configured in Symphony Admin portal
* Extension app setup in Symphony Admin portal (optional)


### Defining RSA keys pair

In order to register a service account in Symphony Admin Portal, a RSA keys pair is required. Optionally, you may use a pair for the bot service account and another one for your extension app.

Run through the following steps to generate your keys pair:

```bash
openssl genrsa -out "my_privatekey.pem" 4096

openssl req -newkey rsa:4096 -x509 -key "my_privatekey.pem" -out "my_publickey.cer"

openssl pkcs8 -topk8 -nocrypt -in "my_privatekey.pem" -out "my_privatekey.pkcs8"

openssl x509 -pubkey -noout -in "my_publickey.cer"  > "my_publickey.pem"

```

The application requires the pkcs8 format private key, so create a top level directory named ```/certs``` and place the private key there.  


### Setting the service account

When registering your service account (or extension app) in Symphony Admin Portal, paste the content of the public key (.pem) file, you generated in previous section, into the authentication text area. 

In addition to the RSA keys, make sure the property botUsername (and appId) in src/main/resources/bot-config.json file matches the value configured in Symphony Admin Portal.


### POD configuration

In src/main/resources/bot-config.json you will find configuration properties where you can specify the details of your 
POD. Fill out the following properties to make the application to point to your POD.

|            Property            |                Description                |
|--------------------------------|-------------------------------------------|
| sessionAuthHost                | The session authorization host URL        |
| sessionAuthPort                | The session authorization host port       |
| keyAuthHost                    | The key manager authorization host URL    |
| keyAuthPort                    | The key manager authorization host port   |
| podHost                        | The Pod host URL                          |
| podPort                        | The Pod host port                         |
| agentHost                      | The Agent host URL                        |
| agentPort                      | The Agent host port                       |
| appId                          | The extension app identification          |
| appPrivateKeyPath              | The extension app private key path        |
| appPrivateKeyName              | The extension app private key file        |
| botPrivateKeyPath              | The bot private key path                  |
| botPrivateKeyName              | The bot private key file                  |
| botUsername                    | The bot username                          |
| authTokenRefreshPeriod         | The authentication token refresh period   |
| showFirehoseErrors             | Showing Firehose errors flag              |

  
### Running locally

The Symphony Bot Template is built using the Spring Boot and uses Maven to manage the dependencies.

1st Step - Install all of the project's dependencies
```
mvn clean install
```
2st Step - Run the application using Maven and Spring Boot
```
mvn clean spring-boot:run
```
To run it in debug mode, provide the following parameters:
```
mvn clean spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
```
Bind the IDE of your choice the the specified port (e.g. 5005).


### Verify your setup

Once the application is up and running, you can check if all the setup works properly by navigating to the health check endpoint: http(s)://&lt;hostname&gt;:&lt;port&gt;/&lt;application_context&gt;/monitor/health.

It should return something like:

```javascript
{
    "status":"UP",
    "details":{
        "symphony":{
            "status":"UP",
            "details":{
                "Symphony":{
                    "agentConnection":"UP",
                    "podConnection":"UP",
                    "agentToPodConnection":"UP",
                    "agentToKMConnection":"UP",
                    "podVersion":"1.55.3",
                    "agentVersion":"2.55.9",
                    "agentToPodConnectionError":"N/A",
                    "agentToKMConnectionError":"N/A",
                    "symphonyApiClientVersion":"1.0.49"
                }
                
            }
            
        },
        "internetConnectivity":{
            "status":"UP",
            "details":{
                "connectivity":"UP"
            }            
        }       
    }    
}
```

**Notice:** You can set the application context for your application in /src/main/resources/application.yaml:

```yaml
server:
  servlet:
    context-path: "/my-botapp"
```


## Command handling

Sample commands are shipped with the Bot Template project as a way to assist you to understand the mechanics of the Symphony Bot SDK.
If your setup is properly configured to point to your POD, create an IM or chat room with the bot (search it by the display name you configured in Symphony Admin Portal).

All the sample commands require mentioning the bot (e.g. @MyBot), although you can specify any other pattern when creating your own commands.


### Help command

Displays static help message with all available commands

>&#9679; **John Doe**
>
>@MyBot /help

>&#9679; **MyBot**
>
>Bot Commands
>- **@MyBot** /hello - simple hello command
>- **@MyBot** /help - displays the list of commands
>- **@MyBot** /create notification - generates details on how to receive notification in this room
>- **@MyBot** /login - returns the HTTP authorization header required to talk to external system
>- **@MyBot** /quote BRL - returns quote for the specified currency (e.g. BRL)
>- **@MyBot** /attachment file - simply returns the file attached to the message
>- **@MyBot** /register quote - displays the currency quote registration form
>- **@MyBot** /template alert - renders predefined templates (e.g. alert, notification) based on your inputs
>- **@MyBot** /broadcast message - spreads a message to all bot active rooms


### Hello command

Simple hello world command.

>&#9679; **John Doe**
>
>@MyBot /hello

>&#9679; **MyBot**
>
>Hello, **John Doe**


### Create notification command

Returns instructions that you can use to receive notifications from external systems into the given Symphony room. To test it, submit a HTTP POST request to the returned URL.

>&#9679; **John Doe**
>
>@MyBot /create notification

>&#9679; **MyBot**
>
>| Method | Request URL |
>|--|--|
>| POST | http://localhost:8080/my-botapp/notification/GhaWqOo6jRsHv5adBv4q73___pK2eM94dA |
>
>| Header name | Header value |
>|--|--|
>| Accept | application/json |
>| Content-type | application/json |
>
>**Payload**
>
>Click to expand the sample payload


### Login command

Returns the HTTP header required to perform authenticated requests to external systems. Sample code includes two implementations of the ```AuthenticationProvider``` interface representing Basic and OAuth v2 authentication.

>&#9679; **John Doe**
>
>@MyBot /login

>&#9679; **MyBot**
>
>**User authenticated**. Please add the following HTTP header to your requests:
>
>```Authorization: Basic am9obi5kb2VAc3ltcGhvbnkuY29tOnN0cm9uZ3Bhc3M=```

By default a sample for basic authentication is returned. To test the OAuth version update the ```LoginCommandHandler``` as below:

```java
@CommandAuthenticationProvider(name = "OAuthAuthenticationProvider")
public class LoginCommandHandler extends AuthenticatedCommandHandler {

  ...

}
```


### Quote command

Relies on the RestClient library offered by the Symphony Bot SDK to request quotes for foreigner currencies on a external system.

>&#9679; **John Doe**
>
>@MyBot /quote BRL

>&#9679; **MyBot**
>
>>USD-BRL X-RATE
>>
>>**4.99**<sub>BRL</sub>


### Attachment command

Highlights message attachments manipulation. Sample code explores downloading attachments content.

>&#9679; **John Doe**
>
>@MyBot /attachment
>
>>attachment1.png (6.47 kB)
>>
>>attachment2.png (12.47 kB)

>&#9679; **MyBot**
>
>![attachment](readme/attachment.png)


### Broadcast command

Spread a message to all bot active rooms.

In the room the command was posted, bot informs to which other rooms the message was broadcast:

>&#9679; **John Doe**
>
>@MyBot /broadcast hey guys!
>
>>attachment1.png (6.47 kB)
>>
>>attachment2.png (12.47 kB)

>&#9679; **MyBot**
>
>![broadcast_command](readme/broadcast_command_response.png)

In the broadcast rooms, the message content is posted as per the example above:

>&#9679; **MyBot**
>
>![attachment](readme/broadcast_message.png)
 

### Default response

The Symphony Bot Template also ships with a mechanism for default responses which sends a default response message in Symphony chat when bot receives an unknown command.

>&#9679; **John Doe**
>
>@MyBot /make coffee

>&#9679; **MyBot**
>
>Sorry, I could not understand


## Event handling

Similarly to commands, Symphony Bot Template offers a sample code for event handling. When a user is added to a room with the bot, it sends a greeting message mentioning that person:

> Hey, @John Doe. It is good to have you here! 


## Receiving notifications

Symphony Bot SDK delivers all support to receive notifications from external systems by exposing an endpoint and offering mechanisms to process incoming requests, the notification interceptors.

A sample notification interceptor, ```SimpleNotificationInterceptor```, is shipped with the Bot Template project. It simply forwards any JSON payload received by the notification endpoint to the Symphony chat specified in URL path.

To test it follow the instructions of the create notification command. Once the POST request comes in, the JSON payload is printed in the specified Symphony room:

>&#9679; **MyBot**
>
>**Notification received:**

```javascript
{"alert": false,"title": "Something Interesting occurred!","content": {"header": "This is an example of a notification, expand to see more","body": "The SDK comes with ready-to-use message templates that you can use to render messages with your own data. You can add you own templates using the extension application."},"showStatusBar": true,"comment": {"body": "so interesting!"},"description": "this is a brief description","assignee": {"displayName": "John Doe"},"type": {"name": "sample"},"status": {"name": "Awesome"},"priority": {"name": "normal"},"labels": [{"text": "Example"},{"text": "SDK"},{"text": "MS"}]}
```

## Symphony Elements samples

The Symphony Bot Template project features two main samples on how to render interactive forms on chat rooms using Symphony Elements and how to react on such forms submissions.


### Register quote command

Explores the Symphony Elements visual components to display a form for quote registration in Symphony chat.

>&#9679; **John Doe**
>
>@MyBot /register quote

>&#9679; **MyBot**
>
>![symphony elements form](readme/quote_registration_form.png)


### Template command

Renders messages using Symphony standard templates. Supported values by this sample command are:

* simple
* alert
* information
* notification
* list
* table

>&#9679; **John Doe**
>
>@MyBot /template simple

>&#9679; **MyBot**
>
>![simple template](readme/template_simple.png)

>&#9679; **John Doe**
>
>@MyBot /template alert

>&#9679; **MyBot**
>
>![alert template](readme/template_alert.png)

>&#9679; **John Doe**
>
>@MyBot /template information

>&#9679; **MyBot**
>
>![information template](readme/template_information.png)


**Notice** how this sample leverages Symphony Elements to render interactive forms to pull data to be used when rendering messages. 


## Plugging in an extension app

Symphony Bot Template ships with few endpoints to assist you on understanding how to leverage Symphony Bot SDK to create your own extension apps. All endpoints are protected and require extension app to be authenticated.

Please refer to following sub-sections for more details.


### Streams details endpoint

| Method | URL | Description
|---|---|---|
| GET | /secure/streams | Returns a list of streams the bot is part of

**Response**

```javascript
[
  {
    "active": true,
    "crossPod": true,
    "members": [
      7215545078229
    ],
    "roomName": " my room name",
    "streamId": "iWyZBIOdQQzQj0tKOLRivX___qu6YeyZdA",
    "streamType": "ROOM"
  }
]
```


### Users details endpoint

| Method | URL | Description
|---|---|---|
| GET | /secure/users | Returns user details by username or user ID. Set either ```username``` or ```userId``` request parameter.

**Response**

```javascript
{
  "userId": 15942919536460,
  "emailAddress": "test_1@symphony.com",
  "firstName": "test_1",
  "lastName": "test",
  "displayName": "test_1 test",
  "title": "Technical Writer",
  "company": "pod232",
  "username": "test_1",
  "location": "location",
  "avatars": [
    {
      "size": "original",
      "url": "../avatars/static/150/default.png"
    }
  ]
}

```

### Static content

Under ```<symphony bot application base path>/src/main/resources/public``` you will find a sample Symphony logo (logo.svg).
 
Try accessing it from:
* browser (full path URL): ```http(s)://<hostname>:<port>/<application_context>/app/logo.svg```
* your extension app code (relative path): ```./logo.svg```


### Real-time events

Refer to the ```SimpleEventPublisher``` to check how to generate real-time events for your extension app based on the Server-Sent Event infrastructure offered by Symphony Bot SDK.


## Extending monitoring endpoints

The ```InternetConnectivityHealthIndicator``` sample shows how you can extend the default details exposed by the /monitor/health endpoint. 

