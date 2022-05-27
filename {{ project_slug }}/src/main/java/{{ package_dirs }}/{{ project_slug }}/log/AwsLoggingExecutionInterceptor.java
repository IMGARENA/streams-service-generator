package com.imgarena.sherlock.log;

import static com.imgarena.sherlock.log.HttpClientRequestMetadata.httpClientRequestLogMetadata;
import static com.imgarena.sherlock.log.HttpClientRequestMetadata.httpClientResponseLogMetadata;

import java.net.URI;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.interceptor.Context.AfterTransmission;
import software.amazon.awssdk.core.interceptor.Context.BeforeTransmission;
import software.amazon.awssdk.core.interceptor.ExecutionAttribute;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.SdkHttpResponse;

public class AwsLoggingExecutionInterceptor implements ExecutionInterceptor {
  public static final ExecutionAttribute<Instant> START_TIME =
      new ExecutionAttribute<>("startTime");
  private static final Logger LOG = LoggerFactory.getLogger(AwsLoggingExecutionInterceptor.class);
  private static final String MISSING_VALUE = "<none>";

  private static String getStatusCode(SdkHttpResponse response) {
    try {
      return String.valueOf(response.statusCode());
    } catch (Exception e) {
      return MISSING_VALUE;
    }
  }

  @Override
  public void beforeTransmission(
      BeforeTransmission context, ExecutionAttributes executionAttributes) {
    executionAttributes.putAttribute(START_TIME, Instant.now());

    LOG.info(
        httpClientRequestLogMetadata(uri(context.httpRequest()), method(context.httpRequest())),
        "httpclient.request");
  }

  @Override
  public void afterTransmission(
      AfterTransmission context, ExecutionAttributes executionAttributes) {

    Instant startTime = executionAttributes.getAttribute(START_TIME);

    LOG.info(
        httpClientResponseLogMetadata(
            uri(context.httpRequest()),
            method(context.httpRequest()),
            getStatusCode(context.httpResponse()),
            startTime),
        "httpclient.response");
  }

  private String method(SdkHttpRequest context) {
    return Optional.ofNullable(context)
        .map(SdkHttpRequest::method)
        .map(Enum::name)
        .orElse(MISSING_VALUE);
  }

  private String uri(SdkHttpRequest context) {
    return Optional.ofNullable(context)
        .map(SdkHttpRequest::getUri)
        .map(URI::toString)
        .orElse(MISSING_VALUE);
  }
}
