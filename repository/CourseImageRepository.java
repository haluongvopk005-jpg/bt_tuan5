package vn.edu.coursemanagement.repository;

import vn.edu.coursemanagement.model.CourseImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseImageRepository extends JpaRepository<CourseImage, Long> {
    long countByCourseId(Long courseId);
}
