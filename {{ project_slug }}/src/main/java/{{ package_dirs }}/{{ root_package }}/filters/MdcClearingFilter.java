package com.imgarena.{{ root_package }}.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(FilterOrder.MDC_CLEARING_FILTER)
public class MdcClearingFilter implements Filter {

  private static final Logger LOG = LoggerFactory.getLogger(MdcClearingFilter.class);

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    MDC.clear();

    chain.doFilter(request, response);
  }
}
