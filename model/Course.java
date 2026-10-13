package vn.edu.coursemanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses", uniqueConstraints = @UniqueConstraint(name = "uk_course_name_category", columnNames = {"name", "course_category_id"}))
public class Course {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Tên khóa học không được để trống")
    @Size(max = 150, message = "Tên khóa học tối đa 150 ký tự")
    @Column(nullable = false, length = 150)
    private String name;
    @NotNull(message = "Vui lòng nhập học phí")
    @DecimalMin(value = "0.0", inclusive = false, message = "Học phí phải lớn hơn 0")
    @Digits(integer = 12, fraction = 2, message = "Học phí không hợp lệ")
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal tuitionFee;
    @NotNull(message = "Vui lòng nhập thời lượng")
    @Min(value = 1, message = "Thời lượng phải lớn hơn 0")
    @Column(nullable = false)
    private Integer duration;
    @Column(nullable = false)
    private boolean isActive = true;
    @Column(length = 1000)
    private String description;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_category_id", nullable = false)
    private CourseCategory courseCategory;
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<CourseImage> images = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getTuitionFee() { return tuitionFee; }
    public void setTuitionFee(BigDecimal tuitionFee) { this.tuitionFee = tuitionFee; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public CourseCategory getCourseCategory() { return courseCategory; }
    public void setCourseCategory(CourseCategory courseCategory) { this.courseCategory = courseCategory; }
    public List<CourseImage> getImages() { return images; }
    public void setImages(List<CourseImage> images) { this.images = images; }
    @Transient public String getDurationType() { return duration != null && duration <= 30 ? "Ngắn hạn" : "Dài hạn"; }
    @Transient public CourseImage getThumbnail() {
        return images == null ? null : images.stream().filter(CourseImage::isThumbnail).findFirst().orElse(null);
    }
}
