package be.mathiasbosman.inverterdataexport.domain;

import java.time.LocalDate;

/**
 * Export service interface.
 */
public interface ExportService {

  void exportPvStatisticsForPeriod(String inverterId, LocalDate startDate, LocalDate endDate);
}
