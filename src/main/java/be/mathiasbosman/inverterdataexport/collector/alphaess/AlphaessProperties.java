package be.mathiasbosman.inverterdataexport.collector.alphaess;

import static be.mathiasbosman.inverterdataexport.collector.alphaess.AlphaessProperties.PREFIX;

import java.net.URL;
import java.util.TimeZone;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration for the AlphaESS API.
 */
@Data
@Validated
@Configuration
@EnableScheduling
@ConfigurationProperties(prefix = PREFIX)
public class AlphaessProperties {

  public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
  public static final String TIME_FORMAT_PATTERN = "HH:mm";
  public static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

  public static final String PREFIX = "alphaess";

  /**
   * Timezone used; Defaults to the default {@link TimeZone}.
   */
  private String timezone = TimeZone.getDefault().getID();

  /**
   * Base {@link URL} of the AlphaESS API.
   */
  @NotNull
  private URL baseUrl;

  /**
   * All the used {@link Endpoints}.
   */
  @NotNull
  private Endpoints endpoints;

  /**
   * {@link Credentials} for the API.
   */
  @NotNull
  private Credentials credentials;

  /**
   * Credentials configuration class.
   */
  @Getter
  @Setter
  public static class Credentials {

    /**
     * Username of the API user.
     */
    @NotNull
    private String username;
    /**
     * Password of the API user.
     */
    @NotNull
    private String password;
  }

  /**
   * Endpoints configuration class.
   */
  @Getter
  @Setter
  public static class Endpoints {

    /**
     * Authentication endpoint.
     */
    @NotNull
    private String authentication;
    /**
     * Daily statistics endpoint.
     */
    @NotNull
    private String dailyStats;
    /**
     * Settings endpoint
     */
    @NotNull
    private String settings;
  }
}
