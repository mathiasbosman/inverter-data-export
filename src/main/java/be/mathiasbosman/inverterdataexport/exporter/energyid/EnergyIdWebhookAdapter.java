package be.mathiasbosman.inverterdataexport.exporter.energyid;

import be.mathiasbosman.inverterdataexport.domain.WebhookAdapter;
import be.mathiasbosman.inverterdataexport.exporter.energyid.dto.MeterReadingsDto;

/**
 * Interface for the EnergyID webhook.
 */
public interface EnergyIdWebhookAdapter extends WebhookAdapter<MeterReadingsDto> {

  void postReadings(MeterReadingsDto readingsDto);
}
