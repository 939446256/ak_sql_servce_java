package com.example.demo;

import com.example.demo.ak.akSQL.mapper.BaseSQLMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@MapperScan(basePackages = "com.example.demo.ak.akSQL.mapper")
public class Demo1Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(Demo1Application.class, args);
        BaseSQLMapper mapper = applicationContext.getBean(BaseSQLMapper.class);
        System.out.println(mapper);
    }

}
