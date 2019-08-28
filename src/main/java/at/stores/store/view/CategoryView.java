package at.stores.store.view;

import at.stores.store.dao.CategoryRepository;
import at.stores.store.entity.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Controller
public class CategoryView {


    private final Logger log = LoggerFactory.getLogger(CategoryView.class);

    @Autowired
    CategoryRepository categoryRepository;



    @ModelAttribute("categories")
    public List<Category> getCategories() {
        List<Category> categoryList = categoryRepository.findAll().stream().collect(Collectors.toList());
        return categoryList;
    }

    @PreAuthorize("isAllowed('Category')")
    @GetMapping("/category")
    public String getCategory() {
        return "addCategory";
    }

    @PostMapping("/category")
    public String addNewCategory(@Valid String descr) {
        long count = categoryRepository.findAll().stream().filter(e -> e.getDescribe().equalsIgnoreCase(descr)).count();
        if (count == 0L) {
            Category newcolor = new Category(descr);
            categoryRepository.save(newcolor);
        }
        return "redirect:category";

    }

    @GetMapping("/category1")
    public List<Category> getD() {
        return  categoryRepository.findAll().stream().collect(Collectors.toList());


    }



    @PostMapping("/editCategory")
    public String editCategory(@Valid String descrDtl, Long idVal) {
        log.info("start edit Category name is {} and id is {}", descrDtl, idVal);
        Optional<Category> first = categoryRepository.findAll().stream().filter(category -> category.getId().equals(idVal)).findFirst();
        if (first.isPresent()) {
            Category category = first.get();
            category.setDescribe(descrDtl);
            categoryRepository.save(category);
        }

        return "redirect:category";
    }


    @PostMapping("/delCategory")
    public String delCategory(Long idValDelete) {
        log.info("start delete Category id  is {}", idValDelete);
        Optional<Category> first = categoryRepository.findAll().stream().filter(category -> category.getId().equals(idValDelete)).findFirst();
        if (first.isPresent()) {
            Category category = first.get();
            categoryRepository.delete(category);
        }
        return "redirect:category";
    }

}
