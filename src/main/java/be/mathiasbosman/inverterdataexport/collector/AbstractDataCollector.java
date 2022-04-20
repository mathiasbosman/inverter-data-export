package be.mathiasbosman.inverterdataexport.collector;

import be.mathiasbosman.inverterdataexport.domain.DataCollector;
import be.mathiasbosman.inverterdataexport.domain.PvStatistics;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Abstract collector holding the method to get a total Pv statistic for a period.
 */
public abstract class AbstractDataCollector implements DataCollector {

  @Override
  public List<Optional<PvStatistics>> getTotalPvForPeriod(String inverterId, LocalDate startDate,
      LocalDate endDate) {
    if (startDate.isEqual(endDate)) {
      return Collections.singletonList(getTotalPv(inverterId, startDate));
    }
    return startDate.datesUntil(endDate)
        .map(date -> getTotalPv(inverterId, date))
        .filter(Optional::isPresent)
        .toList();
  }
}
