package com.example.student.configuration;

import com.example.student.entity.Role;
import com.example.student.entity.User;
import com.example.student.enums.RoleEnum;
import com.example.student.repository.RoleRepository;
import com.example.student.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class ApplicationInitConfig {



    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
        //ApplicationRunner sẽ được khởi chạy mỗi khi chúng ta star source
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {

                Role user_role=roleRepository.save(Role.builder()
                        .name(RoleEnum.USER.name())
                        .description("role user")
                        .build());
                Role admin =roleRepository.save(Role.builder()
                        .name(RoleEnum.ADMIN.name())
                        .description("Admin full role")
                        .build());
                var roles = new HashSet<Role>();
                roles.add(user_role);
                roles.add(admin);
                User user = User.builder()
                        .username("admin")
                        .roles(roles)
                        .password(passwordEncoder.encode("123456"))
                        .build();

                userRepository.save(user);
                log.warn("admin user has been create with dafault password: 123456, please change it");
            }
            log.info("Application initialization completed .....");
        };
    }
}
