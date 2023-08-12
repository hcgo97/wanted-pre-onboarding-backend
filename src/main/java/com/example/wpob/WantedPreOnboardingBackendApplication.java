package com.example.wpob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableAspectJAutoProxy
@EnableJpaAuditing
@ComponentScan("com.example")
@EntityScan("com.example")
@EnableJpaRepositories("com.example")
@ServletComponentScan("com.example")
@SpringBootApplication
public class WantedPreOnboardingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(WantedPreOnboardingBackendApplication.class, args);
    }

}
