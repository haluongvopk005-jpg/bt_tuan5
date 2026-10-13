package vn.edu.coursemanagement.repository;

import vn.edu.coursemanagement.model.CourseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseCategoryRepository extends JpaRepository<CourseCategory, Long> {
}
