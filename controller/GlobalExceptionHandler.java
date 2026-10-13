package vn.edu.coursemanagement.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public String notFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }
    @ExceptionHandler(Exception.class)
    public String general(Exception ex, Model model) {
        model.addAttribute("message", "Đã xảy ra lỗi khi xử lý yêu cầu. Vui lòng kiểm tra dữ liệu và thử lại.");
        return "error/500";
    }
}
