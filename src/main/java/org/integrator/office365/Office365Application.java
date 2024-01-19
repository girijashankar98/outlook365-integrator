package org.integrator.office365;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.integrator.office365.service.MessageSenderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.function.Consumer;
import java.util.function.Function;

@SpringBootApplication
@EnableScheduling
public class Office365Application {

	@Autowired
	MessageSenderService messageSenderService;
	public static void main(String[] args) {
		SpringApplication.run(Office365Application.class, args);
	}

	@Bean
	public RestTemplate restTemplate(OAuth2AuthorizedClientService clientService) {
		return new RestTemplate();
	}

	@Bean
	ModelMapper modelMapper(){
		return new ModelMapper();
	}

	@Bean
	public Function<String,String> NgDeskReceivedMessageProducer() {
		return v -> v;
	}

	@Bean
	public Consumer<String> NgDeskResponseMessageConsumer() {
		return (msg) -> {
			try {
				messageSenderService.sendMessage(msg);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		};
	}

}
