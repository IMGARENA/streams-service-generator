package com.imgarena.{{ root_package }};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class {{ root_package.capitalize() }}Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}