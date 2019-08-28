package at.stores.store.view;

import at.stores.store.dao.CategoryRepository;
import at.stores.store.dao.ProviderRepository;
import at.stores.store.entity.Category;
import at.stores.store.entity.Provider;
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
public class ProviderView {

    private final Logger log = LoggerFactory.getLogger(CategoryView.class);

    @Autowired
    ProviderRepository providerRepository;



    @ModelAttribute("providers")
    public List<Provider> getProviders() {
        List<Provider> providerList = providerRepository.findAll().stream().collect(Collectors.toList());
        return providerList;
    }

    @PreAuthorize("isAllowed('Provider')")
    @GetMapping("/provider")
    public String getProvider() {
        return "addProvider";
    }

    @PostMapping("/provider")
    public String addNewProvider(@Valid String descr) {
        long count = providerRepository.findAll().stream().filter(e -> e.getName().equalsIgnoreCase(descr)).count();
        if (count == 0L) {
            Provider newProvider = new Provider(descr);
            providerRepository.save(newProvider);
        }
        return "redirect:provider";

    }


    @PostMapping("/editProvider")
    public String editProvider(@Valid String descrDtl, Long idVal) {
        log.info("start edit Category name is {} and id is {}", descrDtl, idVal);
        Optional<Provider> first = providerRepository.findAll().stream().filter(category -> category.getId().equals(idVal)).findFirst();
        if (first.isPresent()) {
            Provider category = first.get();
            category.setName(descrDtl);
            providerRepository.save(category);
        }

        return "redirect:provider";
    }

    @PostMapping("/delProvider")
    public String delProvider(Long idValDelete) {
        log.info("start delete Provider id  is {}", idValDelete);
        Optional<Provider> first = providerRepository.findAll().stream().filter(category -> category.getId().equals(idValDelete)).findFirst();
        if (first.isPresent()) {
            providerRepository.delete(first.get());
        }
        return "redirect:provider";
    }

}
