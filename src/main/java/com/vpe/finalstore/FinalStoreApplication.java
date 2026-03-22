package com.vpe.finalstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FinalStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinalStoreApplication.class, args);
        System.out.println("Server is running!\nVisit http://localhost:8080/swagger-ui/index.html to view the API documentation.");
    }

}
