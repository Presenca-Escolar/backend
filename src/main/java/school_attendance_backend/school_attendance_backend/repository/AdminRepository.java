package school_attendance_backend.school_attendance_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school_attendance_backend.school_attendance_backend.entity.Admin;

public interface AdminRepository extends JpaRepository <Admin, Long> {
}
