package be.mathiasbosman.inverterdataexport.controller;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import be.mathiasbosman.inverterdataexport.AbstractControllerTest;
import be.mathiasbosman.inverterdataexport.domain.BatteryService;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.SpyBean;

class BatteryControllerTest extends AbstractControllerTest {

  @SpyBean
  private BatteryService batteryService;

  @Test
  void setGridCharge() throws Exception {
    LocalTime startTime = LocalTime.of(1, 0);
    LocalTime endTime = LocalTime.of(2, 0);
    mvc.perform(post("/rest/battery/settings/gridCharge")
            .param("identifier", "abc")
            .param("start", startTime.toString())
            .param("end", endTime.toString())
            .param("maxPercentage", "20"))
        .andDo(print())
        .andExpect(status().isOk());

    verify(batteryService).setGridCharging("abc", startTime, endTime, 20);
  }
}
