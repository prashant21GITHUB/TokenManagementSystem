package com.brillio.tms.annotation;

import com.brillio.tms.IAppService;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *  This annotation is used to provide information about the {@link com.brillio.tms.IAppService} classes to spring
 *  boot framework to call {@link IAppService#start()} on application startup.
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface AppService {
}
