package com.symphony.certification.fxbot.spreadsheet.model;

import lombok.Data;

/**
 * Model representing a spreadsheet cell
 *
 * @author Gabriel Berberian
 */
@Data
public class SpreadsheetCell {

  private String key;
  private Object value;
  private Object expr;
}
