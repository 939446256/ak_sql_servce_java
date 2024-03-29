package com.example.demo;

import com.example.demo.ak.akSQL.AKSQLMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Demo1ApplicationTests {

    @Autowired
    private AKSQLMapper aksqlMapper;

    @Test
    void contextLoads() {
        System.out.println("打印日志");
//        aksqlMapper.queryForList(TestDto.class);
    }

}
