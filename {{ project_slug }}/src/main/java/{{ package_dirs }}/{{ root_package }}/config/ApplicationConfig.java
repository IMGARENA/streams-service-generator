package {{ base_package }}.{{ root_package }}.config;

import {{ base_package }}.{{ root_package }}.config.interceptors.HttpClientRequestLoggingInterceptor;
import {{ base_package }}.{{ root_package }}.config.interceptors.RequestIdInterceptor;
import {{ base_package }}.{{ root_package }}.config.interceptors.RestTemplateInterceptor;
import {{ base_package }}.{{ root_package }}.log.RequestIdGenerator;
import {{ base_package }}.{{ root_package }}.HttpHeaderFields;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import java.util.Arrays;

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
    result.setValidateAfterInactivity(30_000);
    result.setMaxTotal(50);
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
    httpRequestFactory.setReadTimeout(TIMEOUT_IN_MILLIS);
    httpRequestFactory.setConnectionRequestTimeout(TIMEOUT_IN_MILLIS);
    httpRequestFactory.setHttpClient(httpClient);
    RestTemplate restTemplate =
        new RestTemplateBuilder()
            .requestFactory(() -> httpRequestFactory)
            .build();
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
    restTemplate.getInterceptors()
        .addAll(
            Arrays.asList(
                new RestTemplateInterceptor(),
                new RequestIdInterceptor(requestIdGenerator),
                new HttpClientRequestLoggingInterceptor()));
    return restTemplate;
  }
  
  
}
