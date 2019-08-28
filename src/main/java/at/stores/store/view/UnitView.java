package at.stores.store.view;

import at.stores.store.dao.UnitsRepository;
import at.stores.store.entity.Classes;
import at.stores.store.entity.Units;
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
public class UnitView {

    private final Logger log = LoggerFactory.getLogger(UnitView.class);

    @Autowired
    UnitsRepository unitsRepository;

    @ModelAttribute("unitsList")
    public List<Units> getUnitList() {
        List<Units> unitsList = unitsRepository.findAll().stream().collect(Collectors.toList());
        return unitsList;
    }

    @GetMapping("/units")
    @PreAuthorize("isAllowed('units')")
    public String getUnits() {
        log.info("start Units Page");
        return "units";
    }

    @PostMapping("/unitsAdd")
    public String addNewClass(@Valid String descr) {
        long count = unitsRepository.findAll().stream().filter(e -> e.getDescribe().equalsIgnoreCase(descr)).count();
        if (count == 0L) {
            unitsRepository.save(new Units(descr));
        }
        return "redirect:units";

    }


    @PostMapping("/editUnit")
    public String editUnit(@Valid String descrDtl, Long idVal) {
        log.info("start edit Unit name is {} and id is {}", descrDtl, idVal);
        Optional<Units> first = unitsRepository.findAll().stream().filter(unit -> unit.getId().equals(idVal)).findFirst();

        if (first.isPresent()) {
            Units units = first.get();
            units.setDescribe(descrDtl);
            unitsRepository.save(units);
        }

        return "redirect:units";

    }

    @PostMapping("/delUnit")
    public String delClass(Long idValDelete) {
        log.info("start delete Unit id is  {}", idValDelete);
        Optional<Units> first = unitsRepository.findAll().stream().filter(classes -> classes.getId().equals(idValDelete)).findFirst();
        if (first.isPresent()) {
            Units units = first.get();
            unitsRepository.delete(units);
        }
        return "redirect:units";

    }


}
