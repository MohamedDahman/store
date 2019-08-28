package at.stores.store.view;

import at.stores.store.dao.*;
import at.stores.store.dto.MaterialBl;
import at.stores.store.dto.ResultClass;
import at.stores.store.entity.*;
import at.stores.store.security.UserPrincipal;
import at.stores.store.service.MoveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class General {

    private final Logger log = LoggerFactory.getLogger(General.class);

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ClassesRepository classesRepository;

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    ProviderRepository providerRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MoveDetailsRepository moveDetailsRepository;

    @Autowired
    MovementRepository movementRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    MoveService moveService;


    @Autowired
    UnitsRepository unitsRepository;


    @GetMapping("getMaterial")
    Material getMaterial(@Valid Material material) {

        log.info("Get Material  information Describe is  {}", material.getDescribe());

        Optional<Material> materialFound = materialRepository.findAll().stream().filter(cat -> cat.getDescribe().equalsIgnoreCase(material.getDescribe())).findFirst();

        if (materialFound.isPresent())
            return materialFound.get();
        else {
            return new Material("No Material");
        }
    }

    @GetMapping("getBarcode")
    Material getBarcode(@Valid Material material) {

        log.info("Get Material  information through Barcode  is  {}", material.getBarcode());

        Optional<Material> materialFound = materialRepository.findAll().stream().filter(cat -> cat.getBarcode().equals(material.getBarcode())).findFirst();

        if (materialFound.isPresent())
            return materialFound.get();
        else {
            return new Material("No Material");
        }
    }


    @GetMapping("getCategory")
    Category getCategory(@Valid Category category) {

        log.info("Get Category information Describe is  {}", category.getDescribe());

        Optional<Category> firstColor = categoryRepository.findAll().stream().filter(cat -> cat.getDescribe().equalsIgnoreCase(category.getDescribe())).findFirst();

        if (firstColor.isPresent())
            return firstColor.get();
        else {
            Category emptyCategory = new Category("No Category");

            return emptyCategory;
        }
    }


    @GetMapping("getClasses")
    Classes getCategory(@Valid Classes classes) {

        log.info("Get Classes information Describe is  {}", classes.getDescribe());

        Optional<Classes> firstClass = classesRepository.findAll().stream().filter(cat -> cat.getDescribe().equalsIgnoreCase(classes.getDescribe())).findFirst();

        if (firstClass.isPresent())
            return firstClass.get();
        else {
            Classes emptyClasses = new Classes("No Class");

            return emptyClasses;
        }
    }

    @GetMapping("testClassesDescribe")
    Classes testClassDescribe(@Valid Classes classes) {

        log.info("test Classes information Describe is  {} and id is {}", classes.getDescribe(), classes.getId());

        Optional<Classes> firstClass = classesRepository.findAll().stream().filter(cat -> cat.getDescribe().equalsIgnoreCase(classes.getDescribe())).findFirst();
        if (firstClass.isPresent()) {
            if (!firstClass.get().getId().equals(classes.getId())) {
                return firstClass.get();
            } else {
                return new Classes("this describe already exist");
            }

        } else {
            return new Classes("No Class");
        }
    }


    @GetMapping("getProvider")
    Provider getProvider(@Valid Provider provider) {

        log.info("Get Provider  information Name is  {}", provider.getName());

        Optional<Provider> providerFound = providerRepository.findAll().stream().filter(e -> e.getName().equalsIgnoreCase(provider.getName())).findFirst();

        if (providerFound.isPresent())
            return providerFound.get();
        else {
            return new Provider("No Provider");
        }
    }


    @GetMapping("getMaterialByBarcode")
    public Material getMaterialNoByBarcodeValue(Material material) {
        log.info("start1 get Material By Barcode Value {}", material.getBarcode());

        Optional<Material> materialFound = materialRepository.findAll().stream().filter(e -> e.getBarcode().equalsIgnoreCase(material.getBarcode())).findFirst();
        if (materialFound.isPresent()) {
            log.info("found {}", materialFound.get().getId());
            Material material1 = materialFound.get();

            return material1;
        } else
            return new Material("No Material");
    }

    @GetMapping("/checkLogin")
    Users checkLogin(@Valid Users user2) {
        log.info("check if Login is Exists  : {}", user2.getLogin());
        Optional<Users> firstLogin = userRepository.findAll().stream().filter(e -> e.getLogin().equals(user2.getLogin())).findFirst();
        if (firstLogin.isPresent()) {
            log.info("this Login is Exists  : {}", user2.getLogin());
            return firstLogin.get();
        } else {
            log.info("this Login Name doesn't  Exists  : {}", user2.getLogin());
            Users user3 = new Users();
            user3.setId(-1L);
            return user3;
        }
    }


    @GetMapping("checkPassword")
    Users checkPassword(@Valid Users currentPassword) {
        log.info("Get information Describe is  {}", currentPassword.getPassword());
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Users> users = userRepository.findOneByLogin(userPrincipal.getUsername());
        log.info(userPrincipal.getPassword());
        boolean result = passwordEncoder.matches(currentPassword.getPassword(), users.get().getPassword());
        if (result) {
            return new Users("USER Exists");
        } else {
            return new Users("No User");
        }
    }


    @GetMapping("testUnitDescribe")
    Units testUnitDescribe(@Valid Units units) {

        log.info("test Unit information Describe is  {} and id is {}", units.getDescribe(), units.getId());

        Optional<Units> first = unitsRepository.findAll().stream().filter(cat -> cat.getDescribe().equalsIgnoreCase(units.getDescribe())).findFirst();
        if (first.isPresent()) {
            if (!first.get().getId().equals(units.getId())) {
                return first.get();
            } else {
                return new Units("this describe already exist");
            }

        } else {
            return new Units("No Unit");
        }
    }


    @GetMapping("getUnit")
    Units getUnit(@Valid Units units) {

        log.info("Get Classes information Describe is  {}", units.getDescribe());

        Optional<Units> firstClass = unitsRepository.findAll().stream().filter(cat -> cat.getDescribe().equalsIgnoreCase(units.getDescribe())).findFirst();

        if (firstClass.isPresent())
            return firstClass.get();
        else {
            Units emptyClasses = new Units("No Unit");

            return emptyClasses;
        }
    }


    @GetMapping("/minimumBalanceNotification")
    public ResultClass getMinimumBalance() {

        log.info("Notification");

        List<MaterialBl> minimumBalanceAsService = moveService.getMinimumBalanceAsService();
        if (minimumBalanceAsService.size() == 0) {
            return new ResultClass("No Data");
        } else {
            return new ResultClass("Data");
        }

    }


    @GetMapping("/minimumClassesNotification")
    public ResultClass getminimumClasses() {

        log.info("Notification minimum Classes");

        List<MaterialBl> minimumBalanceAsService = moveService.getMinimumClassesAsService();

        if (minimumBalanceAsService.size() == 0) {
            return new ResultClass("No Data");
        } else {
            return new ResultClass("Data");
        }

    }


    public Long getMaterialBl(Long materialCode) {
        log.info("start get material Balance");
        List<Movement> movementList = movementRepository.findAll()
                .stream().collect(Collectors.toList());
        Long no = 1L;
        Long in = 0L;
        Long out = 0L;
        for (Movement movement : movementList) {
            Optional<MoveDetails> moveDetails = moveDetailsRepository.findAll()
                    .stream()
                    .filter(mv -> mv.getMoveId().equals(movement.getId()))
                    .filter(mt -> mt.getMaterialId().equals(materialCode))
                    .findFirst();
            if (moveDetails.isPresent()) {
                if (movement.getMoveType().equals(1)) {
                    in = in + moveDetails.get().getQuantity();
                } else {
                    out = out + moveDetails.get().getQuantity();
                }
            }
        }


        return in - out;
    }


}
