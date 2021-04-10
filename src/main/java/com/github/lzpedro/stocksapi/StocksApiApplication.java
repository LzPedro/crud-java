package com.github.lzpedro.stocksapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StocksApiApplication {

	public static void main(String[] args) {
                //Start the REST API
		SpringApplication.run(StocksApiApplication.class, args);
	}

}
