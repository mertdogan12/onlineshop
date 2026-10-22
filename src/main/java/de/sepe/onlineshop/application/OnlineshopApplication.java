package de.sepe.onlineshop.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"de.sepe.onlineshop.entity"})
public class OnlineshopApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineshopApplication.class, args);
    }

}
