package com.symphony.certification.fxbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.symphony.bdk.bot.sdk.BotBootstrap;

/**
 * Symphony MS Bot
 */
@SpringBootApplication
@Import(BotBootstrap.class)
public class BotApplication {

  public static void main(String[] args) {
    SpringApplication.run(BotApplication.class, args);
  }

}
