package be.mathiasbosman.inverterdataexport.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import be.mathiasbosman.inverterdataexport.AbstractControllerTest;
import be.mathiasbosman.inverterdataexport.PvStatisticStub;
import be.mathiasbosman.inverterdataexport.domain.DataCollector;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.mock.mockito.SpyBean;

class DataControllerTest extends AbstractControllerTest {

  @SpyBean
  private DataCollector dataCollector;

  @Test
  void getPvStats() throws Exception {
    LocalDate date = LocalDate.now();

    mvc.perform(get("/rest/data/statistics/pv/foo/" + date));

    ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<LocalDate> dateArgumentCaptor = ArgumentCaptor.forClass(LocalDate.class);
    verify(dataCollector).getTotalPv(stringArgumentCaptor.capture(), dateArgumentCaptor.capture());
    assertThat(stringArgumentCaptor.getValue()).isEqualTo("foo");
    assertThat(dateArgumentCaptor.getValue()).isEqualTo(date);
  }

  @Test
  void getPvStatsReturnsData() throws Exception {
    LocalDate today = LocalDate.now();
    when(dataCollector.getTotalPv(any(), eq(today)))
        .thenReturn(Optional.of(new PvStatisticStub(today, 5.0)));

    mvc.perform(get("/rest/data/statistics/pv/foo/" + today))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.date", equalTo(today.toString())))
        .andExpect(jsonPath("$.pvTotal", equalTo(5.0)));
  }

  @Test
  void getPvStatsReturnsNoContent() throws Exception {
    when(dataCollector.getTotalPv(any(), any()))
        .thenReturn(Optional.empty());

    mvc.perform(get("/rest/data/statistics/pv/foo/" + LocalDate.now()))
        .andExpect(status().isNoContent());
  }
}