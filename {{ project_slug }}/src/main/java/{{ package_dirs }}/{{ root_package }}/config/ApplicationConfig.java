package {{ base_package }}.{{ root_package }}.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import {{ base_package }}.{{ root_package }}.config.interceptors.HttpClientRequestLoggingInterceptor;
import {{ base_package }}.{{ root_package }}.config.interceptors.RequestIdInterceptor;
import {{ base_package }}.{{ root_package }}.config.interceptors.RestTemplateInterceptor;
import {{ base_package }}.{{ root_package }}.log.RequestIdGenerator;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.io.SocketConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan(basePackages = "{{ base_package }}.{{ root_package }}")
@EnableWebMvc
public class ApplicationConfig implements WebMvcConfigurer {

  private static final int TIMEOUT_IN_MILLIS = 30000;

  @Autowired
  private RequestIdGenerator requestIdGenerator;

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper =
        new Jackson2ObjectMapperBuilder().modules(new Jdk8Module(), new JavaTimeModule()).build();
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    return objectMapper;
  }

  @Bean
  PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
    PoolingHttpClientConnectionManager result = new PoolingHttpClientConnectionManager();
    result.setDefaultMaxPerRoute(30);
    result.setConnectionConfigResolver(
        (httpRoute) ->
            ConnectionConfig.custom()
                .setValidateAfterInactivity(30_000, TimeUnit.MILLISECONDS)
                .build());
    result.setMaxTotal(50);
    result.setDefaultSocketConfig(
        SocketConfig.custom().setSoTimeout(TIMEOUT_IN_MILLIS, TimeUnit.MILLISECONDS).build());
    return result;
  }

  @Bean
  HttpClient httpClient() {
    return HttpClientBuilder.create()
        .setConnectionManager(poolingHttpClientConnectionManager())
        .build();
  }

  @Bean
  RestTemplate restTemplate(HttpClient httpClient, ObjectMapper objectMapper) {
    HttpComponentsClientHttpRequestFactory httpRequestFactory =
        new HttpComponentsClientHttpRequestFactory();
    httpRequestFactory.setConnectionRequestTimeout(TIMEOUT_IN_MILLIS);
    httpRequestFactory.setHttpClient(httpClient);
    RestTemplate restTemplate =
        new RestTemplateBuilder().requestFactory(() -> httpRequestFactory).build();
    MappingJackson2HttpMessageConverter messageConverter =
        new MappingJackson2HttpMessageConverter();
    messageConverter.setPrettyPrint(false);
    messageConverter.setObjectMapper(objectMapper);
    restTemplate
        .getMessageConverters()
        .removeIf(
            m ->
                m.getClass().getName().equals(MappingJackson2HttpMessageConverter.class.getName()));
    restTemplate.getMessageConverters().add(messageConverter);
    restTemplate
        .getInterceptors()
        .addAll(
            Arrays.asList(
                new RestTemplateInterceptor(),
                new RequestIdInterceptor(requestIdGenerator),
                new HttpClientRequestLoggingInterceptor()));
    return restTemplate;
  }
}
