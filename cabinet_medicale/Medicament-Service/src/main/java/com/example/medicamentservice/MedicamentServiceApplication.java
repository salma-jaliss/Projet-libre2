package com.example.medicamentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MedicamentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MedicamentServiceApplication.class, args);
    }
}