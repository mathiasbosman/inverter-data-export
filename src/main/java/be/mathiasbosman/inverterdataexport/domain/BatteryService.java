package be.mathiasbosman.inverterdataexport.domain;

import java.time.LocalTime;

public interface BatteryService {

  void setGridCharging(String identifier, LocalTime start, LocalTime end,
      int maximumPercentageToCharge);
}
