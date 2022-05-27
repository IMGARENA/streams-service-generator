package com.imgarena.{{ root_package }}.filters;

import static com.imgarena.{{ root_package }}.HttpHeaderFields.RID_HEADER;
import static com.imgarena.{{ root_package }}.MdcKeys.RID_MDC_KEY;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(FilterOrder.RID_RECORDING_FILTER)
public class RequestIdFilter implements Filter {

  private final Supplier<String> requestIdGenerator;
  private final MdcWriter mdcWriter;

  public RequestIdFilter(
      @Qualifier("requestIdGenerator") Supplier<String> requestIdGenerator, MdcWriter mdcWriter) {
    this.requestIdGenerator = requestIdGenerator;
    this.mdcWriter = mdcWriter;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    mdcWriter.writeToMdc(
        RID_MDC_KEY,
        Optional.ofNullable(request)
            .map(
                req ->
                    req instanceof HttpServletRequest httpServletRequest
                        ? httpServletRequest
                        : null)
            .map(httpRequest -> httpRequest.getHeader(RID_HEADER))
            .orElseGet(requestIdGenerator));

    chain.doFilter(request, response);

    if (response instanceof HttpServletResponse httpServletResponse) {
      httpServletResponse.setHeader(RID_HEADER, MDC.get(RID_MDC_KEY));
    }
  }
}
