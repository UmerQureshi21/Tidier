package com.umerqureshicodes.tidier;

import com.umerqureshicodes.tidier.s3.S3Service;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class TidierApplication {

	public static void main(String[] args) {SpringApplication.run(TidierApplication.class, args);}

	@Bean
	CommandLineRunner commandLineRunner(S3Service s3Service) {

		String filename = "ny-heli.mp4";
		return args -> {
			s3Service.putObject(
					"tidier",
					filename,
					Paths.get("/Users/umerqureshi/Desktop/personal-projects/SpringBoot/Tidier/uploads/ny-helicopter.mp4")
					);

			Path destination = Paths.get("/Users/umerqureshi/Desktop/personal-projects/SpringBoot/Tidier/s3-downloads")
					.resolve(filename );  // key or any name you want

			Path obj = s3Service.getObjectByDownloadingToLocal("tidier", "foo", destination);

			System.out.println("Hooray! " + obj.toString());
		};
	}
}
