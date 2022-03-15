package be.mathiasbosman.alphaessenergyidproxy.domain.energyid.dto;

import be.mathiasbosman.alphaessenergyidproxy.config.ProxyProperties.EnergyIdMeter;
import java.util.ArrayList;
import java.util.List;

/**
 * Data used to post to the EnergyID webhook.
 */
public record MeterReadingsDto(
    String remoteId,
    String remoteName,
    String metric,
    String unit,
    String readingType,
    List<List<Object>> data
) {

  /**
   * Create {@link MeterReadingsDto} based on an {@link EnergyIdMeter}.
   *
   * @param energyIdMeter The {@link EnergyIdMeter}
   * @return {@link MeterReadingsDto}
   */
  public static MeterReadingsDto fromEnergyIdMeter(EnergyIdMeter energyIdMeter) {
    return new MeterReadingsDto(
        energyIdMeter.getRemoteId(),
        energyIdMeter.getRemoteName(),
        energyIdMeter.getMetric(),
        energyIdMeter.getUnit(),
        energyIdMeter.getReadingType(),
        new ArrayList<>()
    );
  }
}