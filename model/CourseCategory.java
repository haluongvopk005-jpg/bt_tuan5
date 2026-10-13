package vn.edu.coursemanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "course_categories")
public class CourseCategory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Tên loại khóa học không được để trống")
    @Size(max = 100, message = "Tên loại tối đa 100 ký tự")
    @Column(nullable = false, unique = true, length = 100)
    private String name;
    @Column(length = 500)
    private String description;
    @OneToMany(mappedBy = "courseCategory")
    private List<Course> courses = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }
}
