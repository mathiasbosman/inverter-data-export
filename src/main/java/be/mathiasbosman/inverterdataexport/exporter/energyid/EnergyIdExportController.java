package be.mathiasbosman.inverterdataexport.exporter.energyid;

import be.mathiasbosman.inverterdataexport.domain.ExportService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Energy ID controller.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/energyId")
public class EnergyIdExportController {

  private final ExportService exportService;

  /**
   * Endpoint to export weekly PV statistics.
   *
   * @param inverterId Unique id of the inverter
   * @return {@link ResponseEntity}
   */
  @PutMapping("/trigger/weeklyExport/{inverterId}")
  public ResponseEntity<Object> triggerWeeklyExport(
      @PathVariable("inverterId") String inverterId) {
    LocalDate today = LocalDate.now();
    LocalDate pastWeek = today.minusWeeks(1);

    exportService.exportPvStatisticsForPeriod(inverterId, pastWeek, today);

    return ResponseEntity.ok().build();
  }
}
