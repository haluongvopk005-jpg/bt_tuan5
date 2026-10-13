package vn.edu.coursemanagement.controller;

import jakarta.validation.Valid;
import vn.edu.coursemanagement.model.CourseCategory;
import vn.edu.coursemanagement.repository.CourseCategoryRepository;
import vn.edu.coursemanagement.repository.CourseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CategoryController {

    private final CourseCategoryRepository categories;
    private final CourseRepository courses;

    public CategoryController(CourseCategoryRepository categories, CourseRepository courses) {
        this.categories = categories;
        this.courses = courses;
    }

    @GetMapping("/categories")
    public String list(Model model) {
        model.addAttribute("categories", categories.findAll(Sort.by("id")));
        model.addAttribute("category", new CourseCategory());
        return "categories/list";
    }

    @PostMapping("/categories")
    public String create(@Valid @ModelAttribute("category") CourseCategory category,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categories.findAll(Sort.by("id")));
            return "categories/list";
        }
        categories.save(category);
        redirect.addFlashAttribute("success", "Đã thêm loại khóa học.");
        return "redirect:/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        CourseCategory category = categories.findById(id).orElse(null);
        if (category == null) {
            redirect.addFlashAttribute("error", "Không tìm thấy loại khóa học ID " + id + ".");
            return "redirect:/categories";
        }
        if (courses.existsByCourseCategory_Id(id)) {
            redirect.addFlashAttribute("error", "Không thể xóa loại \"" + category.getName()
                    + "\" vì vẫn còn khóa học thuộc loại này.");
            return "redirect:/categories";
        }
        categories.delete(category);
        redirect.addFlashAttribute("success", "Đã xóa loại khóa học.");
        return "redirect:/categories";
    }
}
