package com.example.cloudserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.alibaba.nacos.client.config.impl.CacheData.log;

@SpringBootApplication
//@EnableDiscoveryClient
public class CloudServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudServerApplication.class, args);
	}

	@Slf4j
	@RestController
	@RefreshScope
	static class TestController {

		@GetMapping("/hello")
		public String hello(String name) {
			log.info("name:{}", name);
			return "hello " + name;
		}

        @Value("${didispace.title:}")
        private String title;

        @GetMapping("/test")
        public String hello() {
            return title;
        }
	}
}
