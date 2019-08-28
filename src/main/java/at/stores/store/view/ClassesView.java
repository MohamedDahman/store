package at.stores.store.view;

import at.stores.store.dao.ClassesRepository;
import at.stores.store.entity.Classes;
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
public class ClassesView {

    private final Logger log = LoggerFactory.getLogger(ClassesView.class);
    @Autowired
    ClassesRepository classesRepository;


    @ModelAttribute("classesAll")
    public List<Classes> getClasses() {
        List<Classes> classesList = classesRepository.findAll().stream().collect(Collectors.toList());
        return classesList;
    }

    @PreAuthorize("isAllowed('Classes')")
    @GetMapping("/classes")
    public String getClasse() {
        log.info("start Classes Page");
        return "addClasses";
    }


    @PostMapping("/classes")
    public String addNewClass(@Valid String descr, Long reorderpoint) {
        long count = classesRepository.findAll().stream().filter(e -> e.getDescribe().equalsIgnoreCase(descr)).count();
        if (count == 0L) {
            Classes newClass = new Classes(descr, reorderpoint);
            classesRepository.save(newClass);
        }
        return "redirect:classes";

    }

    @PostMapping("/editClasses")
    public String editClass(@Valid String descrDtl, Long reOrderPointDtl, Long idVal) {
        log.info("start edit class name is {} and id is {}", descrDtl, idVal);
        Optional<Classes> first = classesRepository.findAll().stream().filter(classes -> classes.getId().equals(idVal)).findFirst();
        if (first.isPresent()) {
            Classes classId = first.get();
            classId.setDescribe(descrDtl);
            classId.setReOrderPoint(reOrderPointDtl);
            classesRepository.save(classId);
        }

        return "redirect:classes";

    }

    @PostMapping("/delClasses")
    public String delClass(Long idValDelete) {
        log.info("start delete class id is  {}", idValDelete);
        Optional<Classes> first = classesRepository.findAll().stream().filter(classes -> classes.getId().equals(idValDelete)).findFirst();
        if (first.isPresent()) {
            Classes classId = first.get();
            classesRepository.delete(classId);
        }
        return "redirect:classes";

    }

}
