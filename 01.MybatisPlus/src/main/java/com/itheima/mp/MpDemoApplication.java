package com.itheima.mp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.itheima.mp.mapper")
@SpringBootApplication
public class MpDemoApplication {
    /**
     *
     * 学到 p14 扩展功能-Db静态工具
     */
    public static void main(String[] args) {
        SpringApplication.run(MpDemoApplication.class, args);
    }

}

