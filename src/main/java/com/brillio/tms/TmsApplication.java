package com.brillio.tms;

import com.brillio.tms.annotation.AppService;
import com.brillio.tms.tokenGeneration.IAppService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

@SpringBootApplication(scanBasePackages = {"com.brillio.tms"})
public class TmsApplication {


	public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(TmsApplication.class, args);
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(AppService.class);
        for(Object obj : beansWithAnnotation.values()) {
            try {
                ((IAppService)obj).start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

	}

}
