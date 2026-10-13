package vn.edu.coursemanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "course_images")
public class CourseImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Đường dẫn ảnh không được để trống")
    @Size(max = 1000, message = "Đường dẫn ảnh quá dài")
    @Column(nullable = false, length = 1000)
    private String imageUrl;
    @Column(nullable = false)
    private boolean isThumbnail = false;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public boolean isThumbnail() { return isThumbnail; }
    public void setThumbnail(boolean thumbnail) { isThumbnail = thumbnail; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
}
