package com.cwk.qserver;

import com.cwk.qserver.dao.entity.User;
import com.cwk.qserver.dao.mapper.UserMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.cwk.qserver.dao")

public class QServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(QServerApplication.class, args);




	}

}
