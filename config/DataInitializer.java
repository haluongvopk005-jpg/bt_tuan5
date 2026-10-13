package vn.edu.coursemanagement.config;

import vn.edu.coursemanagement.model.*;
import vn.edu.coursemanagement.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seed(CourseCategoryRepository categories, CourseRepository courses, CourseImageRepository images) {
        return args -> {
            if (categories.count() > 0) return;
            CourseCategory web = category(categories, "Lập trình Web", "Phát triển ứng dụng web");
            CourseCategory programming = category(categories, "Lập trình cơ bản", "Nền tảng lập trình");
            CourseCategory data = category(categories, "Dữ liệu", "Cơ sở dữ liệu và phân tích dữ liệu");
            makeCourse(courses, images, web, "ASP.NET Core", 2500000, 45, true, "Xây dựng ứng dụng web với ASP.NET Core", "https://placehold.co/640x360/1d4ed8/ffffff?text=ASP.NET+Core");
            makeCourse(courses, images, web, "ReactJS", 2200000, 30, true, "Xây dựng giao diện với ReactJS", "https://placehold.co/640x360/0891b2/ffffff?text=ReactJS");
            makeCourse(courses, images, programming, "Java cơ bản", 1800000, 36, true, "Biến, điều kiện, vòng lặp và OOP", "https://placehold.co/640x360/ea580c/ffffff?text=Java");
            makeCourse(courses, images, programming, "Python nhập môn", 1500000, 24, false, "Nhập môn ngôn ngữ Python", "https://placehold.co/640x360/15803d/ffffff?text=Python");
            makeCourse(courses, images, data, "SQL Server", 2000000, 32, true, "Thiết kế và truy vấn cơ sở dữ liệu", "https://placehold.co/640x360/7c3aed/ffffff?text=SQL+Server");
        };
    }
    private CourseCategory category(CourseCategoryRepository repo, String name, String desc) {
        CourseCategory c = new CourseCategory(); c.setName(name); c.setDescription(desc); return repo.save(c);
    }
    private void makeCourse(CourseRepository repo, CourseImageRepository imageRepo, CourseCategory cat,
                            String name, double fee, int duration, boolean active, String desc, String url) {
        Course c = new Course(); c.setName(name); c.setTuitionFee(BigDecimal.valueOf(fee));
        c.setDuration(duration); c.setActive(active); c.setDescription(desc); c.setCourseCategory(cat);
        c = repo.save(c);
        CourseImage image = new CourseImage(); image.setCourse(c); image.setImageUrl(url); image.setThumbnail(true);
        imageRepo.save(image);
    }
}
