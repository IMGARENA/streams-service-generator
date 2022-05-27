package com.imgarena.{{ root_package }}.filters;

import org.springframework.core.Ordered;

public final class FilterOrder {
  private FilterOrder() {}

  static final int MDC_CLEARING_FILTER = Ordered.HIGHEST_PRECEDENCE;
  static final int RID_RECORDING_FILTER = MDC_CLEARING_FILTER + 1;
  static final int HTTP_LOGGING_FILTER = RID_RECORDING_FILTER + 1;
}
