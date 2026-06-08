package org.example.document_pro_v1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DocumentProV1Application {

    public static void main(String[] args) {
        System.out.println("JWT_SECRET = " + System.getenv("JWT_SECRET"));
        SpringApplication.run(DocumentProV1Application.class, args);
    }

}
