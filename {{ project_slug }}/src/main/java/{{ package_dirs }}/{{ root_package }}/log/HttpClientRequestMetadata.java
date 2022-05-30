package {{ base_package }}.{{ root_package }}.log;

import static net.logstash.logback.marker.Markers.appendEntries;

import com.imgarena.{{ root_package }}.LogMetadataFields;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import net.logstash.logback.marker.LogstashMarker;

public class HttpClientRequestMetadata {

  private HttpClientRequestMetadata() {}

  public static LogstashMarker httpClientRequestLogMetadata(String uri, String method) {
    return appendEntries(httpClientRequestLogMetadataMap(uri, method));
  }

  public static LogstashMarker httpClientResponseLogMetadata(
      String uri, String method, String statusCode, Instant requestStart) {
    Map<String, Object> result = new HashMap<>();
    result.putAll(httpClientRequestLogMetadataMap(uri, method));
    result.putAll(httpClientResponseLogMetadataMap(statusCode, requestStart));
    return appendEntries(result);
  }

  private static Map<String, Object> httpClientResponseLogMetadataMap(
      String statusCode, Instant requestStart) {
    return Map.of(
        LogMetadataFields.HTTP_RESPONSE_STATUS,
        statusCode,
        LogMetadataFields.CLIENT_HTTP_RESPONSE_TIME,
        String.valueOf(requestStart.until(Instant.now(), ChronoUnit.MILLIS)));
  }

  private static Map<String, Object> httpClientRequestLogMetadataMap(String uri, String method) {
    return Map.of(
        LogMetadataFields.CLIENT_HTTP_REQUEST_URI, uri,
        LogMetadataFields.CLIENT_HTTP_REQUEST_METHOD, method);
  }
}
