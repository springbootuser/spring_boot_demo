package com.capgemini.rest.parent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoConfiguration
@SpringBootApplication
public class ParentApp 
{
    public static void main( String[] args )
    {
        SpringApplication.run(ParentApp.class, args);
    }
}
