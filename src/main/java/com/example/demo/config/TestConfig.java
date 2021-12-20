package com.example.demo.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.entities.Role;
import com.example.demo.entities.User;
import com.example.demo.repositories.RoleRepository;
import com.example.demo.repositories.UserRepository;

@Configuration
@Profile("test")
public class TestConfig implements CommandLineRunner {

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {
		Role role1 = new Role(null, "READ");
		Role role2 = new Role(null, "WRITE");

		roleRepository.saveAll(Arrays.asList(role1, role2));

		User user = new User(null, "Bob Brown", "bob@gmail.com", "988888888", passwordEncoder.encode("123456"));
		user.getRoles().add(role1);
		user.getRoles().add(role2);
		
		userRepository.save(user);
	}

}
