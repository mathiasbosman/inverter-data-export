package be.mathiasbosman.inverterdataexport.controller;

import be.mathiasbosman.inverterdataexport.domain.BatteryService;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller used to manually trigger an export.
 */
@Profile("test")
@RestController
@RequiredArgsConstructor
@RequestMapping("/rest/battery")
public class BatteryController {

  private final BatteryService batteryService;

  /**
   * Enables grid charging for the given settings.
   *
   * @param batteryId     The unique identifier of the battery
   * @param start         Start time of the period in which to charge from the grid
   * @param end           End time of said period
   * @param maxPercentage Maximum of percentage to charge
   * @return {@link ResponseEntity} of ok or exception if any
   */
  @PostMapping("/settings/gridCharge")
  public ResponseEntity<Void> setGridCharge(
      @RequestParam("identifier") String batteryId,
      @RequestParam("start") @DateTimeFormat(iso = ISO.TIME) LocalTime start,
      @RequestParam("end") @DateTimeFormat(iso = ISO.TIME) LocalTime end,
      @RequestParam("maxPercentage") int maxPercentage
  ) {
    batteryService.setGridCharging(batteryId, start, end, maxPercentage);
    return ResponseEntity.ok().build();
  }
}
