package be.mathiasbosman.inverterdataexport.exporter.energyid;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import be.mathiasbosman.inverterdataexport.PvStatisticStub;
import be.mathiasbosman.inverterdataexport.domain.DataCollector;
import be.mathiasbosman.inverterdataexport.domain.ExporterException;
import be.mathiasbosman.inverterdataexport.exporter.energyid.EnergyIdProperties.EnergyIdMeter;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EnergyIdExportServiceTest {

  @Mock
  private EnergyIdWebhookAdapter webhookAdapter;
  @Mock
  private DataCollector dataCollector;
  private static final String INVERTER_ID_1 = "sn1";
  private final EnergyIdProperties energyIdProperties = new EnergyIdProperties();
  private static final String INVERTER_ID_2 = "sn2";
  private static final LocalDate DATE_NOW = LocalDate.now();
  private static final LocalDate DATE_LAST_WEEK = LocalDate.now().minusWeeks(1);
  private EnergyIdExportService energyIdExportService;

  @BeforeEach
  void initService() {
    energyIdProperties.setMeters(List.of(
        createEnergyIdMeter(INVERTER_ID_1),
        createEnergyIdMeter(INVERTER_ID_2)
    ));
    energyIdExportService = new EnergyIdExportService(
        webhookAdapter,
        dataCollector,
        energyIdProperties);
  }

  private EnergyIdMeter createEnergyIdMeter(String inverterId) {
    EnergyIdMeter meter = new EnergyIdMeter();
    meter.setMetric("metric");
    meter.setMultiplier(1);
    meter.setReadingType("readingType");
    meter.setRemoteId("remoteId");
    meter.setUnit("unit");
    meter.setInverterId(inverterId);
    return meter;
  }

  @Test
  void exportDoesNotTriggerPushWhenNoDataIsCollected() {
    when(dataCollector.getTotalPvForPeriod(any(), any(), any()))
        .thenReturn(Collections.emptyList());

    energyIdExportService.exportPvStatisticsForPeriod(INVERTER_ID_2, DATE_LAST_WEEK, DATE_NOW);

    verify(webhookAdapter, never()).postReadings(any());
  }

  @Test
  void exportPvStatisticsForPeriod() {
    mockDataCollector();

    energyIdExportService.exportPvStatisticsForPeriod(INVERTER_ID_1, DATE_LAST_WEEK, DATE_NOW);

    verify(webhookAdapter).postReadings(any());
  }

  @Test
  void exportPvStatisticsThrowsErrorForNoneExistingMeter() {
    assertThrows(ExporterException.class, () ->
        energyIdExportService.exportPvStatisticsForPeriod("foo", DATE_LAST_WEEK, DATE_NOW));
  }

  private void mockDataCollector() {
    when(dataCollector.getZoneId()).thenReturn(ZoneId.of("Europe/Brussels"));
    when(dataCollector.getTotalPvForPeriod(any(), any(), any()))
        .thenReturn(List.of(
            Optional.of(new PvStatisticStub()),
            Optional.of(new PvStatisticStub())
        ));
  }
}