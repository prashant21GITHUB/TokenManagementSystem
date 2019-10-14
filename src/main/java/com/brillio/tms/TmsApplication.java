package com.brillio.tms;

import com.brillio.tms.annotation.AppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

@SpringBootApplication(scanBasePackages = {"com.brillio.tms"})
public class TmsApplication {

    private static final Logger LOGGER= LoggerFactory.getLogger("TMSApplication");

	public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(TmsApplication.class, args);
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(AppService.class);
        for(Object obj : beansWithAnnotation.values()) {
            ((IAppService)obj).start();
        }
        LOGGER.info("Application started");
	}

}
