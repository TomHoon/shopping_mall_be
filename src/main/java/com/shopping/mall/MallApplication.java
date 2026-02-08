package com.shopping.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// @MapperScan("com.example.hotel_back.*.mapper")
@MapperScan("com.shopping.mall.*.mapper")
@EnableJpaAuditing
@SpringBootApplication
public class MallApplication {

  public static void main(String[] args) {
    SpringApplication.run(MallApplication.class, args);
  }
}
