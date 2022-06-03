package {{ base_package }}.{{ root_package }};

public final class HttpHeaderFields {
  private HttpHeaderFields() {}

  public static final String RID_HEADER = "X-Request-Id";
  public static final String REQUEST_ID_TRACE = "rid";
  public static final String REQUEST_FROM = "Request-From";
  public static final String REQUEST_FROM_SERVICE_NAME = "{{ project_name }}";
}
