package com.imgarena.sherlock.filters;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class MdcWriter {
  public void writeToMdc(String key, String value) {
    MDC.put(key, value);
  }
}
