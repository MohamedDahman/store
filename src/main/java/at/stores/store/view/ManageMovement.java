package at.stores.store.view;

import at.stores.store.dao.*;
import at.stores.store.dto.MoveDTO;
import at.stores.store.dto.MovementDto;
import at.stores.store.entity.*;
import at.stores.store.service.MoveService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ManageMovement {

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
    MoveService moveService;

    @Autowired
    UserRepository userRepository;

    private List<MoveDTO> moveDTOList = new ArrayList<>();

    private List<MovementDto> movementDtoList = new ArrayList<>();


    @GetMapping("/managePurchase")
    String pageDailyBuyDate(Model model, String moveDate) {

        if (!StringUtils.isEmpty(moveDate)) {
            model.addAttribute("purchaseDate", moveDate);

            LocalDate localDate = fromStrToLocalDate(moveDate);

            List<Movement> collect = movementRepository.findAll().stream()
                    .filter(e -> e.getMoveType().equals(1))
                    .filter(e -> e.getMoveDate().equals(localDate))
                    .collect(Collectors.toList());


            movementDtoList.clear();

            for (Movement movement : collect) {
                List<MoveDetails> moveDetailsList = moveDetailsRepository.findAll().stream().filter(e -> e.getMoveId().equals(movement.getId())).collect(Collectors.toList());
                Float total = 0.0F;
                for (MoveDetails moveDetails : moveDetailsList) {
                    total = total + moveDetails.getPrice() * moveDetails.getQuantity();
                }

                MovementDto movementDto = new MovementDto();
                movementDto.setBillNo(movement.getBillNo());
                movementDto.setId(movement.getId());
                movementDto.setTotal(total);
                Optional<Provider> provider = providerRepository.findById(movement.getProvider());
                movementDto.setProviderName(provider.get().getName());
                movementDtoList.add(movementDto);

            }

            model.addAttribute("purchaseListManage", movementDtoList);


        }


        return "managePurchase";

    }

    @ModelAttribute("providerAll")
    public List<Provider> getProviders() {
        return providerRepository.findAll().stream().collect(Collectors.toList());
    }

    @PostMapping("/delPurchase")
    String pageDelPurchase(Model model, String idValDelete) {
        log.info(idValDelete);
        if (moveService.checkCanDel(Long.parseLong(idValDelete)) == true) {
            List<MoveDetails> moveDetailsList = moveDetailsRepository.findAll()
                    .stream()
                    .filter(e -> e.getMoveId().equals(Long.parseLong(idValDelete)))
                    .collect(Collectors.toList());
            for (MoveDetails moveDetails : moveDetailsList) {
                moveDetailsRepository.delete(moveDetails);
            }
            movementRepository.deleteById(Long.parseLong(idValDelete));
            log.info("start delete Purchase");
            return "/index";
        } else {
            log.error("doesn't possible to delete this Movement");
            return "error";
        }
    }

    @GetMapping("/editPurchase")
    String pageEditPurchase(Model model, String moveId) {
        Optional<Movement> movement = movementRepository.findById(Long.parseLong(moveId));
        model.addAttribute("movementData", movement.get());
        List<MoveDetails> moveDetailsList = getMoveDetailsList(moveId);
        model.addAttribute("recordCount", moveDetailsList.size() + 1);
        moveDTOList.clear();
        Float v = 0f;
        for (int i = 0; i < moveDetailsList.size() + 1; i++) {
            MoveDTO moveDTO = new MoveDTO();
            moveDTO.setRowNo(i);
            if (moveDetailsList.size() > i) {
                MoveDetails moveDetails = moveDetailsList.get(i);
                moveDTO.setBarcode(moveDetails.getBarcode());
                moveDTO.setQuantity(moveDetails.getQuantity());
                moveDTO.setPrice(moveDetails.getPrice());
                moveDTO.setTotal(moveDetails.getPrice() * moveDetails.getQuantity());
                moveDTO.setAllPrice(moveDetails.getPrice() * moveDetails.getQuantity());
                v += moveDetails.getPrice() * moveDetails.getQuantity();
                List<Material> materialList = materialRepository.findAll()
                        .stream()
                        .filter(e -> e.getBarcode().equals(moveDetails.getBarcode()))
                        .collect(Collectors.toList());

                moveDTO.setMaterialName(materialList.get(0).getDescribe());
                moveDTO.setMaterialId(materialList.get(0).getId());

            }
            moveDTOList.add(moveDTO);
        }

        model.addAttribute("moveDtoAll", moveDTOList);
        model.addAttribute("totalAll", v);


        return "editPurchase";
    }

    private List<MoveDetails> getMoveDetailsList(String moveId) {
        return moveDetailsRepository.findAll()
                    .stream()
                    .filter(e -> e.getMoveId().equals(Long.parseLong(moveId)))
                    .collect(Collectors.toList());
    }


    @RequestMapping(value = "/editMoveIn", method = RequestMethod.POST, params = "action=edit")
    public String saveMoveIn(HttpServletRequest request, Principal principal) {
        log.info("Edit Move In ");

        String countR = request.getParameter("recordCount");
        Integer recordCount = Integer.parseInt(countR);

        Optional<Users> optionalUser = userRepository.findOneByLogin(principal.getName());

        String moveDate = request.getParameter("moveDate");
        LocalDate localDate = fromStrToLocalDate(moveDate);
        long provider = Long.parseLong(request.getParameter("provider"));
        long moveId = Long.parseLong(request.getParameter("moveId"));

        String billNo = request.getParameter("billNo");
        Movement movement = Movement.builder()
                .id(moveId)
                .provider(provider)
                .billNo(billNo)
                .moveType(1)
                .moveDate(localDate)
                .status(1)
                .userNo(optionalUser.get().getId())
                .build();
        Movement save = movementRepository.save(movement);

        List<MoveDetails> moveDetailsList = moveDetailsRepository.findAll().stream().filter(e -> e.getMoveId().equals(moveId)).collect(Collectors.toList());
        for (MoveDetails moveDetails : moveDetailsList) {
            moveDetailsRepository.delete(moveDetails);
        }

        for (int i = 0; i < recordCount; i++) {
            String barcode = request.getParameter("bar" + i);
            String quantity = request.getParameter("qua" + i);
            String price = request.getParameter("pri" + i);
            String materialName = request.getParameter("name" + i);
            String code = request.getParameter("code" + i);
            if (!Strings.isEmpty(code) && !Strings.isEmpty(barcode) && !Strings.isEmpty(quantity) && !Strings.isEmpty(price) & !Strings.isEmpty(materialName)) {
                MoveDetails moveDetails = MoveDetails.builder()
                        .barcode(barcode)
                        .quantity(Long.parseLong(quantity))
                        .price(Float.parseFloat(price))
                        .materialId(Long.parseLong(code))
                        .moveId(save.getId())
                        .build();

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


        return "index";
    }


    @GetMapping("/manageSelling")
    public String pageDailySellDate(Model model, String moveDate) {

        if (!StringUtils.isEmpty(moveDate)) {
            model.addAttribute("sellDate", moveDate);
            LocalDate localDate = fromStrToLocalDate(moveDate);
            List<Movement> collect = movementRepository.findAll().stream()
                    .filter(e -> e.getMoveType().equals(2))
                    .filter(e -> e.getMoveDate().equals(localDate))
                    .collect(Collectors.toList());

            movementDtoList.clear();

            for (Movement movement : collect) {
                List<MoveDetails> moveDetailsList = moveDetailsRepository.findAll().stream().filter(e -> e.getMoveId().equals(movement.getId())).collect(Collectors.toList());
                Float total = 0.0F;
                for (MoveDetails moveDetails : moveDetailsList) {
                    total = total + moveDetails.getPrice() * moveDetails.getQuantity();
                }

                MovementDto movementDto = new MovementDto();
                movementDto.setBillNo(movement.getBillNo());
                movementDto.setId(movement.getId());
                movementDto.setTotal(total);
                movementDtoList.add(movementDto);

            }

            model.addAttribute("sellListManage", movementDtoList);

        }
        return "manageSelling";
    }


    @GetMapping("/editSell")
    public String pageEditSell(Model model, String moveId) {
        Optional<Movement> movement = movementRepository.findById(Long.parseLong(moveId));
        model.addAttribute("movementData", movement.get());

        List<MoveDetails> moveDetailsList = moveDetailsRepository.findAll()
                .stream()
                .filter(e -> e.getMoveId().equals(Long.parseLong(moveId)))
                .collect(Collectors.toList());
        model.addAttribute("recordCount", moveDetailsList.size() + 1);


        moveDTOList.clear();
        float v = 0;
        for (int i = 0; i < moveDetailsList.size() + 1; i++) {
            MoveDTO moveDTO = new MoveDTO();
            moveDTO.setRowNo(i);
            if (moveDetailsList.size() > i) {
                MoveDetails moveDetails = moveDetailsList.get(i);
                moveDTO.setBarcode(moveDetails.getBarcode());
                moveDTO.setQuantity(moveDetails.getQuantity());
                moveDTO.setPrice(moveDetails.getPrice());
                moveDTO.setTotal(moveDetails.getPrice() * moveDetails.getQuantity());

                v += moveDetails.getPrice() * moveDetails.getQuantity();
                moveDTO.setAllPrice(moveDetails.getPrice() * moveDetails.getQuantity());
                List<Material> materialList = materialRepository.findAll()
                        .stream()
                        .filter(e -> e.getBarcode().equals(moveDetails.getBarcode()))
                        .collect(Collectors.toList());

                moveDTO.setMaterialName(materialList.get(0).getDescribe());
                moveDTO.setMaterialId(materialList.get(0).getId());

            }
            moveDTOList.add(moveDTO);
        }


        model.addAttribute("totalAll", v);
        model.addAttribute("moveDtoAll", moveDTOList);

        return "editSell";
    }


    @RequestMapping(value = "/editMoveOut", method = RequestMethod.POST, params = "action=addNew")
    public String savePurchase(Model model, HttpServletRequest request, Principal principal) throws ParseException {

        log.info("here start new Record for edit ");
        String countR = request.getParameter("recordCount");
        Integer recordCount = Integer.parseInt(countR);

        long moveId = Long.parseLong(request.getParameter("moveId"));

        Optional<Movement> movement = movementRepository.findById(moveId);
        model.addAttribute("movementData", movement.get());


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


        model.addAttribute("moveDtoAll", dtoList);
        model.addAttribute("recordCount", recordCount);
        model.addAttribute("totalAll", sum);


        return "editSell";
    }


    @RequestMapping(value = "/editMoveIn", method = RequestMethod.POST, params = "action=addNew")
    public String addnewPa(Model model, HttpServletRequest request, Principal principal) throws ParseException {

        log.info("here start new Record for edit ");
        String countR = request.getParameter("recordCount");
        Integer recordCount = Integer.parseInt(countR);

        long moveId = Long.parseLong(request.getParameter("moveId"));

        Optional<Movement> movement = movementRepository.findById(moveId);
        model.addAttribute("movementData", movement.get());


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


        model.addAttribute("moveDtoAll", dtoList);
        model.addAttribute("recordCount", recordCount);
        model.addAttribute("totalAll", sum);


        return "editPurchase";
    }






    @RequestMapping(value = "/editMoveOut", method = RequestMethod.POST, params = "action=edit")
    public String saveMoveOut(HttpServletRequest request, Principal principal) {
        log.info("Edit Move Out");
        Optional<Users> optionalUser = userRepository.findOneByLogin(principal.getName());

        String countR = request.getParameter("recordCount");
        Integer recordCount = Integer.parseInt(countR);

        String moveDate = request.getParameter("moveDate");
        LocalDate localDate = fromStrToLocalDate(moveDate);
        long moveId = Long.parseLong(request.getParameter("moveId"));

        String billNo = "0";
        Movement movement = Movement.builder()
                .id(moveId)
                .provider(0L)
                .billNo(billNo)
                .moveType(2)
                .moveDate(localDate)
                .status(1)
                .userNo(optionalUser.get().getId())
                .build();
        Movement save = movementRepository.save(movement);

        List<MoveDetails> moveDetailsList = moveDetailsRepository.findAll().stream().filter(e -> e.getMoveId().equals(moveId)).collect(Collectors.toList());
        for (MoveDetails moveDetails : moveDetailsList) {
            moveDetailsRepository.delete(moveDetails);
        }

        for (int i = 0; i < recordCount; i++) {
            String barcode = request.getParameter("bar" + i);
            String quantity = request.getParameter("qua" + i);
            String price = request.getParameter("pri" + i);
            String materialName = request.getParameter("name" + i);
            String code = request.getParameter("code" + i);
            if (!Strings.isEmpty(code) && !Strings.isEmpty(barcode) && !Strings.isEmpty(quantity) && !Strings.isEmpty(price) & !Strings.isEmpty(materialName)) {
                MoveDetails moveDetails = MoveDetails.builder()
                        .barcode(barcode)
                        .quantity(Long.parseLong(quantity))
                        .price(Float.parseFloat(price))
                        .materialId(Long.parseLong(code))
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


        return "index";
    }


    @PostMapping("/delSell")
    String pageDelSell(Model model, String idValDelete) {
        log.info(idValDelete);
        if (moveService.checkCanDel(Long.parseLong(idValDelete)) == true) {
            List<MoveDetails> moveDetailsList = moveDetailsRepository.findAll()
                    .stream()
                    .filter(e -> e.getMoveId().equals(Long.parseLong(idValDelete)))
                    .collect(Collectors.toList());
            for (MoveDetails moveDetails : moveDetailsList) {
                moveDetailsRepository.delete(moveDetails);
            }
            movementRepository.deleteById(Long.parseLong(idValDelete));
            log.info("start delete Sell");
            return "/index";
        } else {
            log.error("doesn't possible to delete this Movement");
            return "error";
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
}
