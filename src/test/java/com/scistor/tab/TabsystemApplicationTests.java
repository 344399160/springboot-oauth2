package com.scistor.tab;

import com.scistor.tab.auth.model.User;
import com.scistor.tab.auth.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TabsystemApplicationTests {

	@Autowired
	private UserRepository userRepository;

	@Test
	public void contextLoads() {
		List<User> list = userRepository.findAll();
		list.forEach(item -> {
			System.out.println(item.getGroupName());
		});
	}

}
