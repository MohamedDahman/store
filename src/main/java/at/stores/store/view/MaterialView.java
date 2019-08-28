package at.stores.store.view;

import at.stores.store.dao.CategoryRepository;
import at.stores.store.dao.ClassesRepository;
import at.stores.store.dao.MaterialRepository;
import at.stores.store.dao.UnitsRepository;
import at.stores.store.entity.Category;
import at.stores.store.entity.Classes;
import at.stores.store.entity.Material;
import at.stores.store.entity.Units;
import org.omg.CORBA.INTERNAL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class MaterialView {

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    ClassesRepository classesRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    UnitsRepository unitsRepository;


    private final Integer MaxPageSize = 2;

    private final Logger log = LoggerFactory.getLogger(CategoryView.class);


    @ModelAttribute("classesAll")
    public List<Classes> getClasses() {
        List<Classes> classesList = classesRepository.findAll().stream().collect(Collectors.toList());
        return classesList;
    }

    @PreAuthorize("isAllowed('Material')")
    @GetMapping("/material")
    public String getCategory(Model model) {
        int pageNo = 0;
        model.addAttribute("currentGroup", 0);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("addValue", 0);
        model.addAttribute("lastGroup", 0);

        model.addAttribute("nextGroup", 2);


        List<Material> materialList = materialRepository
                .findAll()
                .stream()
                .skip(pageNo)
                .limit(MaxPageSize)
                .collect(Collectors.toList());
        model.addAttribute("materialAll", materialList);

        return "addMaterial";
    }


    @RequestMapping(value = "/materialBarcode/{barcode}")
    public String getBarcode(Model model,
                              @PathVariable("barcode") String barcode) {

        int pageNo = 0;
        model.addAttribute("currentGroup", 0);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("addValue", 0);
        model.addAttribute("lastGroup", 0);

        model.addAttribute("nextGroup", 2);

        List<Material> materialList = materialRepository.findAll().stream()
                .filter(e->e.getBarcode().equals(barcode))
                .collect(Collectors.toList());
        model.addAttribute("materialAll", materialList);

        return "addMaterial";

    }



    @RequestMapping(value = "/materialPageNextPage/{pageNo}/{nextGroup}")
    public String getNextPage(Model model,
                              @PathVariable("pageNo") String pageNo,
                              @PathVariable("nextGroup") String nextGroup) {

        model.addAttribute("lastGroup", 0);
        model.addAttribute("addValue", 0);
        Integer x = Integer.parseInt(pageNo) + 10;
        nextGroup = nextGroup;


        model.addAttribute("pageNo", x);

        model.addAttribute("nextGroup", Integer.parseInt(nextGroup) + 1);
        List<Material> materialList = materialRepository.findAll().stream()
                .skip(x)
                .limit(MaxPageSize)
                .collect(Collectors.toList());
        model.addAttribute("materialAll", materialList);

        return "addMaterial";

    }

    @RequestMapping(value = "/materialPage/{pageNo}/{addValue}")
    public String getClassMovement(Model model,
                                   @PathVariable("pageNo") String pageNo,
                                   @PathVariable("addValue") String addValue) {


        model.addAttribute("pageNo", pageNo);
        model.addAttribute("addValue", addValue);

        model.addAttribute("nextGroup", 2);

        model.addAttribute("lastGroup", 0);


        List<Material> materialList = materialRepository.findAll().stream()
                .skip(Integer.parseInt(pageNo) + Integer.parseInt(addValue))
                .limit(MaxPageSize)
                .collect(Collectors.toList());
        model.addAttribute("materialAll", materialList);

        return "addMaterial";
    }


    @ModelAttribute("categories")
    public List<Category> getCategories() {
        List<Category> categoryList = categoryRepository.findAll().stream().collect(Collectors.toList());
        return categoryList;
    }

    @ModelAttribute("units")
    public List<Units> getUnits() {
        List<Units> categoryList = unitsRepository.findAll().stream().collect(Collectors.toList());
        return categoryList;
    }

/*

    @ModelAttribute("materialAll")
    public List<Material> getMaterial() {
        List<Material> materialList = materialRepository.findAll().stream()
                .collect(Collectors.toList());
        return materialList;
    }
*/


    @PostMapping("/addMaterial")
    public String addNewMaterial(Model model, @Valid String descr, String barcode, Long minimum, Long code,
                                 Long category, Long classes, Float buy, Float sell, Float profit_margin, Long unit,
                                 String notes) {
        log.error("{}", profit_margin);
        long count = materialRepository.findAll().stream().filter(e -> e.getDescribe().equalsIgnoreCase(descr)).count();

        if (count == 0L) {

            Material material = Material.builder()
                    .classId(classes)
                    .ProfitMargin(profit_margin)
                    .describe(descr)
                    .code(code)
                    .barcode(barcode)
                    .minimum(minimum)
                    .categoryId(category)
                    .buyPrice(buy)
                    .quantity(0L)
                    .notes(notes)
                    .sellPrice(sell)
                    .unit(unit)
                    .build();
            this.materialRepository.save(material);
        }


        return "redirect:material";

    }


    @PostMapping("/delMaterial")
    public String delMaterial(Long idValDelete) {
        log.info("start delete Material id  is {}", idValDelete);
        Optional<Material> material = materialRepository.findAll().stream().filter(e -> e.getId().equals(idValDelete)).findFirst();
        if (material.isPresent()) {
            materialRepository.delete(material.get());
            log.info("this  Material with this id  {} deleted ", idValDelete);
        }
        return "redirect:material";
    }


    @PostMapping("/editMaterial")
    public String editMaterial(Model model, @Valid String descrDtl, String barcodeDtl, Long minimumDtl, Long codeDtl, Long categoryDtl, Long classDtl, Long idVal, Float buyDtl, Float sellDtl, Long unitDtl) {
        log.info("start edit Material name is {} and id is {}", descrDtl, idVal);

        Optional<Material> materialFound = materialRepository.findAll().stream().filter(material -> material.getId().equals(idVal)).findFirst();
        if (materialFound.isPresent()) {
            Material material = materialFound.get();
            material.setDescribe(descrDtl);
            material.setMinimum(minimumDtl);
            material.setCategoryId(categoryDtl);
            material.setClassId(classDtl);
            material.setCode(codeDtl);
            material.setBarcode(barcodeDtl);
            material.setBuyPrice(buyDtl);
            material.setSellPrice(sellDtl);
            material.setUnit(unitDtl);
            materialRepository.save(material);
        }
        model.addAttribute("pageNo", 5);

        return "redirect:material";
    }


}
