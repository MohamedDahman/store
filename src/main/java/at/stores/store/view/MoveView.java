package at.stores.store.view;

import at.stores.store.dao.*;
import at.stores.store.dto.MoveDTO;
import at.stores.store.entity.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Controller
public class MoveView {

    private final Logger log = LoggerFactory.getLogger(ClassesView.class);

    @Autowired
    ProviderRepository providerRepository;

    @Autowired
    MovementRepository movementRepository;

    @Autowired
    MoveDetailsRepository moveDetailsRepository;

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    UserRepository userRepository;

    private Movement movement = new Movement();
    private MoveDetails moveDetails = new MoveDetails();
    private List<MoveDTO> moveDTOList = new ArrayList<>();

    @ModelAttribute("providerAll")
    public List<Provider> getProviders() {
        return providerRepository.findAll().stream().collect(Collectors.toList());
    }

    @ModelAttribute("movementData")
    public Movement getMovement() {
        return movement;
    }

    @ModelAttribute("moveDtoAll")
    public List<MoveDTO> getMoveDTOList() {
        long count = moveDTOList.stream().map(e -> e.getBarcode()).filter(e1 -> Strings.isEmpty(e1)).count();
        if (count == 0L) {
            for (int i = 0; i < 8; i++) {
                MoveDTO moveDTO = new MoveDTO();
                moveDTO.setRowNo(i);
                moveDTOList.add(moveDTO);
            }

        }

        return moveDTOList;
    }


    @GetMapping("/moveIn")
    public String getMoveIn() {
        log.info("start move in Page");
        return "moveIn";
    }

    @GetMapping("/moveOut")
    public String getMoveOut(Model model) {
        log.info("start move out Page");
        LocalDateTime now = LocalDateTime.now();
        String strDate = now.toString();
        String subYear = strDate.substring(0, 4);
        String subMonth = strDate.substring(5, 7);
        String subDay = strDate.substring(8, 10);


        Integer year = Integer.parseInt(subYear);
        Integer month = Integer.parseInt(subMonth);
        Integer day = Integer.parseInt(subDay);
        LocalDate of = LocalDate.of(year, month, day);

        model.addAttribute("md", of);

        return "moveOut";
    }

    @PostMapping("/addNewRowMaterial")
    public String addNew(HttpServletRequest request) {
        log.info("start1 add new Row");
        return "moveIn";
    }


    @RequestMapping(value = "/saveMoveIn", method = RequestMethod.POST, params = "action=save")
    public String saveMoveIn(HttpServletRequest request, Principal principal) throws ParseException {
        log.info("Save Move In ");
        Optional<Users> optionalUser = userRepository.findOneByLogin(principal.getName());

        String moveDate = request.getParameter("moveDate");
        String subYear = moveDate.substring(0, 4);
        String subMonth = moveDate.substring(5, 7);
        String subDay = moveDate.substring(8, 10);

        Integer year = Integer.parseInt(subYear);
        Integer month = Integer.parseInt(subMonth);
        Integer day = Integer.parseInt(subDay);
        LocalDate localDate = LocalDate.of(year, month, day);

        long provider = Long.parseLong(request.getParameter("provider"));
        String billNo = request.getParameter("billNo");
        Movement movement = Movement.builder()
                .provider(provider)
                .billNo(billNo)
                .moveType(1)
                .moveDate(localDate)
                .status(1).userNo(optionalUser.get().getId())
                .build();
        Movement save = movementRepository.save(movement);


        for (int i = 0; i < 8; i++) {
            String barcode = request.getParameter("bar" + i);
            String quantity = request.getParameter("qua" + i);
            String price = request.getParameter("pri" + i);
            String materialName = request.getParameter("name" + i);
            String code = request.getParameter("code" + i);
            if (!Strings.isEmpty(code) && !Strings.isEmpty(barcode) && !Strings.isEmpty(quantity) && !Strings.isEmpty(price) & !Strings.isEmpty(materialName)) {
                MoveDetails moveDetails = new MoveDetails();

                moveDetails.setBarcode(barcode);
                moveDetails.setQuantity(Long.parseLong(quantity));
                moveDetails.setPrice(Float.parseFloat(price));
                moveDetails.setMaterialId(Long.parseLong(code));
                moveDetails.setMoveId(save.getId());
                moveDetailsRepository.save(moveDetails);

                log.info("barcode value {} ,  {} , {}", barcode, quantity, price);
                Optional<Material> material = materialRepository.findById(Long.parseLong(code));
                if (material.isPresent()) {
                    float v = (material.get().getProfitMargin() * Float.parseFloat(price) * 0.01F) + Float.parseFloat(price);
                    material.get().setSellPrice(v);
                    Long quantity1 = material.get().getQuantity();
                    material.get().setQuantity(quantity1 + Long.parseLong(quantity));
                    material.get().setSellPrice(v);
                    material.get().setBuyPrice(Float.parseFloat(price));
                    materialRepository.save(material.get());
                }
            }


        }


        return "moveIn";
    }


    @RequestMapping(value = "/saveMoveOut", method = RequestMethod.POST, params = "action=save")
    public String saveMoveOut(HttpServletRequest request, Principal principal) throws ParseException {
        log.info("Save Move In ");
        Optional<Users> optionalUser = userRepository.findOneByLogin(principal.getName());


        String moveDate = request.getParameter("moveDate");
        String subYear = moveDate.substring(0, 4);
        String subMonth = moveDate.substring(5, 7);
        String subDay = moveDate.substring(8, 10);

        Integer year = Integer.parseInt(subYear);
        Integer month = Integer.parseInt(subMonth);
        Integer day = Integer.parseInt(subDay);
        LocalDate localDate = LocalDate.of(year, month, day);

        long provider = 0;
        //long provider = Long.parseLong(request.getParameter("provider"));


        String billNo = "0";
        Movement movement = Movement.builder()
                .provider(provider)
                .billNo(billNo)
                .moveType(2)
                .moveDate(localDate)
                .status(1)
                .userNo(optionalUser.get().getId())
                .build();


        Movement save = movementRepository.save(movement);


        for (int i = 0; i < 8; i++) {
            String barcode = request.getParameter("bar" + i);
            String quantity = request.getParameter("qua" + i);
            String price = request.getParameter("pri" + i);
            String materialName = request.getParameter("name" + i);
            String code = request.getParameter("code" + i);
            if (!Strings.isEmpty(code) && !Strings.isEmpty(barcode) && !Strings.isEmpty(quantity) && !Strings.isEmpty(price) & !Strings.isEmpty(materialName)) {
                MoveDetails moveDetails = MoveDetails.builder()
                        .barcode(barcode)
                        .quantity(Long.parseLong(quantity))
                        .materialId(Long.parseLong(code))
                        .price(Float.parseFloat(price))
                        .moveId(save.getId())
                        .build();
                moveDetailsRepository.save(moveDetails);
                log.info("barcode value {} ,  {} , {}", barcode, quantity, price);
                Optional<Material> material = materialRepository.findById(Long.parseLong(code));
                if (material.isPresent()) {
                    Long quantity1 = material.get().getQuantity();
                    material.get().setQuantity(quantity1 - Long.parseLong(quantity));
                    materialRepository.save(material.get());
                }

            }


        }


        return "moveOut";
    }


    @RequestMapping(value = "/saveMoveIn", method = RequestMethod.POST, params = "action=clear")
    public String clearAll() {
        log.info("start clear Method");
        return "moveIn";
    }


    @GetMapping("/purchaseSell")
    @PreAuthorize("isAllowed('purchase')")
    public String getPurchaseSell(Model model) {
        LocalDateTime now = LocalDateTime.now();
        String strDate = now.toString();

        String subYear = strDate.substring(0, 4);
        String subMonth = strDate.substring(5, 7);
        String subDay = strDate.substring(8, 10);


        Integer year = Integer.parseInt(subYear);
        Integer month = Integer.parseInt(subMonth);
        Integer day = Integer.parseInt(subDay);
        LocalDate of = LocalDate.of(year, month, day);

        model.addAttribute("md", of);

        int recordCount = 1;
        model.addAttribute("recordCount", recordCount);
        List<MoveDTO> dtoList = new ArrayList<>();
        dtoList.add(new MoveDTO(recordCount - 1));

        model.addAttribute("purchaseAll", dtoList);

        return "purchaseSell";
    }

    @PreAuthorize("isAllowed('sell')")
    @GetMapping("/purchase")
    public String getPurchase(Model model) {
        log.info("start purchase");
        LocalDateTime now = LocalDateTime.now();
        String strDate = now.toString();

        String subYear = strDate.substring(0, 4);
        String subMonth = strDate.substring(5, 7);
        String subDay = strDate.substring(8, 10);


        Integer year = Integer.parseInt(subYear);
        Integer month = Integer.parseInt(subMonth);
        Integer day = Integer.parseInt(subDay);
        LocalDate of = LocalDate.of(year, month, day);

        model.addAttribute("md", of);

        int recordCount = 1;
        model.addAttribute("recordCount", recordCount);
        List<MoveDTO> dtoList = new ArrayList<>();
        dtoList.add(new MoveDTO(recordCount - 1));

        model.addAttribute("purchaseAll", dtoList);

        return "purchase";
    }


    @RequestMapping(value = "/savePurchase", method = RequestMethod.POST, params = "action=addNew")
    public String savePurchase(Model model, HttpServletRequest request, Principal principal) throws ParseException {

        log.info("Add New Item in Purchase");
        String countR = request.getParameter("recordCount");
        Integer recordCount = Integer.parseInt(countR);


        String moveDate = request.getParameter("moveDate");
        if (!StringUtils.isEmpty(moveDate)) {
            LocalDate localDate = fromStrToLocalDate(moveDate);
            model.addAttribute("md", localDate);
        }


        List<MoveDTO> dtoList = new ArrayList<>();
        int valMinus = 0;
        for (int i = 0; i < recordCount; i++) {
            String barcode = request.getParameter("bar" + i);
            String materialName = request.getParameter("name" + i);
            String quantity = request.getParameter("qua" + i);
            String price = request.getParameter("pri" + i);
            String code = request.getParameter("code" + i);
            // String bln = request.getParameter("bln" + i);
            if ((!StringUtils.isEmpty(barcode)) && (!StringUtils.isEmpty(materialName)) && (!StringUtils.isEmpty(quantity)) && (!StringUtils.isEmpty(price)) && (!StringUtils.isEmpty(code))) {
                Optional<MoveDTO> moveDTOOptional = dtoList.stream().filter(e -> e.getBarcode().equals(barcode)).findAny();
                if (moveDTOOptional.isPresent()) {
                    Long quantity1 = moveDTOOptional.get().getQuantity();
                    long l = quantity1 + Long.parseLong(quantity);
                    Float allPrice = l * Float.parseFloat(price);
                    moveDTOOptional.get().setQuantity(l);
                    moveDTOOptional.get().setAllPrice(allPrice);
                    if (l == 0) {
                        dtoList.remove(moveDTOOptional.get());
                    }
                    valMinus++;
                } else {

                    MoveDTO build = MoveDTO.builder()
                            .barcode(barcode)
                            .materialName(materialName)
                            .materialId(Long.parseLong(code))
                            .quantity(Long.parseLong(quantity))
                            //               .quantityBalance(Long.parseLong(bln))
                            .rowNo(i)
                            .allPrice(Float.parseFloat(price) * Float.parseFloat(quantity))
                            .price(Float.parseFloat(price))
                            .build();
                    if (Long.parseLong(quantity) != 0L) {
                        dtoList.add(build);
                    }
                }

            } else {
                valMinus++;
            }
        }
        recordCount = recordCount - valMinus + 1;

        Float sum = 0f;
        for (MoveDTO moveDTO : dtoList) {
            sum += moveDTO.getAllPrice();
        }

        MoveDTO moveDTO = new MoveDTO();
        moveDTO.setRowNo(recordCount - 1);
        dtoList.add(moveDTO);


        model.addAttribute("purchaseAll", dtoList);
        model.addAttribute("recordCount", recordCount);
        model.addAttribute("totalAll", sum);


        return "purchase";
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


    @RequestMapping(value = "/savePurchaseSell", method = RequestMethod.POST, params = "action=save")
    public String savePurchaseSell(HttpServletRequest request, Principal principal) throws ParseException {
        log.info("Save Move In ");
        Optional<Users> optionalUser = userRepository.findOneByLogin(principal.getName());

        String moveDate = request.getParameter("moveDate");

        LocalDate localDate = fromStrToLocalDate(moveDate);


        long provider = Long.parseLong(request.getParameter("provider"));
        String billNo = request.getParameter("billNo");

        Movement movement = Movement.builder()
                .provider(provider)
                .billNo(billNo)
                .moveType(1)
                .moveDate(localDate)
                .status(1)
                .userNo(optionalUser.get().getId())
                .build();


        Movement save = movementRepository.save(movement);

        int count = Integer.parseInt(request.getParameter("recordCount"));

        for (int i = 0; i < count - 1; i++) {
            String barcode = request.getParameter("bar" + i);
            String quantity = request.getParameter("qua" + i);
            String price = request.getParameter("pri" + i);
            String materialName = request.getParameter("name" + i);
            String code = request.getParameter("code" + i);
            if (!Strings.isEmpty(code) && !Strings.isEmpty(barcode) && !Strings.isEmpty(quantity) && !Strings.isEmpty(price) & !Strings.isEmpty(materialName)) {
                MoveDetails moveDetails = MoveDetails.builder()
                        .barcode(barcode)
                        .quantity(Long.parseLong(quantity))
                        .materialId(Long.parseLong(code))
                        .price(Float.parseFloat(price))
                        .moveId(save.getId())
                        .build();
                if (Long.parseLong(quantity) != 0) {
                    moveDetailsRepository.save(moveDetails);
                    log.info("barcode value {} ,  {} , {}", barcode, quantity, price);
                    Optional<Material> material = materialRepository.findById(Long.parseLong(code));
                    if (material.isPresent()) {
                        Long quantity1 = material.get().getQuantity();
                        material.get().setQuantity(quantity1 + Long.parseLong(quantity));
                        materialRepository.save(material.get());
                    }
                }

            }


        }

        return "redirect:purchaseSell";

    }

    @RequestMapping(value = "/savePurchase", method = RequestMethod.POST, params = "action=save")
    public String savePurchase(HttpServletRequest request, Principal principal) throws ParseException {
        log.info("Save Move In ");
        Optional<Users> optionalUser = userRepository.findOneByLogin(principal.getName());

        String moveDate = request.getParameter("moveDate");

        LocalDate localDate = fromStrToLocalDate(moveDate);


        long provider = 0;

        String billNo = "0";
        Movement movement = Movement.builder()
                .provider(provider)
                .billNo(billNo)
                .moveType(2)
                .moveDate(localDate)
                .status(1)
                .userNo(optionalUser.get().getId())
                .build();


        Movement save = movementRepository.save(movement);

        int count = Integer.parseInt(request.getParameter("recordCount"));

        for (int i = 0; i < count - 1; i++) {
            String barcode = request.getParameter("bar" + i);
            String quantity = request.getParameter("qua" + i);
            String price = request.getParameter("pri" + i);
            String materialName = request.getParameter("name" + i);
            String code = request.getParameter("code" + i);
            if (!Strings.isEmpty(code) && !Strings.isEmpty(barcode) && !Strings.isEmpty(quantity) && !Strings.isEmpty(price) & !Strings.isEmpty(materialName)) {
                MoveDetails moveDetails = MoveDetails.builder()
                        .barcode(barcode)
                        .quantity(Long.parseLong(quantity))
                        .materialId(Long.parseLong(code))
                        .price(Float.parseFloat(price))
                        .moveId(save.getId())
                        .build();
                if (Long.parseLong(quantity) != 0) {
                    moveDetailsRepository.save(moveDetails);
                    log.info("barcode value {} ,  {} , {}", barcode, quantity, price);
                    Optional<Material> material = materialRepository.findById(Long.parseLong(code));
                    if (material.isPresent()) {
                        Long quantity1 = material.get().getQuantity();
                        material.get().setQuantity(quantity1 - Long.parseLong(quantity));
                        materialRepository.save(material.get());
                    }
                }

            }


        }

        return "redirect:purchase";
    }


    @RequestMapping(value = "/savePurchaseSell", method = RequestMethod.POST, params = "action=addNew")
    public String savePurchaseSell(Model model, HttpServletRequest request, Principal principal) throws ParseException {

        log.info("Add New Item in PurchaseSell");
        String countR = request.getParameter("recordCount");
        Integer recordCount = Integer.parseInt(countR);


        String billNo = request.getParameter("billNo");
        model.addAttribute("billNo", billNo);

        long provider = Long.parseLong(request.getParameter("provider"));

        model.addAttribute("provider1", provider);

        String moveDate = request.getParameter("moveDate");
        if (!StringUtils.isEmpty(moveDate)) {
            LocalDate localDate = fromStrToLocalDate(moveDate);
            model.addAttribute("md", localDate);
        }


        List<MoveDTO> dtoList = new ArrayList<>();
        int valMinus = 0;
        for (int i = 0; i < recordCount; i++) {
            String barcode = request.getParameter("bar" + i);
            String materialName = request.getParameter("name" + i);
            String quantity = request.getParameter("qua" + i);
            String bln = request.getParameter("bln" + i);

            String price = request.getParameter("pri" + i);
            String code = request.getParameter("code" + i);
            if ((!StringUtils.isEmpty(barcode)) && (!StringUtils.isEmpty(materialName)) && (!StringUtils.isEmpty(quantity)) && (!StringUtils.isEmpty(price)) && (!StringUtils.isEmpty(code))) {
                Optional<MoveDTO> moveDTOOptional = dtoList.stream().filter(e -> e.getBarcode().equals(barcode)).findAny();
                if (moveDTOOptional.isPresent()) {
                    Long quantity1 = moveDTOOptional.get().getQuantity();
                    long l = quantity1 + Long.parseLong(quantity);
                    Float allPrice = l * Float.parseFloat(price);
                    moveDTOOptional.get().setQuantity(l);
                    moveDTOOptional.get().setAllPrice(allPrice);
                    if (l == 0) {
                        dtoList.remove(moveDTOOptional.get());
                    }
                    valMinus++;
                } else {

                    MoveDTO build = MoveDTO.builder()
                            .barcode(barcode)
                            .materialName(materialName)
                            .materialId(Long.parseLong(code))
                            .quantityBalance(Long.parseLong(bln))
                            .quantity(Long.parseLong(quantity))
                            .rowNo(i)
                            .allPrice(Float.parseFloat(price) * Float.parseFloat(quantity))
                            .price(Float.parseFloat(price))
                            .build();
                    if (Long.parseLong(quantity) != 0L) {
                        dtoList.add(build);
                    }
                }

            } else {
                valMinus++;
            }
        }
        recordCount = recordCount - valMinus + 1;

        Float sum = 0f;
        for (MoveDTO moveDTO : dtoList) {
            sum += moveDTO.getAllPrice();
        }

        MoveDTO moveDTO = new MoveDTO();
        moveDTO.setRowNo(recordCount - 1);
        dtoList.add(moveDTO);


        model.addAttribute("purchaseAll", dtoList);
        model.addAttribute("recordCount", recordCount);
        model.addAttribute("totalAll", sum);


        return "purchaseSell";
    }


}


