package {{ base_package }}.{{ root_package }}.config.filter;

import static java.util.Collections.singletonList;

import com.imgarena.auth.InternalAuthenticator;
import com.imgarena.auth.InternalAuthenticatorFactory;
import com.imgarena.auth.User;
import com.imgarena.auth.dto.InternalAuthHeader;
import com.imgarena.auth.dto.InternalToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {
  public static final String HEADER_NAME = "x-base64-token";

  private final InternalAuthenticator authenticator = InternalAuthenticatorFactory.newInstance();

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String header = request.getHeader(authenticator.zero().getName());

    if (StringUtils.isNotEmpty(header)) {
      InternalToken token = authenticator.createToken(header);
      InternalAuthHeader internalAuthHeader = authenticator.createAuthHeader(token);
      User user = authenticator.toUser(internalAuthHeader);

      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(
              user,
              internalAuthHeader,
              singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));

      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    filterChain.doFilter(request, response);
  }
}
