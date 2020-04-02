package com.symphony.certification.fxbot.spreadsheet.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.symphony.certification.fxbot.spreadsheet.model.RoomSpreadsheet;
import com.symphony.certification.fxbot.spreadsheet.model.SpreadsheetCell;
import com.symphony.certification.fxbot.spreadsheet.model.SpreadsheetRoom;
import com.symphony.certification.fxbot.spreadsheet.service.SpreadsheetService;
import com.symphony.bdk.bot.sdk.symphony.exception.SymphonyClientException;

@RestController
@RequestMapping("/secure/spreadsheet")
public class SpreadsheetController {

  private final SpreadsheetService spreadsheetService;

  public SpreadsheetController(SpreadsheetService spreadsheetService) {
    this.spreadsheetService = spreadsheetService;
  }

  /**
   * Gets all spreadsheets
   * @param userId the user's id
   * @return all spreadsheets
   */
  @GetMapping
  public ResponseEntity<Map<String, Map>> getAllSpreadsheets(
      @RequestAttribute("userId") String userId) {
    Map<String, Map> currentSpreadsheets = spreadsheetService.getSpreadsheets();
    if (currentSpreadsheets != null && !currentSpreadsheets.isEmpty()) {
      return ResponseEntity.ok().body(currentSpreadsheets);
    }
    return ResponseEntity.noContent().build();
  }

  /**
   * Gets a specific spreadsheet
   * @param userId   the user's id
   * @param streamId the id of the stream the spreadsheet belongs to
   * @return the spreadsheet
   */
  @GetMapping("{streamId}")
  public ResponseEntity<Map> getSpreadsheet(@RequestAttribute("userId") String userId,
      @PathVariable String streamId) {
    Map spreadsheet = spreadsheetService.getSpreadsheet(streamId);
    if (spreadsheet != null) {
      return ResponseEntity.ok().body(spreadsheet);
    }
    return ResponseEntity.noContent().build();
  }

  /**
   * Creates a new spreadsheet to a room. If the room already have a spreadsheet it is overwritten
   *
   * @param userId the user's id
   * @param roomSpreadsheet the new spreadsheet
   * @return the response success
   */
  @PostMapping
  public ResponseEntity postSpreadsheet(@RequestAttribute("userId") String userId,
      @RequestBody RoomSpreadsheet roomSpreadsheet) {
    spreadsheetService.addSpreadsheet(roomSpreadsheet, userId);
    return ResponseEntity.ok().build();
  }

  /**
   * Updates a spreadsheet
   *
   * @param userId   the user's id
   * @param cells    the cells to be updated
   * @param streamId the id of the room the spreadsheet belongs to
   * @return the response success
   */
  @PutMapping("{streamId}")
  public ResponseEntity putSpreadsheet(@RequestAttribute("userId") String userId,
      @RequestBody List<SpreadsheetCell> cells, @PathVariable String streamId) {
    Map spreadsheet = spreadsheetService.getSpreadsheet(streamId);
    if (spreadsheet == null) {
      return ResponseEntity.notFound().build();
    }
    spreadsheetService.putCells(cells, streamId, userId);
    return ResponseEntity.ok().build();
  }

  /**
   * Gets the rooms that can have a spreadsheet
   *
   * @param userId the user's id
   * @return the rooms with a flag signing if they have a spreadsheet
   */
  @GetMapping("rooms")
  public ResponseEntity<List<SpreadsheetRoom>> getRooms(@RequestAttribute("userId") String userId) {
    List<SpreadsheetRoom> spreadsheetRooms;
    try {
      spreadsheetRooms = spreadsheetService.getBotRooms();
    } catch (SymphonyClientException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    if (spreadsheetRooms != null && !spreadsheetRooms.isEmpty()) {
      return ResponseEntity.ok().body(spreadsheetRooms);
    }
    return ResponseEntity.noContent().build();
  }

}
