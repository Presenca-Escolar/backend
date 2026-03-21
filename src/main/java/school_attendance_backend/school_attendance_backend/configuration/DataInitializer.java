package school_attendance_backend.school_attendance_backend.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import school_attendance_backend.school_attendance_backend.entity.User;
import school_attendance_backend.school_attendance_backend.entity.UserRole;
import school_attendance_backend.school_attendance_backend.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            if (userRepository.count() == 0) {

                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("123456"));
                admin.setRole(UserRole.ADMIN);

                User teacher = new User();
                teacher.setUsername("teacher");
                teacher.setPassword(passwordEncoder.encode("123456"));
                teacher.setRole(UserRole.TEACHER);

                User student = new User();
                student.setUsername("student");
                student.setPassword(passwordEncoder.encode("123456"));
                student.setRole(UserRole.STUDENT);

                userRepository.save(admin);
                userRepository.save(teacher);
                userRepository.save(student);
            }
        };
    }
}