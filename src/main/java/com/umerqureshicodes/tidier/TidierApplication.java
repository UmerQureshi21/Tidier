package com.umerqureshicodes.tidier;

import com.umerqureshicodes.tidier.s3.S3Service;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
@EnableCaching
public class TidierApplication {

	public static void main(String[] args) {SpringApplication.run(TidierApplication.class, args);}


}
