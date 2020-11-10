package com.chl.face.intercept;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author gyh
 * @create 2020-11-03 15:39
 */
@Configuration
public class MyConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration interceptorRegistration = registry.addInterceptor(new MyIntercept());
        interceptorRegistration.addPathPatterns("/**").excludePathPatterns("/","/registry","/login","/attendance/callback",
                "/registry/toFace","/login","/toRegistry","/registry/callback");
    }
}
