package be.mathiasbosman.inverterdataexport.collector.alphaess.dto;

import be.mathiasbosman.inverterdataexport.collector.alphaess.AlphaessProperties;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Dto to post to the AlphaESS to retrieve statistics.
 */
public record StatisticsRequestDto(
    @JsonFormat(pattern = AlphaessProperties.DATE_FORMAT_PATTERN)
    @JsonProperty("beginDay")
    LocalDateTime beginDay,

    @JsonFormat(pattern = AlphaessProperties.DATE_FORMAT_PATTERN)
    @JsonProperty("endDay")
    LocalDateTime endDay,

    @JsonFormat(pattern = AlphaessProperties.DATE_FORMAT_PATTERN)
    @JsonProperty("tDay")
    LocalDate today,

    @JsonProperty("isOEM")
    int isOem,

    @JsonProperty("SN")
    String sn,

    @JsonProperty("userId")
    String userId,

    @JsonProperty("noLoading")
    boolean noLoading) {

}
