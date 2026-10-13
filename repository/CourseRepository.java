package vn.edu.coursemanagement.repository;

import vn.edu.coursemanagement.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    boolean existsByNameIgnoreCaseAndCourseCategoryId(String name, Long categoryId);
    boolean existsByCourseCategory_Id(Long categoryId);
    boolean existsByNameIgnoreCaseAndCourseCategoryIdAndIdNot(String name, Long categoryId, Long id);
}
