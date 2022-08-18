package org.example.condigbat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Time;
import java.sql.Timestamp;

@SpringBootApplication
public class DemoApplication {

    public static void main(String... args) {
        SpringApplication.run(DemoApplication.class);
        System.out.println("-------------STARTED-------------" );
    }
}
