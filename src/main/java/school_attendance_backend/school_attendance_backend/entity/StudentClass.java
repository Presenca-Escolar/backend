package school_attendance_backend.school_attendance_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "t_classes")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class StudentClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer grade;

    private String shift;

    @OneToMany(mappedBy = "studentClass")
    private Set<Student> students = new HashSet<>();

    @OneToMany(mappedBy = "studentClass")
    private Set<Lesson> lessons = new HashSet<>();
}