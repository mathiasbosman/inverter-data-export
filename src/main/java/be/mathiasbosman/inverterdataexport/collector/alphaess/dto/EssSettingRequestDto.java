package be.mathiasbosman.inverterdataexport.collector.alphaess.dto;

import be.mathiasbosman.inverterdataexport.collector.alphaess.AlphaessProperties;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalTime;

/**
 * Dto to post settings
 */
public record EssSettingRequestDto(
    @JsonProperty("sys_sn")
    String sn,

    @JsonProperty("grid_charge")
    boolean gridCharge,

    @JsonProperty("time_chaf1a")
    @JsonFormat(pattern = AlphaessProperties.TIME_FORMAT_PATTERN)
    LocalTime chargingPeriod1Start,

    @JsonProperty("time_chae1a")
    @JsonFormat(pattern = AlphaessProperties.TIME_FORMAT_PATTERN)
    LocalTime chargingPeriod1End,

    @JsonProperty("bat_high_cap")
    String maxGridChargingCapacity
) {

}
