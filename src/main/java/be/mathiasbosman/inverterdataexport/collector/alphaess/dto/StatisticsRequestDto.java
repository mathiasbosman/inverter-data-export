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
    @JsonProperty("beginDay")
    @JsonFormat(pattern = AlphaessProperties.DATE_FORMAT_PATTERN)
    LocalDateTime beginDay,

    @JsonProperty("endDay")
    @JsonFormat(pattern = AlphaessProperties.DATE_FORMAT_PATTERN)
    LocalDateTime endDay,

    @JsonProperty("tDay")
    @JsonFormat(pattern = AlphaessProperties.DATE_FORMAT_PATTERN)
    LocalDate today,

    @JsonProperty("isOEM")
    int isOem,

    @JsonProperty("SN")
    String sn,

    @JsonProperty("userId")
    String userId,

    @JsonProperty("noLoading")
    boolean noLoading) {

    public StatisticsRequestDto(LocalDateTime begin, LocalDateTime end, String sn) {
        this(begin, end, LocalDate.now(), 0, sn, "", true);
    }

}
