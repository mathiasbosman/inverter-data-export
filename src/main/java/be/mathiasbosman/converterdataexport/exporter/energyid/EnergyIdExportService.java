package be.mathiasbosman.converterdataexport.exporter.energyid;

import be.mathiasbosman.converterdataexport.domain.DataCollector;
import be.mathiasbosman.converterdataexport.domain.ExportService;
import be.mathiasbosman.converterdataexport.domain.PvStatistics;
import be.mathiasbosman.converterdataexport.exporter.energyid.EnergyIdProperties.EnergyIdMeter;
import be.mathiasbosman.converterdataexport.exporter.energyid.dto.MeterReadingsDto;
import be.mathiasbosman.converterdataexport.util.DateUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
   * @param converterId The unique id of the converter
   * @param start       Start date
   * @param end         End date (not inclusive)
   */
  public void exportPvStatisticsForPeriod(String converterId, LocalDate start, LocalDate end) {
    EnergyIdMeter energyIdMeter = getEnergyIdMeter(converterId);
    List<PvStatistics> statistics = dataCollector.getTotalPvForPeriod(converterId, start, end);
    ZoneId zoneId = dataCollector.getZoneId();

    MeterReadingsDto meterReadingsDto = MeterReadingsDto.fromEnergyIdMeter(energyIdMeter);
    statistics.forEach(pvStatistics -> {
      LocalDateTime localDateTime = DateUtils.atStartOfDayInZone(pvStatistics.getDate(), zoneId);
      List<Object> data = List.of(
          DateUtils.formatAsIsoDate(localDateTime, zoneId),
          pvStatistics.getPvTotal());
      meterReadingsDto.data().add(data);
    });

    if (!meterReadingsDto.data().isEmpty()) {
      webhookAdapter.postReadings(meterReadingsDto);
    }
  }

  private EnergyIdMeter getEnergyIdMeter(String converterId) {
    return energyIdProperties.getMeters().stream()
        .filter(meter -> meter.getConverterId().equals(converterId))
        .findFirst()
        .orElseThrow();
  }
}