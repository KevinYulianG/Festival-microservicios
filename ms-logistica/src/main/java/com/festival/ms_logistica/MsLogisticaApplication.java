package com.festival.ms_logistica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableFeignClients
@SpringBootApplication
public class MsLogisticaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsLogisticaApplication.class, args);
	}

}
