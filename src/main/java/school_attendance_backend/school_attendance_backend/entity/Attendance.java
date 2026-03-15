package school_attendance_backend.school_attendance_backend.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "t_attendances")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    private AttendanceStatus status;
}
