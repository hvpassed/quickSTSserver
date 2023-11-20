package com.cwk.qserver;

import com.cwk.qserver.dao.entity.User;
import com.cwk.qserver.dao.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

@SpringBootTest
@Service
class QServerApplicationTests {
	@Autowired
	private UserMapper usermappper;

	@Test
	public void test(){
		User user= new User();
		user.setUsername("123");
		user.setPassword("654");
		usermappper.insert(user);
	}
}
