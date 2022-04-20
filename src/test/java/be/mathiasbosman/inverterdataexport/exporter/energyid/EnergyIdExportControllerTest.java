package be.mathiasbosman.inverterdataexport.exporter.energyid;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import be.mathiasbosman.inverterdataexport.AbstractControllerTest;
import be.mathiasbosman.inverterdataexport.domain.ExportService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.mock.mockito.MockBean;

class EnergyIdExportControllerTest extends AbstractControllerTest {

  @MockBean
  private ExportService exportService;


  @Test
  void triggerWeeklyExport() throws Exception {
    ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<LocalDate> dateArgumentCaptorA = ArgumentCaptor.forClass(LocalDate.class);
    ArgumentCaptor<LocalDate> dateArgumentCaptorB = ArgumentCaptor.forClass(LocalDate.class);
    doNothing().when(exportService).exportPvStatisticsForPeriod(
        stringArgumentCaptor.capture(),
        dateArgumentCaptorA.capture(),
        dateArgumentCaptorB.capture());

    mvc.perform(put("/energyId/trigger/weeklyExport/foo"))
        .andExpect(status().isOk());
    assertThat(stringArgumentCaptor.getValue()).isEqualTo("foo");
    assertThat(dateArgumentCaptorA.getValue()).isEqualTo(LocalDate.now().minusWeeks(1));
    assertThat(dateArgumentCaptorB.getValue()).isEqualTo(LocalDate.now());
  }
}