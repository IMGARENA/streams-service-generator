package {{ base_package }}.{{ root_package }};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class {{ root_package.capitalize() }}Application {

  public static void main(String[] args) {
    SpringApplication.run({{ root_package.capitalize() }}Application.class, args);
  }
}
