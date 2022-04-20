package be.mathiasbosman.inverterdataexport.collector.alphaess;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import be.mathiasbosman.inverterdataexport.collector.alphaess.AlphaessProperties.Credentials;
import be.mathiasbosman.inverterdataexport.collector.alphaess.AlphaessProperties.Endpoints;
import be.mathiasbosman.inverterdataexport.collector.alphaess.dto.LoginRequestDto;
import be.mathiasbosman.inverterdataexport.collector.alphaess.response.LoginResponseEntity;
import be.mathiasbosman.inverterdataexport.collector.alphaess.response.LoginResponseEntity.LoginData;
import be.mathiasbosman.inverterdataexport.collector.alphaess.response.SticsByPeriodResponseEntity;
import be.mathiasbosman.inverterdataexport.collector.alphaess.response.SticsByPeriodResponseEntity.Statistics;
import be.mathiasbosman.inverterdataexport.domain.ExporterException;
import be.mathiasbosman.inverterdataexport.domain.PvStatistics;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class AlphaessCollectorTest {

  @Mock
  private RestTemplate restTemplate;
  private final AlphaessProperties alphaessProperties = new AlphaessProperties();
  private final Endpoints endpoints = new Endpoints();

  private static final String TIME_ZONE = "Europe/Brussels";
  private static final String MOCK_TOKEN = "123";
  private AlphaessCollector alphaessCollector;

  @BeforeEach
  void initService() throws MalformedURLException {
    alphaessProperties.setBaseUrl(new URL("https://foo"));
    alphaessProperties.setTimezone(TIME_ZONE);
    endpoints.setAuthentication("/auth");
    endpoints.setDailyStats("/stats");
    alphaessProperties.setEndpoints(endpoints);
    alphaessCollector = new AlphaessCollector(restTemplate, alphaessProperties);
  }

  @Test
  void buildUri() throws URISyntaxException {
    assertThat(alphaessCollector.buildUri("bar")).isEqualTo(new URI("https://foo/bar"));
  }

  @Test
  void authentication() {
    mockAuth();

    alphaessCollector.authenticate(new LoginRequestDto("foo", "bar"));

    assertThat(alphaessCollector.getOrRefreshAccessToken()).isNotNull();
  }

  @Test
  void authenticationFails() {
    when(restTemplate.postForObject(any(), any(), eq(LoginResponseEntity.class)))
        .thenReturn(null);

    LoginRequestDto loginRequestDto = new LoginRequestDto("foo", "bar");
    assertThrows(ExporterException.class,
        () -> alphaessCollector.authenticate(loginRequestDto));
  }

  @Test
  void authenticationFailsWithNoData() {
    when(restTemplate.postForObject(any(), any(), eq(LoginResponseEntity.class)))
        .thenReturn(new LoginResponseEntity());

    LoginRequestDto loginRequestDto = new LoginRequestDto("foo", "bar");
    assertThrows(ExporterException.class,
        () -> alphaessCollector.authenticate(loginRequestDto));
  }

  @Test
  void buildNoneAuthHeaders() {
    HttpHeaders httpHeaders = alphaessCollector.buildHeaders(null);

    assertHttpHeaders(httpHeaders, null);
  }

  @Test
  void buildAuthHeaders() {
    HttpHeaders httpHeaders = alphaessCollector.buildHeaders(MOCK_TOKEN);

    assertHttpHeaders(httpHeaders, "Bearer " + MOCK_TOKEN);
  }

  @Test
  void isTokenValid() {
    LoginData missingToken = LoginData.builder().
        accessToken(null)
        .build();
    LoginData expiresInIsZero = LoginData.builder()
        .accessToken(MOCK_TOKEN)
        .expiresIn(0)
        .build();
    LoginData expiresInIsNegative = LoginData.builder()
        .accessToken(MOCK_TOKEN)
        .expiresIn(-1)
        .tokenCreateTime(new Date())
        .build();
    LoginData creationTimeIsNull = LoginData.builder()
        .accessToken(MOCK_TOKEN)
        .expiresIn(10)
        .tokenCreateTime(null)
        .build();
    LoginData validToken = LoginData.builder()
        .accessToken(MOCK_TOKEN)
        .expiresIn(1000)
        .tokenCreateTime(new Date())
        .build();

    assertThat(alphaessCollector.isTokenValid(null)).isFalse();
    assertThat(alphaessCollector.isTokenValid(missingToken)).isFalse();
    assertThat(alphaessCollector.isTokenValid(expiresInIsZero)).isFalse();
    assertThat(alphaessCollector.isTokenValid(expiresInIsNegative)).isFalse();
    assertThat(alphaessCollector.isTokenValid(creationTimeIsNull)).isFalse();
    assertThat(alphaessCollector.isTokenValid(validToken)).isTrue();
  }

  @Test
  void getTotalPv() {
    mockAuth();
    SticsByPeriodResponseEntity response = new SticsByPeriodResponseEntity();
    response.setData(Statistics.builder().pvTotal(50).build());
    when(restTemplate.postForObject(any(), any(), eq(SticsByPeriodResponseEntity.class)))
        .thenReturn(response);
    LocalDate today = LocalDate.now();

    Optional<PvStatistics> stats = alphaessCollector.getTotalPv("sn", today);
    assertThat(stats).isNotEmpty();
    assertThat(stats.get().getPvTotal()).isEqualTo(50);
    assertThat(stats.get().getDate()).isEqualTo(today);
  }

  @Test
  void getTotalPvIsEmpty() {
    mockAuth();
    when(restTemplate.postForObject(any(), any(), eq(SticsByPeriodResponseEntity.class)))
        .thenReturn(null);

    assertThat(alphaessCollector.getTotalPv("123", LocalDate.now())).isEmpty();
  }

  @Test
  void getTotalPvWithoutData() {
    mockAuth();
    SticsByPeriodResponseEntity sticsByPeriodResponseEntity = new SticsByPeriodResponseEntity();
    sticsByPeriodResponseEntity.setData(null);
    when(restTemplate.postForObject(any(), any(), eq(SticsByPeriodResponseEntity.class)))
        .thenReturn(sticsByPeriodResponseEntity);

    assertThat(alphaessCollector.getTotalPv("123", LocalDate.now())).isEmpty();
  }

  @Test
  void getOrRefreshAccessTokenFirstRun() {
    mockAuth();

    String token = alphaessCollector.getOrRefreshAccessToken();

    assertThat(token).isEqualTo(MOCK_TOKEN);
  }

  @Test
  void getOrRefreshAccessTokenExistingToken() {
    mockAuth();

    String firstToken = alphaessCollector.getOrRefreshAccessToken();
    String secondToken = alphaessCollector.getOrRefreshAccessToken();

    assertThat(firstToken)
        .isEqualTo(secondToken)
        .isEqualTo(MOCK_TOKEN);
  }

  private void mockAuth() {
    Credentials credentials = new Credentials();
    credentials.setUsername("foo");
    credentials.setPassword("bar");
    alphaessProperties.setCredentials(credentials);
    LoginData loginData = LoginData.builder()
        .accessToken(MOCK_TOKEN)
        .expiresIn(Double.MAX_VALUE)
        .tokenCreateTime(new Date())
        .build();
    LoginResponseEntity responseEntity = new LoginResponseEntity();
    responseEntity.setData(loginData);

    when(restTemplate.postForObject(any(), any(), eq(LoginResponseEntity.class)))
        .thenReturn(responseEntity);
  }

  @Test
  void getTimeZone() {
    assertThat(alphaessCollector.getZoneId()).isEqualTo(ZoneId.of(TIME_ZONE));
  }

  @Test
  void setGridCharging() {
    mockAuth();
    when(restTemplate.postForEntity(any(), any(), any()))
        .thenReturn(ResponseEntity.ok().build());

    assertDoesNotThrow(() -> alphaessCollector.setGridCharging("123", LocalTime.of(1, 0),
        LocalTime.of(2, 0), 20));
  }

  @Test
  void setGridChargingFails() {
    mockAuth();
    when(restTemplate.postForEntity(any(), any(), any()))
        .thenReturn(ResponseEntity.internalServerError().build());

    LocalTime time = LocalTime.now();
    assertThrows(ExporterException.class,
        () -> alphaessCollector.setGridCharging("123", time,
            time, 20));
  }

  private void assertHttpHeaders(HttpHeaders headers, String authorisation) {
    assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    assertThat(headers.getConnection()).contains("keep-alive");
    assertThat(headers.getCacheControl()).isEqualTo("no-cache");
    if (authorisation != null) {
      assertThat(headers.get("Authorization")).contains(authorisation);
    } else {
      assertThat(headers.get("Authorization")).isNull();
    }
  }
}