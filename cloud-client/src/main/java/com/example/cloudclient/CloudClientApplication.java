package com.example.cloudclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
public class CloudClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudClientApplication.class, args);
	}

	@Slf4j
	@RestController
	static class TestController {

		@Autowired
		LoadBalancerClient loadBalancerClient;
		@Autowired
		private WebClient.Builder webClientBuilder;
		@Autowired
        Client client;

		/* 使用RestTemplate */
		@GetMapping("/test")
		public String test() {
			ServiceInstance serviceInstance = loadBalancerClient.choose("nacos-server");
			String url = serviceInstance.getUri() + "/hello?name=" + "didi";
			RestTemplate restTemplate = new RestTemplate();
			String result = restTemplate.getForObject(url, String.class);
			return "Invoke : " + url + ", return : " + result;
		}

		/* 使用WebClient*/
		@GetMapping("test2")
        public Mono<String> test2() {
            Mono<String> result = webClientBuilder.build()
                    .get()
                    .uri("http://nacos-server/hello?name=okok")
                    .retrieve()
                    .bodyToMono(String.class);
            return result;
        }

        /* 使用Feign */
        @GetMapping("test3")
        public String test3() {
            String result = client.hello("koko");
            return "Return : " + result;
        }
	}

	@Bean
	@LoadBalanced
	public WebClient.Builder loadBalancedWebClientBuilder() {
	    return WebClient.builder();
    }

    @FeignClient("nacos-server")
     interface Client {

        @GetMapping("/hello")
        String hello(@RequestParam(name = "name") String name);
    }
}
