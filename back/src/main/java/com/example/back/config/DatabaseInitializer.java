package com.example.back.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.back.domain.Role;
import com.example.back.domain.UserBean;
import com.example.back.repository.RoleRepository;
import com.example.back.repository.UserRepository;

@Component
public class DatabaseInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initRoles();
        initUsers();
    }
    
    private void initRoles() {
        // 只有當角色表為空時才初始化
        if (roleRepository.count() == 0) {
            // 創建所有必要的角色
            Role userRole = new Role();
            userRole.setRoleName("ROLE_USER");
            userRole.setRoleDescription("前台一般使用者");
            roleRepository.save(userRole);
            
            Role campOwnerRole = new Role();
            campOwnerRole.setRoleName("ROLE_CAMP_OWNER");
            campOwnerRole.setRoleDescription("營地管理員");
            roleRepository.save(campOwnerRole);
            
            Role adminRole = new Role();
            adminRole.setRoleName("ROLE_ADMIN");
            adminRole.setRoleDescription("一般管理員");
            roleRepository.save(adminRole);
            
            Role superAdminRole = new Role();
            superAdminRole.setRoleName("ROLE_SUPER_ADMIN");
            superAdminRole.setRoleDescription("超級管理員");
            roleRepository.save(superAdminRole);
            
            System.out.println("Initialized database with default roles");
        }
    }
    
    private void initUsers() {
        // 只有當用戶表為空時才初始化
        if (userRepository.count() == 0) {
            // 創建測試用戶
            
            // 1. 一般用戶
        	UserBean user = new UserBean();
            user.setUsername("user1");
            user.setPassword(passwordEncoder.encode("password123"));
            user.setEmail("user1@example.com");
            user.setFullName("測試用戶1");
            user.setStatus(1);
            user.setIsActive(true);
            
            Role userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));
            user.getRoles().add(userRole);
            userRepository.save(user);
            
            // 2. 營地管理員
            UserBean campOwner = new UserBean();
            campOwner.setUsername("campowner1");
            campOwner.setPassword(passwordEncoder.encode("password123"));
            campOwner.setEmail("owner1@example.com");
            campOwner.setFullName("營地管理員1");
            campOwner.setStatus(1);
            campOwner.setIsActive(true);
            
            Role campOwnerRole = roleRepository.findByRoleName("ROLE_CAMP_OWNER")
                .orElseThrow(() -> new RuntimeException("Role not found"));
            campOwner.getRoles().add(campOwnerRole);
            userRepository.save(campOwner);
            
            // 3. 一般管理員
            UserBean admin = new UserBean();
            admin.setUsername("admin1");
            admin.setPassword(passwordEncoder.encode("password123"));
            admin.setEmail("admin1@example.com");
            admin.setFullName("管理員1");
            admin.setStatus(1);
            admin.setIsActive(true);
            
            Role adminRole = roleRepository.findByRoleName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Role not found"));
            admin.getRoles().add(adminRole);
            userRepository.save(admin);
            
            // 4. 超級管理員
            UserBean superAdmin = new UserBean();
            superAdmin.setUsername("superadmin");
            // 修改這裡的密碼為Admin@123
            superAdmin.setPassword(passwordEncoder.encode("Admin@123"));
            superAdmin.setEmail("super@example.com");
            superAdmin.setFullName("超級管理員");
            superAdmin.setStatus(1);
            superAdmin.setIsActive(true);
            
            Role superAdminRole = roleRepository.findByRoleName("ROLE_SUPER_ADMIN")
                .orElseThrow(() -> new RuntimeException("Role not found"));
            superAdmin.getRoles().add(superAdminRole);
            userRepository.save(superAdmin);
            
            logger.info("Initialized database with default users");
            logger.info("超級管理員帳號: superadmin, 密碼: Admin@123");
        }
    }
}