package school_attendance_backend.school_attendance_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import school_attendance_backend.school_attendance_backend.entity.User;

public interface AuthorizationRepository extends JpaRepository<User, Long> {
    UserDetails findByUsername(String username);
}
