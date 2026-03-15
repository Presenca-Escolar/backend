package school_attendance_backend.school_attendance_backend.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "t_teacher_class")
public class TeacherClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private StudentClass studentClass;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;
}