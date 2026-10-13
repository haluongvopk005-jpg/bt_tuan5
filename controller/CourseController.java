package vn.edu.coursemanagement.controller;

import jakarta.validation.Valid;
import vn.edu.coursemanagement.model.*;
import vn.edu.coursemanagement.repository.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;

@Controller
public class CourseController {
    private final CourseRepository courses;
    private final CourseCategoryRepository categories;
    private final CourseImageRepository images;

    public CourseController(CourseRepository courses, CourseCategoryRepository categories, CourseImageRepository images) {
        this.courses = courses; this.categories = categories; this.images = images;
    }

    @GetMapping({"/", "/courses"})
    public String list(@RequestParam(required=false) String keyword,
                       @RequestParam(required=false) Long categoryId,
                       @RequestParam(required=false) String active,
                       @RequestParam(required=false) BigDecimal minFee,
                       @RequestParam(required=false) BigDecimal maxFee,
                       @RequestParam(defaultValue="") String sort,
                       Model model) {
        Specification<Course> spec = (root, query, cb) -> cb.conjunction();
        if (keyword != null && !keyword.isBlank()) {
            String term = "%" + keyword.trim().toLowerCase() + "%";
            spec = spec.and((r,q,cb) -> cb.like(cb.lower(r.get("name")), term));
        }
        if (categoryId != null) spec = spec.and((r,q,cb) -> cb.equal(r.get("courseCategory").get("id"), categoryId));
        if ("true".equals(active)) spec = spec.and((r,q,cb) -> cb.isTrue(r.get("isActive")));
        if ("false".equals(active)) spec = spec.and((r,q,cb) -> cb.isFalse(r.get("isActive")));
        if (minFee != null) spec = spec.and((r,q,cb) -> cb.greaterThanOrEqualTo(r.get("tuitionFee"), minFee));
        if (maxFee != null) spec = spec.and((r,q,cb) -> cb.lessThanOrEqualTo(r.get("tuitionFee"), maxFee));
        Sort ordering = switch (sort) {
            case "feeAsc" -> Sort.by(Sort.Direction.ASC, "tuitionFee");
            case "feeDesc" -> Sort.by(Sort.Direction.DESC, "tuitionFee");
            case "durationAsc" -> Sort.by(Sort.Direction.ASC, "duration");
            default -> Sort.by(Sort.Direction.ASC, "id");
        };
        model.addAttribute("courseList", courses.findAll(spec, ordering));
        model.addAttribute("categories", categories.findAll(Sort.by("name")));
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("active", active == null ? "" : active);
        model.addAttribute("minFee", minFee);
        model.addAttribute("maxFee", maxFee);
        model.addAttribute("sort", sort);
        if (minFee != null && maxFee != null && minFee.compareTo(maxFee) > 0) {
            model.addAttribute("warning", "Học phí tối thiểu không được lớn hơn học phí tối đa. Hãy điều chỉnh khoảng lọc.");
        }
        return "courses/list";
    }

    @GetMapping("/courses/new")
    public String createForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("categories", categories.findAll(Sort.by("name")));
        return "courses/form";
    }

    @PostMapping("/courses")
    public String create(@Valid @ModelAttribute("course") Course form, BindingResult result,
                         @RequestParam Long categoryId, Model model, RedirectAttributes redirect) {
        CourseCategory category = categories.findById(categoryId).orElse(null);
        if (category == null) result.rejectValue("courseCategory", "category.invalid", "Loại khóa học không tồn tại.");
        if (category != null && form.getName() != null && courses.existsByNameIgnoreCaseAndCourseCategoryId(form.getName().trim(), categoryId))
            result.rejectValue("name", "course.duplicate", "Tên khóa học đã tồn tại trong loại này.");
        if (result.hasErrors()) {
            model.addAttribute("categories", categories.findAll(Sort.by("name")));
            return "courses/form";
        }
        form.setName(form.getName().trim()); form.setCourseCategory(category);
        courses.save(form);
        redirect.addFlashAttribute("success", "Đã thêm khóa học thành công.");
        return "redirect:/courses";
    }

    @GetMapping("/courses/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("course", courses.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học ID " + id)));
        model.addAttribute("categories", categories.findAll(Sort.by("name")));
        return "courses/form";
    }

    @PostMapping("/courses/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("course") Course form,
                         BindingResult result, @RequestParam Long categoryId, Model model, RedirectAttributes redirect) {
        Course existing = courses.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học ID " + id));
        CourseCategory category = categories.findById(categoryId).orElse(null);
        if (category == null) result.rejectValue("courseCategory", "category.invalid", "Loại khóa học không tồn tại.");
        if (category != null && form.getName() != null && courses.existsByNameIgnoreCaseAndCourseCategoryIdAndIdNot(form.getName().trim(), categoryId, id))
            result.rejectValue("name", "course.duplicate", "Tên khóa học đã tồn tại trong loại này.");
        if (result.hasErrors()) {
            form.setId(id); model.addAttribute("categories", categories.findAll(Sort.by("name")));
            return "courses/form";
        }
        existing.setName(form.getName().trim()); existing.setTuitionFee(form.getTuitionFee());
        existing.setDuration(form.getDuration()); existing.setActive(form.isActive());
        existing.setDescription(form.getDescription()); existing.setCourseCategory(category);
        courses.save(existing);
        redirect.addFlashAttribute("success", "Đã cập nhật khóa học.");
        return "redirect:/courses";
    }

    @GetMapping("/courses/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("course", courses.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học ID " + id)));
        model.addAttribute("newImage", new CourseImage());
        return "courses/detail";
    }

    @PostMapping("/courses/{id}/images")
    public String addImage(@PathVariable Long id, @RequestParam String imageUrl, RedirectAttributes redirect) {
        Course course = courses.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học ID " + id));
        if (imageUrl == null || imageUrl.isBlank() || imageUrl.length() > 1000) {
            redirect.addFlashAttribute("error", "URL ảnh không được trống và tối đa 1000 ký tự.");
            return "redirect:/courses/" + id;
        }
        CourseImage image = new CourseImage(); image.setCourse(course); image.setImageUrl(imageUrl.trim());
        image.setThumbnail(course.getImages().isEmpty());
        images.save(image);
        redirect.addFlashAttribute("success", "Đã thêm ảnh.");
        return "redirect:/courses/" + id;
    }

    @PostMapping("/courses/{courseId}/images/{imageId}/thumbnail")
    public String setThumbnail(@PathVariable Long courseId, @PathVariable Long imageId, RedirectAttributes redirect) {
        Course course = courses.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học ID " + courseId));
        CourseImage selected = images.findById(imageId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ảnh ID " + imageId));
        if (!selected.getCourse().getId().equals(courseId)) throw new ResourceNotFoundException("Ảnh không thuộc khóa học này.");
        course.getImages().forEach(image -> image.setThumbnail(image.getId().equals(imageId)));
        courses.save(course);
        redirect.addFlashAttribute("success", "Đã đổi ảnh đại diện.");
        return "redirect:/courses/" + courseId;
    }

    @PostMapping("/courses/{id}/delete")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes redirect) {
        Course course = courses.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học ID " + id));
        courses.delete(course);
        redirect.addFlashAttribute("success", "Đã xóa khóa học.");
        return "redirect:/courses";
    }
}
