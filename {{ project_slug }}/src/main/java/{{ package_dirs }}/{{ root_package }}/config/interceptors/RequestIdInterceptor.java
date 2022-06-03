package {{ base_package }}.{{ root_package }}.config.interceptors;

import {{ base_package }}.{{ root_package }}.log.RequestIdGenerator;
import {{ base_package }}.{{ root_package }}.HttpHeaderFields;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;

public class RequestIdInterceptor implements ClientHttpRequestInterceptor {
  public static final String REQUEST_FROM_SERVICE_NAME = "scoring";

  private final RequestIdGenerator requestIdGenerator;

  public RequestIdInterceptor(RequestIdGenerator requestIdGenerator) {
    this.requestIdGenerator = requestIdGenerator;
  }

  @Override
  @NonNull
  public ClientHttpResponse intercept(
      @NonNull HttpRequest request,
      @NonNull byte[] body,
      @NonNull ClientHttpRequestExecution execution)
      throws IOException {

    String rid = MDC.get(HttpHeaderFields.REQUEST_ID_TRACE);
    if (StringUtils.isEmpty(rid)) {
      rid = requestIdGenerator.get();
      MDC.put(HttpHeaderFields.REQUEST_ID_TRACE, rid);
    }
    request.getHeaders().add(HttpHeaderFields.RID_HEADER, rid);
    request.getHeaders().add(HttpHeaderFields.REQUEST_FROM, REQUEST_FROM_SERVICE_NAME);
    return execution.execute(request, body);
  }
}
