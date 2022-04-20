package be.mathiasbosman.inverterdataexport.exporter.energyid;

import be.mathiasbosman.inverterdataexport.domain.DataCollector;
import be.mathiasbosman.inverterdataexport.domain.ExportService;
import be.mathiasbosman.inverterdataexport.domain.ExporterException;
import be.mathiasbosman.inverterdataexport.exporter.energyid.EnergyIdProperties.EnergyIdMeter;
import be.mathiasbosman.inverterdataexport.exporter.energyid.dto.MeterReadingsDto;
import be.mathiasbosman.inverterdataexport.util.DateUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.stereotype.Component;

/**
 * Service that communicates with the EnergyID webhook.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EnergyIdExportService implements ExportService {

  private final EnergyIdWebhookAdapter webhookAdapter;
  private final DataCollector dataCollector;
  private final EnergyIdProperties energyIdProperties;

  /**
   * Exports statistics to the EnergyID platform for a given period.
   *
   * @param inverterId The unique id of the inverter
   * @param start      Start date
   * @param end        End date (not inclusive)
   */
  public void exportPvStatisticsForPeriod(String inverterId, LocalDate start, LocalDate end) {
    List<EnergyIdMeter> energyIdMeter = getEnergyIdMeters(inverterId);
    ZoneId zoneId = dataCollector.getZoneId();
    energyIdMeter.forEach(meter -> exportPeriodStatisticsForMeter(meter, zoneId, start, end));
  }

  private void exportPeriodStatisticsForMeter(EnergyIdMeter meter, ZoneId zoneId, LocalDate start,
      LocalDate end) {
    MeterReadingsDto meterReadingsDto = MeterReadingsDto.fromEnergyIdMeter(meter);

    dataCollector.getTotalPvForPeriod(meter.getInverterId(), start, end).stream()
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(pvStatistics -> {
          LocalDateTime dateTime = DateUtils.atStartOfDayInZone(pvStatistics.getDate(), zoneId);
          String isoTimestamp = DateUtils.formatAsIsoDate(dateTime, zoneId);
          meterReadingsDto.data().add(buildPvData(isoTimestamp, pvStatistics.getPvTotal()));
        });

    if (!meterReadingsDto.data().isEmpty()) {
      webhookAdapter.postReadings(meterReadingsDto);
    }
  }

  private List<Object> buildPvData(String timestamp, double pvTotal) {
    return List.of(timestamp, pvTotal);
  }

  private List<EnergyIdMeter> getEnergyIdMeters(String inverterId) {
    List<EnergyIdMeter> meters = energyIdProperties.getMeters().stream()
        .filter(meter -> meter.getInverterId().equals(inverterId))
        .toList();

    if (!meters.isEmpty()) {
      return meters;
    }
    throw new ExporterException(Level.ERROR,
        String.format("No EnergyID meter found with inverter id: %s", inverterId));
  }
}
