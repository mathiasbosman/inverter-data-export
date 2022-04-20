package be.mathiasbosman.inverterdataexport.collector;

import be.mathiasbosman.inverterdataexport.domain.DataCollector;
import be.mathiasbosman.inverterdataexport.domain.PvStatistics;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class DataCollectorStub implements DataCollector {

  @Override
  public ZoneId getZoneId() {
    return null;
  }

  @Override
  public Optional<PvStatistics> getTotalPv(String inverterId, LocalDate date) {
    return Optional.empty();
  }

  @Override
  public List<Optional<PvStatistics>> getTotalPvForPeriod(String inverterId, LocalDate start,
      LocalDate end) {
    return null;
  }
}
