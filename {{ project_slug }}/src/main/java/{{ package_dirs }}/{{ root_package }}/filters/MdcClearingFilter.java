package {{ base_package }}.{{ root_package }}.filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
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
