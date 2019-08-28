package at.stores.store.view;

import at.stores.store.dao.*;
import at.stores.store.dto.MaterialBl;
import at.stores.store.dto.MovementDto;
import at.stores.store.entity.*;
import at.stores.store.service.MoveService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Controller
public class Reports {

    private final Logger log = LoggerFactory.getLogger(General.class);

    @Autowired
    MovementRepository movementRepository;

    @Autowired
    MoveDetailsRepository moveDetailsRepository;

    @Autowired
    ProviderRepository providerRepository;

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    ClassesRepository classesRepository;


    @Autowired
    MoveService moveService;

    @GetMapping("/reports")
    String pageReports() {
        return "reports";

    }


    @GetMapping("/dailySellingMovements")
    String pageDailySellDate(Model model, String moveDate1, String moveDate2) {

        if (!StringUtils.isEmpty(moveDate1)) {
            model.addAttribute("purchaseDate1", moveDate1);
            model.addAttribute("purchaseDate2", moveDate2);

            LocalDate localDate1 = fromStrToLocalDate(moveDate1);
            LocalDate localDate2 = fromStrToLocalDate(moveDate2);


            List<Movement> collect = movementRepository.findAllByMoveDateBetween(localDate1, localDate2)
                    .stream()
                    .filter(e -> e.getMoveType().equals(2))
                    .collect(Collectors.toList());

            List<MovementDto> movementDtoList = new ArrayList<>();

            for (Movement movement : collect) {
                List<MoveDetails> moveDetailsList = moveDetailsRepository.findAll().stream().filter(e -> e.getMoveId().equals(movement.getId())).collect(Collectors.toList());
                for (MoveDetails moveDetails : moveDetailsList) {

                    MovementDto movementDto = new MovementDto();
                    movementDto.setMoveDate(movement.getMoveDate());
                    movementDto.setBarcode(moveDetails.getBarcode());
                    Optional<Material> material = materialRepository.findById(moveDetails.getMaterialId());
                    movementDto.setMaterialName(material.get().getDescribe());
                    movementDto.setPrice(moveDetails.getPrice());
                    movementDto.setQuantity(moveDetails.getQuantity());
                    movementDto.setBillNo(movement.getBillNo());
                    movementDto.setTotal(moveDetails.getPrice() * moveDetails.getQuantity());
                    movementDtoList.add(movementDto);
                }
            }

            model.addAttribute("sellList", movementDtoList);
        }


        return "dailySellMovements";

    }


    @RequestMapping(value = "/dailyPurchasingMovements", method = RequestMethod.GET)
    String pageDailyDate1(Model model, String moveDate1, String moveDate2) throws ParseException {

        if (!StringUtils.isEmpty(moveDate1) && !StringUtils.isEmpty(moveDate2)) {
            model.addAttribute("purchaseDate1", moveDate1);
            model.addAttribute("purchaseDate2", moveDate2);

            LocalDate localDate1 = fromStrToLocalDate(moveDate1);
            LocalDate localDate2 = fromStrToLocalDate(moveDate2);


            List<Movement> collect = movementRepository.findAllByMoveDateBetween(localDate1, localDate2)
                    .stream()
                    .filter(e -> e.getMoveType().equals(1))
                    .collect(Collectors.toList());

            List<MovementDto> movementDtoList = new ArrayList<>();

            for (Movement movement : collect) {
                List<MoveDetails> moveDetailsList = moveDetailsRepository.findAll().stream().filter(e -> e.getMoveId().equals(movement.getId())).collect(Collectors.toList());
                for (MoveDetails moveDetails : moveDetailsList) {

                    MovementDto movementDto = new MovementDto();
                    movementDto.setBarcode(moveDetails.getBarcode());
                    Optional<Material> material = materialRepository.findById(moveDetails.getMaterialId());
                    movementDto.setMaterialName(material.get().getDescribe());
                    movementDto.setPrice(moveDetails.getPrice());
                    movementDto.setQuantity(moveDetails.getQuantity());
                    movementDto.setMoveDate(movement.getMoveDate());

                    movementDto.setBillNo(movement.getBillNo());
                    movementDto.setTotal(moveDetails.getPrice() * moveDetails.getQuantity());
                    Optional<Provider> provider = providerRepository.findById(movement.getProvider());
                    movementDto.setProviderName(provider.get().getName());
                    movementDtoList.add(movementDto);
                }
            }

            model.addAttribute("purchaseList", movementDtoList);
        }
        return "dailyPurchasingMovements";

    }


    @RequestMapping(value = "/dailyPurchasingMovements", method = RequestMethod.GET, params = "action=search")
    String pageDailyDate(Model model, String moveDate1, String moveDate2) throws ParseException {

        if (!StringUtils.isEmpty(moveDate1) && !StringUtils.isEmpty(moveDate2)) {
            model.addAttribute("purchaseDate1", moveDate1);
            model.addAttribute("purchaseDate2", moveDate2);

            LocalDate localDate1 = fromStrToLocalDate(moveDate1);
            LocalDate localDate2 = fromStrToLocalDate(moveDate2);


            List<Movement> collect = movementRepository.findAllByMoveDateBetween(localDate1, localDate2)
                    .stream()
                    .filter(e -> e.getMoveType().equals(1))
                    .collect(Collectors.toList());

            List<MovementDto> movementDtoList = new ArrayList<>();

            for (Movement movement : collect) {
                List<MoveDetails> moveDetailsList = moveDetailsRepository.findAll().stream().filter(e -> e.getMoveId().equals(movement.getId())).collect(Collectors.toList());
                for (MoveDetails moveDetails : moveDetailsList) {

                    MovementDto movementDto = new MovementDto();
                    movementDto.setBarcode(moveDetails.getBarcode());
                    Optional<Material> material = materialRepository.findById(moveDetails.getMaterialId());
                    movementDto.setMaterialName(material.get().getDescribe());
                    movementDto.setPrice(moveDetails.getPrice());
                    movementDto.setQuantity(moveDetails.getQuantity());
                    movementDto.setMoveDate(movement.getMoveDate());

                    movementDto.setBillNo(movement.getBillNo());
                    movementDto.setTotal(moveDetails.getPrice() * moveDetails.getQuantity());
                    Optional<Provider> provider = providerRepository.findById(movement.getProvider());
                    movementDto.setProviderName(provider.get().getName());
                    movementDtoList.add(movementDto);
                }
            }

            model.addAttribute("purchaseList", movementDtoList);
        }


        return "dailyPurchasingMovements";

    }


    @RequestMapping(value = "/dailyPurchasingMovementsN/{moveDate1}/{moveDate2}")
    String pageDailyDatePrint(@PathVariable("moveDate1") String moveDate1,
                              @PathVariable("moveDate2") String moveDate2,
                              Model model) throws ParseException {

        if (!StringUtils.isEmpty(moveDate1) && !StringUtils.isEmpty(moveDate2)) {
            model.addAttribute("purchaseDate1", moveDate1);
            model.addAttribute("purchaseDate2", moveDate2);

            LocalDate localDate1 = fromStrToLocalDate(moveDate1);
            LocalDate localDate2 = fromStrToLocalDate(moveDate2);


            List<Movement> collect = movementRepository.findAllByMoveDateBetween(localDate1, localDate2)
                    .stream()
                    .filter(e -> e.getMoveType().equals(1))
                    .collect(Collectors.toList());

            List<MovementDto> movementDtoList = new ArrayList<>();

            for (Movement movement : collect) {
                List<MoveDetails> moveDetailsList = moveDetailsRepository.findAll().stream().filter(e -> e.getMoveId().equals(movement.getId())).collect(Collectors.toList());
                for (MoveDetails moveDetails : moveDetailsList) {

                    MovementDto movementDto = new MovementDto();
                    movementDto.setBarcode(moveDetails.getBarcode());
                    Optional<Material> material = materialRepository.findById(moveDetails.getMaterialId());
                    movementDto.setMaterialName(material.get().getDescribe());
                    movementDto.setPrice(moveDetails.getPrice());
                    movementDto.setQuantity(moveDetails.getQuantity());
                    movementDto.setMoveDate(movement.getMoveDate());

                    movementDto.setBillNo(movement.getBillNo());
                    movementDto.setTotal(moveDetails.getPrice() * moveDetails.getQuantity());
                    Optional<Provider> provider = providerRepository.findById(movement.getProvider());
                    movementDto.setProviderName(provider.get().getName());
                    movementDtoList.add(movementDto);
                }
            }

            model.addAttribute("purchaseList", movementDtoList);
        }


        return "dailyPurchasingMovementsPrint";

    }


    @RequestMapping(value = "/dailyPurchasingMovementsP/{moveDate1}/{moveDate2}")
    String pageDailySellPrint(@PathVariable("moveDate1") String moveDate1,
                              @PathVariable("moveDate2") String moveDate2,
                              Model model) throws ParseException {

        if (!StringUtils.isEmpty(moveDate1)) {
            model.addAttribute("purchaseDate1", moveDate1);
            model.addAttribute("purchaseDate2", moveDate2);

            LocalDate localDate1 = fromStrToLocalDate(moveDate1);
            LocalDate localDate2 = fromStrToLocalDate(moveDate2);


            List<Movement> collect = movementRepository.findAllByMoveDateBetween(localDate1, localDate2)
                    .stream()
                    .filter(e -> e.getMoveType().equals(2))
                    .collect(Collectors.toList());

            List<MovementDto> movementDtoList = new ArrayList<>();

            for (Movement movement : collect) {
                List<MoveDetails> moveDetailsList = moveDetailsRepository.findAll().stream().filter(e -> e.getMoveId().equals(movement.getId())).collect(Collectors.toList());
                for (MoveDetails moveDetails : moveDetailsList) {

                    MovementDto movementDto = new MovementDto();
                    movementDto.setMoveDate(movement.getMoveDate());
                    movementDto.setBarcode(moveDetails.getBarcode());
                    Optional<Material> material = materialRepository.findById(moveDetails.getMaterialId());
                    movementDto.setMaterialName(material.get().getDescribe());
                    movementDto.setPrice(moveDetails.getPrice());
                    movementDto.setQuantity(moveDetails.getQuantity());
                    movementDto.setBillNo(movement.getBillNo());
                    movementDto.setTotal(moveDetails.getPrice() * moveDetails.getQuantity());
                    movementDtoList.add(movementDto);
                }
            }

            model.addAttribute("sellList", movementDtoList);
        }


        return "dailySellMovementsPrint";


    }


    @RequestMapping(value = "/monthlyPurchasingMovementsPrint/{purchaseDate}")
    String pageMonthlyPurchasPrint(@PathVariable("purchaseDate") String moveDate,
                                   Model model) throws ParseException {
        model.addAttribute("formMode", "print");

        monthlyRepo(model, moveDate);
        return "monthlyPurchasingMovements";
    }

    @GetMapping("/monthlyPurchasingMovements")
    String pageMonthlyPurchas(Model model, String moveDate) {
        model.addAttribute("formMode", "search");
        monthlyRepo(model, moveDate);
        return "monthlyPurchasingMovements";
    }

    @GetMapping("/monthlySellMovements")
    String pageMonthlySell(Model model, String moveDate) {
        model.addAttribute("formMode", "search");
        getReportMonthlySell(model, moveDate);
        return "monthlySellMovements";
    }

    @RequestMapping("/monthlySellMovementsPrint/{purchaseDate}")
    String pageMonthlySellPrint(Model model, @PathVariable("purchaseDate") String moveDate) {
        model.addAttribute("formMode", "print");
        getReportMonthlySell(model, moveDate);
        return "monthlySellMovements";
    }


    private void monthlyRepo(Model model, String moveDate) {
        if (!StringUtils.isEmpty(moveDate)) {
            model.addAttribute("purchaseDate", moveDate);
            Integer year = Integer.parseInt(moveDate.substring(0, 4));
            Integer month = Integer.parseInt(moveDate.substring(5, 7));


            List<MovementDto> movementDtoList = new ArrayList<>();
            List<Material> materialList = materialRepository.findAll().stream().collect(Collectors.toList());
            Float totalAll = 0.0F;
            for (Material material : materialList) {
                MovementDto movementDto = new MovementDto();
                movementDto.setMaterialName(material.getDescribe());
                movementDto.setBarcode(material.getBarcode());
                Long quantity = 0L;
                Float total = 0.0F;

                List<Movement> movementList = movementRepository.findAll()
                        .stream()
                        .filter(e -> e.getMoveType().equals(1))
                        .collect(Collectors.toList());
                for (Movement movement : movementList) {
                    if ((movement.getMoveDate().getYear() == year) &
                            (movement.getMoveDate().getMonth().getValue() == month)) {
                        List<MoveDetails> moveDetailsList = moveDetailsRepository.findAll()
                                .stream()
                                .filter(e -> e.getMoveId().equals(movement.getId()))
                                .filter(e -> e.getMaterialId().equals(material.getId()))
                                .collect(Collectors.toList());
                        for (MoveDetails moveDetails : moveDetailsList) {
                            quantity = quantity + moveDetails.getQuantity();
                            total = total + moveDetails.getPrice() * moveDetails.getQuantity();
                            totalAll = totalAll + moveDetails.getPrice() * moveDetails.getQuantity();

                        }

                    }
                }
                movementDto.setQuantity(quantity);
                movementDto.setTotal(total);
                movementDtoList.add(movementDto);


            }

            model.addAttribute("totalAll", totalAll);

            model.addAttribute("purchaseList", movementDtoList);
        }
    }


    private void getReportMonthlySell(Model model, String moveDate) {
        if (!StringUtils.isEmpty(moveDate)) {
            model.addAttribute("purchaseDate", moveDate);
            Integer year = Integer.parseInt(moveDate.substring(0, 4));
            Integer month = Integer.parseInt(moveDate.substring(5, 7));


            List<MovementDto> movementDtoList = new ArrayList<>();
            List<Material> materialList = materialRepository.findAll().stream().collect(Collectors.toList());
            Float totalAll = 0.0F;
            for (Material material : materialList) {
                MovementDto movementDto = new MovementDto();
                movementDto.setMaterialName(material.getDescribe());
                movementDto.setBarcode(material.getBarcode());
                Long quantity = 0L;
                Float total = 0.0F;

                List<Movement> movementList = movementRepository.findAll()
                        .stream()
                        .filter(e -> e.getMoveType().equals(2))
                        .collect(Collectors.toList());
                for (Movement movement : movementList) {
                    if ((movement.getMoveDate().getYear() == year) &
                            (movement.getMoveDate().getMonth().getValue() == month)) {
                        List<MoveDetails> moveDetailsList = moveDetailsRepository.findAll()
                                .stream()
                                .filter(e -> e.getMoveId().equals(movement.getId()))
                                .filter(e -> e.getMaterialId().equals(material.getId()))
                                .collect(Collectors.toList());
                        for (MoveDetails moveDetails : moveDetailsList) {
                            quantity = quantity + moveDetails.getQuantity();
                            total = total + moveDetails.getPrice() * moveDetails.getQuantity();
                            totalAll = totalAll + moveDetails.getPrice() * moveDetails.getQuantity();

                        }

                    }
                }
                movementDto.setQuantity(quantity);
                movementDto.setTotal(total);
                movementDtoList.add(movementDto);


            }

            model.addAttribute("totalAll", totalAll);

            model.addAttribute("purchaseList", movementDtoList);
        }
    }


    private LocalDate fromStrToLocalDate(String strDate) {
        String subYear = strDate.substring(0, 4);
        String subMonth = strDate.substring(5, 7);
        String subDay = strDate.substring(8, 10);


        Integer year = Integer.parseInt(subYear);
        Integer month = Integer.parseInt(subMonth);
        Integer day = Integer.parseInt(subDay);
        return LocalDate.of(year, month, day);

    }


    @RequestMapping(value = "/materialBalancePrint")
    public String getMaterialBlPrint(Model model) {
        model.addAttribute("formMode", "print");
        getBlance(model);
        return "materialBlnc";

    }


    @GetMapping("/materialBalance")
    public String getMaterialBl(Model model) {
        model.addAttribute("formMode", "view");
        getBlance(model);
        return "materialBlnc";
    }

    private void getBlance(Model model) {
        model.addAttribute("thisReportName", "Inventory Material");
        List<MaterialBl> materialBlList = new ArrayList<>();
        List<MaterialBl> mList = new ArrayList<>();

        Long no = 1L;
        materialRepository.findAll()
                .stream()
                .forEach(e -> {
                    MaterialBl ma = MaterialBl
                            .builder()
                            .no(1L)
                            .id(e.getId())
                            .describe(e.getDescribe())
                            .bl(0L)
                            .in(0L)
                            .out(0L)
                            .build();
                    materialBlList.add(ma);

                });

        List<Movement> movementList = movementRepository.findAll()
                .stream().collect(Collectors.toList());
        no = 1L;
        for (MaterialBl materialBl : materialBlList) {
            Long in = 0L;
            Long out = 0L;
            for (Movement movement : movementList) {
                Optional<MoveDetails> moveDetails = moveDetailsRepository.findAll()
                        .stream()
                        .filter(mv -> mv.getMoveId().equals(movement.getId()))
                        .filter(mt -> mt.getMaterialId().equals(materialBl.getId()))
                        .findFirst();
                if (moveDetails.isPresent()) {
                    if (movement.getMoveType().equals(1)) {
                        in = in + moveDetails.get().getQuantity();
                    } else {
                        out = out + moveDetails.get().getQuantity();
                    }
                }
            }
            materialBl.setIn(in);
            materialBl.setOut(out);
            if (in >= out) {
                materialBl.setBl(in - out);
            } else {
                materialBl.setBl(99999999999999l);
            }
            materialBl.setNo(no);
            no = no + 1;
            mList.add(materialBl);
        }
        model.addAttribute("materialList", mList);
    }


    @GetMapping("/minimumBalance")
    public String getMinimumBalance(Model model) {
        model.addAttribute("formMode", "view");

        model.addAttribute("thisReportName", "Material with a balance of less than the minimum");

        List<MaterialBl> minimumBalanceAsService = moveService.getMinimumBalanceAsService();
        model.addAttribute("materialList", minimumBalanceAsService);
        return "materialBlnc";
    }

    @GetMapping("/minimumBalancePrint")
    public String getMinimumBalancePrint(Model model) {
        model.addAttribute("formMode", "print");

        model.addAttribute("thisReportName", "Material with a balance of less than the minimum");

        List<MaterialBl> minimumBalanceAsService = moveService.getMinimumBalanceAsService();
        model.addAttribute("materialList", minimumBalanceAsService);
        return "materialBlnc";
    }


    @GetMapping("/minimumClasses")
    public String getMinimumClasses(Model model) {
        model.addAttribute("thisReportName", "Classes Material with a balance of less than the minimum");
        model.addAttribute("formMode", "view");

        List<MaterialBl> collect = moveService.getMinimumClassesAsService();
        model.addAttribute("materialList", collect);
        return "materialBlnc";
    }


    @GetMapping("/minimumClassesPrint")
    public String getMinimumClassesPrint(Model model) {
        model.addAttribute("thisReportName", "Classes Material with a balance of less than the minimum");
        model.addAttribute("formMode", "Print");

        List<MaterialBl> collect = moveService.getMinimumClassesAsService();
        model.addAttribute("materialList", collect);
        return "materialBlnc";
    }

    @GetMapping("/materialMovement")
    public String getmaterialMovement(Model model, String moveDate1, String moveDate2, String barcode, String materialName, String mcode) {
        model.addAttribute("formMode", "search");
        getMaterialMovements(model, moveDate1, moveDate2, barcode, materialName, mcode);
        return "materialMovement";

    }

    @RequestMapping("/materialMovementPrint/{moveDate1}/{moveDate2}/{mcode}/{barcode}/{materialName}")
    String getmaterialMovementPrint(Model model,
                                    @PathVariable("moveDate1") String moveDate1,
                                    @PathVariable("moveDate2") String moveDate2,
                                    @PathVariable("barcode") String barcode,
                                    @PathVariable("materialName") String materialName,
                                    @PathVariable("mcode") String mcode
    ) {
        model.addAttribute("formMode", "print");
        getMaterialMovements(model, moveDate1, moveDate2, barcode, materialName, mcode);
        return "materialMovement";
    }

    private void getMaterialMovements(Model model, String moveDate1, String moveDate2, String barcode, String materialName, String mcode) {
        if ((!StringUtils.isEmpty(moveDate1)) && (!StringUtils.isEmpty(moveDate2))) {

            model.addAttribute("purchaseDate1", moveDate1);
            model.addAttribute("purchaseDate2", moveDate2);
            model.addAttribute("barcode", barcode);
            model.addAttribute("mcode", mcode);
            model.addAttribute("materialName", materialName);

            Long materialCode = Long.parseLong(mcode);

            LocalDate localDate1 = fromStrToLocalDate(moveDate1);
            LocalDate localDate2 = fromStrToLocalDate(moveDate2);


            List<MaterialBl> blCollect = new ArrayList<>();

            List<Movement> collect1 = movementRepository.findAllByMoveDateBetween(localDate1, localDate2).stream().collect(Collectors.toList());
            for (Movement movement : collect1) {
                List<MoveDetails> collect = moveDetailsRepository.findAll()
                        .stream()
                        .filter(e -> e.getMoveId().equals(movement.getId()))
                        .filter(mt -> mt.getMaterialId().equals(materialCode))
                        .collect(Collectors.toList());
                for (MoveDetails moveDetails : collect) {
                    Long in = 0L;
                    Long out = 0L;

                    String providerName = "";

                    Optional<Provider> provider = providerRepository.findById(movement.getProvider());
                    if (provider.isPresent()) {
                        providerName = provider.get().getName();
                    }


                    if (movement.getMoveType().equals(1)) {
                        in = moveDetails.getQuantity();
                    } else {
                        out = moveDetails.getQuantity();
                    }


                    MaterialBl any = MaterialBl.builder()
                            .in(in)
                            .out(out)
                            .moveDate(movement.getMoveDate())
                            .describe(providerName)
                            .price(moveDetails.getPrice())
                            .billNo(movement.getBillNo())
                            .total(moveDetails.getPrice() * moveDetails.getQuantity())
                            .build();
                    blCollect.add(any);
                }


            }

            model.addAttribute("purchaseList", blCollect);

        }
    }

    @GetMapping("/classesMovement")
    public String getClassMovementPrint(Model model, String moveDate1, String moveDate2, String classDtl) {
        classMovements(model, moveDate1, moveDate2, classDtl);
        model.addAttribute("formMode", "search");
        return "classesMovement";
    }


    @RequestMapping(value = "/classesMovementPrint/{moveDate1}/{moveDate2}/{classDtl}")
    public String getClassMovement(Model model,
                                   @PathVariable("moveDate1") String moveDate1,
                                   @PathVariable("moveDate2") String moveDate2,
                                   @PathVariable("classDtl") String classDtl) {

        classMovements(model, moveDate1, moveDate2, classDtl);
        model.addAttribute("formMode", "print");
        return "classesMovement";
    }

    private void classMovements(Model model, String moveDate1, String moveDate2, String classDtl) {
        List<Classes> classesList = classesRepository.findAll().stream().collect(Collectors.toList());
        model.addAttribute("classesAll", classesList);
        if ((!StringUtils.isEmpty(moveDate1)) && (!StringUtils.isEmpty(moveDate2)) && (classDtl != null)) {

            Long classId = Long.parseLong(classDtl);

            Optional<Classes> classMaterial = classesRepository.findById(classId);
            if (classMaterial.isPresent()) {
                model.addAttribute("className", classMaterial.get().getDescribe());
            }
            model.addAttribute("classId", classId);

            model.addAttribute("purchaseDate1", moveDate1);
            model.addAttribute("purchaseDate2", moveDate2);


            LocalDate localDate1 = fromStrToLocalDate(moveDate1);
            LocalDate localDate2 = fromStrToLocalDate(moveDate2);


            List<MaterialBl> blCollect = new ArrayList<>();

            List<Movement> collect1 = movementRepository.findAllByMoveDateBetween(localDate1, localDate2).stream().collect(Collectors.toList());
            for (Movement movement : collect1) {
                List<MoveDetails> collect = moveDetailsRepository.findAll()
                        .stream()
                        .filter(e -> e.getMoveId().equals(movement.getId()))
                        //.filter(mt -> mt.getMaterialId().equals(materialCode))
                        .collect(Collectors.toList());
                for (MoveDetails moveDetails : collect) {
                    Long in = 0L;
                    Long out = 0L;

                    String providerName = "";

                    Optional<Provider> provider = providerRepository.findById(movement.getProvider());
                    if (provider.isPresent()) {
                        providerName = provider.get().getName();
                    }


                    if (movement.getMoveType().equals(1)) {
                        in = moveDetails.getQuantity();
                    } else {
                        out = moveDetails.getQuantity();
                    }


                    MaterialBl any = MaterialBl.builder()
                            .in(in)
                            .out(out)
                            .moveDate(movement.getMoveDate())
                            .describe(providerName)
                            .price(moveDetails.getPrice())
                            .billNo(movement.getBillNo())
                            .total(moveDetails.getPrice() * moveDetails.getQuantity())
                            .build();
                    blCollect.add(any);
                }


            }

            model.addAttribute("purchaseList", blCollect);

        }
    }


}
