package school_attendance_backend.school_attendance_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@DiscriminatorValue("Student")
public class Student extends User {

    private String name;

    private String registration;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private StudentClass studentClass;

    @OneToMany(mappedBy = "student")
    private Set<Attendance> attendances = new HashSet<>();
}
