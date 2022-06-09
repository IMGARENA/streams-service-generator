package {{ base_package }}.{{ root_package }}.controller;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalManagementPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HealthEndpointTest {

    @LocalManagementPort
    private int managementPort;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @ParameterizedTest
    @ValueSource(
            strings = {"/actuator/health", "/actuator/health/liveness", "/actuator/health/liveness"})
    void shouldExposeActuatorHealthEndpoints(String path) {
        String url = "http://localhost:%d%s".formatted(managementPort, path);

        ResponseEntity<Void> response = testRestTemplate.getForEntity(url, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
