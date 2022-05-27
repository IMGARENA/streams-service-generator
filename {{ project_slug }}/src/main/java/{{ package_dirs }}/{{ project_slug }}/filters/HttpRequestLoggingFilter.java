package com.imgarena.sherlock.filters;

import static com.imgarena.sherlock.HttpHeaderFields.RID_HEADER;
import static com.imgarena.sherlock.LogMetadataFields.CLIENT_HTTP_RESPONSE_TIME;
import static com.imgarena.sherlock.LogMetadataFields.HTTP_RESPONSE_STATUS;
import static com.imgarena.sherlock.MdcKeys.HTTP_REQUEST_METHOD_MDC_KEY;
import static com.imgarena.sherlock.MdcKeys.HTTP_REQUEST_REMOTE_HOST_MDC_KEY;
import static com.imgarena.sherlock.MdcKeys.HTTP_REQUEST_URI_MDC_KEY;
import static com.imgarena.sherlock.MdcKeys.RID_MDC_KEY;
import static java.time.temporal.ChronoUnit.MILLIS;
import static net.logstash.logback.marker.Markers.appendEntries;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(FilterOrder.HTTP_LOGGING_FILTER)
public class HttpRequestLoggingFilter implements Filter {

  private static final Logger LOG = LoggerFactory.getLogger(HttpRequestLoggingFilter.class);

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    var shouldLog = shouldLog(request);
    Instant start = null;

    if (shouldLog) {
      start = Instant.now();
      var httpServletRequest = (HttpServletRequest) request;
      MDC.put(HTTP_REQUEST_URI_MDC_KEY, getUri(httpServletRequest));
      MDC.put(HTTP_REQUEST_METHOD_MDC_KEY, httpServletRequest.getMethod());
      MDC.put(HTTP_REQUEST_REMOTE_HOST_MDC_KEY, httpServletRequest.getRemoteHost());
      LOG.info("http.request");
    }

    try {
      chain.doFilter(request, response);
    } finally {
      if (shouldLog && response instanceof HttpServletResponse httpServletResponse) {
        httpServletResponse.addHeader(RID_HEADER, MDC.get(RID_MDC_KEY));
        String status = Integer.toString(httpServletResponse.getStatus());

        LOG.info(appendEntries(getHttpResponseMetadataMap(start, status)), "http.response");
      }
    }
  }

  private Map<String, String> getHttpResponseMetadataMap(Instant start, String status) {
    return Map.of(
        HTTP_RESPONSE_STATUS,
        status,
        CLIENT_HTTP_RESPONSE_TIME,
        String.valueOf(start.until(Instant.now(), MILLIS)));
  }

  private String getUri(HttpServletRequest request) {
    StringBuilder requestUrl = new StringBuilder(request.getRequestURL().toString());
    String queryString = request.getQueryString();

    if (!StringUtils.isEmpty(queryString)) {
      requestUrl.append('?').append(queryString);
    }

    return requestUrl.toString();
  }

  private boolean shouldLog(ServletRequest request) {
    if (request instanceof HttpServletRequest httpServletRequest) {
      return !httpServletRequest.getRequestURI().contains("actuator");
    }
    return false;
  }
}
