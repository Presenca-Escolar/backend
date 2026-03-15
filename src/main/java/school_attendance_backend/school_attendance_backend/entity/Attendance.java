package school_attendance_backend.school_attendance_backend.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "t_attendances")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
}
