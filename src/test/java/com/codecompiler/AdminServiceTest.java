package com.codecompiler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.codecompiler.entity.Admin;
import com.codecompiler.repository.AdminRepository;
import com.codecompiler.service.AdminService;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest

public class AdminServiceTest {
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private AdminRepository adminRepository;

	@Test
	public void saveAdminDetailsSuccessTest() {
		String email = "test@gmail.com";
		Admin saveAdmin = this.adminService.findByEmail(email);
		if(saveAdmin!=null) {
			this.adminRepository.delete(saveAdmin);
		}
		Admin admin = new Admin();
		admin.sethName("test");
		admin.setEmail(email);
		admin.sethNumber("000000000000");
		admin.setPassword("asdf");
		Admin savedAdmin = this.adminService.saveAdminDetails(admin);
		Assertions.assertNotNull(savedAdmin);
		Assertions.assertTrue(savedAdmin.gethId() != null);
	}
	
}
