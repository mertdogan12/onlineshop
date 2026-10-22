package de.sepe.onlineshop.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EntityScan(basePackages = {"de.sepe.onlineshop.entity"})
@ComponentScan(basePackages = {"de.sepe.onlineshop.controller", "de.sepe.onlineshop.service"})
@EnableJpaRepositories(basePackages = {"de.sepe.onlineshop.repository"})
@EnableSwagger2
public class OnlineshopApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineshopApplication.class, args);
    }

    public Docket restApi() {
        return new Docket(DocumentationType.SWAGGER_2).select().build();
    }

}
