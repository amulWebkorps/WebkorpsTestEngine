package com.codecompiler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.codecompiler.entity.Admin;
import com.codecompiler.service.AdminService;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest

public class AdminServiceTest {
	
	@Autowired
	private AdminService adminService;

	@Test
	public void saveAdminDetailsSuccessTest() {
		Admin admin = new Admin();
		admin.sethName("test");
		admin.setEmail("test@gmail.com");
		admin.sethNumber("000000000000");
		admin.setPassword("asdf");
		Admin savedAdmin = this.adminService.saveAdminDetails(admin);
		Assertions.assertNotNull(savedAdmin);
		Assertions.assertTrue(savedAdmin.gethId() != null);
	}
	
}
