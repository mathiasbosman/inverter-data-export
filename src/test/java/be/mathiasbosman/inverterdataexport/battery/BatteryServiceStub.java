package be.mathiasbosman.inverterdataexport.battery;

import be.mathiasbosman.inverterdataexport.domain.BatteryService;
import java.time.LocalTime;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class BatteryServiceStub implements BatteryService {

  @Override
  public void setGridCharging(String identifier, LocalTime start, LocalTime end,
      int maximumPercentageToCharge) {

  }
}
