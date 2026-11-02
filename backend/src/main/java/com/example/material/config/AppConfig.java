package com.example.material.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.concurrent.Executor;
@Configuration public class AppConfig implements WebMvcConfigurer {
 @Value("${app.cors-origin}") String origin; @Value("${app.storage-dir}") String storage;
 @Bean("taskExecutor") Executor taskExecutor(){ var e=new ThreadPoolTaskExecutor(); e.setCorePoolSize(4); e.setMaxPoolSize(8); e.setQueueCapacity(50); e.setThreadNamePrefix("material-task-"); e.initialize(); return e; }
 public void addCorsMappings(CorsRegistry r){ r.addMapping("/api/**").allowedOrigins(origin).allowedMethods("GET","POST","OPTIONS").allowedHeaders("*"); }
 public void addResourceHandlers(ResourceHandlerRegistry r){ r.addResourceHandler("/uploads/**").addResourceLocations("file:"+storage+"/"); }
}
