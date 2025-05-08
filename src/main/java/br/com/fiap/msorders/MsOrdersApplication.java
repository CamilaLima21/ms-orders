package br.com.fiap.msorders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@AutoConfigureAfter(FlywayAutoConfiguration.class)
@EnableFeignClients(basePackages = "br.com.fiap.msorders.infrastructure.integration.client")
public class MsOrdersApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsOrdersApplication.class, args);
	}

}
