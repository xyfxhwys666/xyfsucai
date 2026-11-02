package com.example.material;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MaterialApplication { public static void main(String[] args) { SpringApplication.run(MaterialApplication.class, args); } }
