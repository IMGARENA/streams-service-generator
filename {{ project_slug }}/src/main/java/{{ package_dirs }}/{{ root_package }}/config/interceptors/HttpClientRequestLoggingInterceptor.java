package {{ base_package }}.{{ root_package }}.config.interceptors;

import static {{ base_package }}.{{ root_package }}.log.HttpClientRequestMetadata.httpClientRequestLogMetadata;
import static {{ base_package }}.{{ root_package }}.log.HttpClientRequestMetadata.httpClientResponseLogMetadata;

import java.io.IOException;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;

public class HttpClientRequestLoggingInterceptor implements ClientHttpRequestInterceptor {
  private static final Logger LOG =
      LoggerFactory.getLogger(HttpClientRequestLoggingInterceptor.class);

  private static final String HTTP_CLIENT_REQUEST_LOG_EVENT = "httpclient.request";
  private static final String HTTP_CLIENT_RESPONSE_LOG_EVENT = "httpclient.response";
  private static final String HTTP_CLIENT_EXCEPTION_LOG_EVENT = "httpclient.exception";

  @Override
  @NonNull
  public ClientHttpResponse intercept(
      @NonNull HttpRequest request,
      @NonNull byte[] body,
      @NonNull ClientHttpRequestExecution execution)
      throws IOException {
    LOG.info(
        httpClientRequestLogMetadata(request.getURI().toString(), request.getMethodValue()),
        HTTP_CLIENT_REQUEST_LOG_EVENT);
    Instant start = Instant.now();

    try {
      ClientHttpResponse response = execution.execute(request, body);
      LOG.info(
          httpClientResponseLogMetadata(
              request.getURI().toString(),
              request.getMethodValue(),
              getStatusCode(response),
              start),
          HTTP_CLIENT_RESPONSE_LOG_EVENT);
      return response;
    } catch (Exception e) {
      LOG.error(
          httpClientRequestLogMetadata(request.getURI().toString(), request.getMethodValue()),
          HTTP_CLIENT_EXCEPTION_LOG_EVENT,
          e);
      throw e;
    }
  }

  private static String getStatusCode(ClientHttpResponse response) {
    try {
      return String.valueOf(response.getStatusCode().value());
    } catch (Exception e) {
      return "";
    }
  }
}
