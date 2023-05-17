package com.feedback.hafit.controller;

import com.feedback.hafit.entity.CategoryFormDTO;
import com.feedback.hafit.repository.CategoryRepository;
import com.feedback.hafit.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
@CrossOrigin(origins = "#")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @PostMapping("create") // 카테고리 추가
    @CrossOrigin(origins = "#")
    public boolean create(@RequestBody CategoryFormDTO categoryFormDTO) {
        boolean isCategoryCreated = categoryService.created(categoryFormDTO);
        if(!isCategoryCreated) {
            System.out.println("카테고리 추가 실패");
            return false;
        }
        System.out.println("카테고리 추가 성공");
        return true;
    }

    @PostMapping("update") // 카테고리 수정
    @CrossOrigin(origins = "#")
    public boolean update(@RequestBody CategoryFormDTO categoryFormDTO) {
        boolean isCategoryUpdated = categoryService.updated(categoryFormDTO);
        if(!isCategoryUpdated) {
            System.out.println("수정 실패");
            return false;
        }
        System.out.println("수정 성공");
        return true;
    }
}
