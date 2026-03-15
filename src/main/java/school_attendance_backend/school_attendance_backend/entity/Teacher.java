package school_attendance_backend.school_attendance_backend.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@DiscriminatorValue("Teacher")
public class Teacher extends User {

    private String name;

    private String cpf;

    @OneToMany(mappedBy = "teacher")
    private Set<TeacherClass> teacherClasses = new HashSet<>();
}