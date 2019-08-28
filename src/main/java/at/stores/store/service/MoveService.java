package at.stores.store.service;

import at.stores.store.dao.ClassesRepository;
import at.stores.store.dao.MaterialRepository;
import at.stores.store.dao.MoveDetailsRepository;
import at.stores.store.dao.MovementRepository;
import at.stores.store.dto.MaterialBl;
import at.stores.store.entity.Material;
import at.stores.store.entity.MoveDetails;
import at.stores.store.entity.Movement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MoveService {

    @Autowired
    MovementRepository movementRepository;

    @Autowired
    MoveDetailsRepository moveDetailsRepository;

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    ClassesRepository classesRepository;


    public Boolean checkCanDel(Long moveId) {
        List<MoveDetails> moveDetailsList = moveDetailsRepository.findAll()
                .stream()
                .filter(e -> e.getMoveId().equals(moveId))
                .collect(Collectors.toList());
        boolean canDel = false;
        for (MoveDetails moveDetails : moveDetailsList) {
            Material material = materialRepository.findById(moveDetails.getMaterialId()).get();
            if (material.getQuantity() < moveDetails.getQuantity()) {
                canDel = true;
            }
        }
        canDel = true;
        return canDel;
    }


    public void increaseQuntity(Long materialId, Long quantity) {
        Material material = materialRepository.findById(materialId).get();
        material.setQuantity(material.getQuantity() + quantity);
        materialRepository.save(material);
    }


    public void decreaseQuntity(Long materialId, Long quantity) {
        Material material = materialRepository.findById(materialId).get();
        material.setQuantity(material.getQuantity() - quantity);
        materialRepository.save(material);
    }


    public List<MaterialBl> getMinimumBalanceAsService() {

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
                            .minimum(e.getMinimum())
                            .build();
                    materialBlList.add(ma);

                });

        List<Movement> movementList = movementRepository.findAll()
                .stream().collect(Collectors.toList());
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
        List<MaterialBl> collect = mList.stream().filter(e -> e.getMinimum() > e.getBl()).collect(Collectors.toList());
        return collect;

    }


    public List<MaterialBl> getMinimumClassesAsService() {
        List<MaterialBl> materialBlList = new ArrayList<>();
        List<MaterialBl> mList = new ArrayList<>();

        Long no = 1L;
        classesRepository.findAll()
                .stream()
                .forEach(e -> {
                    long count = materialRepository.findAll().stream().filter(mt -> mt.getClassId().equals(e.getId())).count();
                    if (count != 0) {
                        MaterialBl ma = MaterialBl
                                .builder()
                                .no(1L)
                                .id(e.getId())
                                .describe(e.getDescribe())
                                .bl(0L)
                                .in(0L)
                                .out(0L)
                                .minimum(e.getReOrderPoint())
                                .build();
                        materialBlList.add(ma);
                    }
                });

        List<Movement> movementList = movementRepository.findAll()
                .stream().collect(Collectors.toList());

        for (MaterialBl materialBl : materialBlList) {

            List<Material> collect = materialRepository.findAll().stream().filter(e -> e.getClassId().equals(materialBl.getId())).collect(Collectors.toList());
            Long in = 0L;
            Long out = 0L;
            for (Material material : collect) {
                for (Movement movement : movementList) {
                    Optional<MoveDetails> moveDetails = moveDetailsRepository.findAll()
                            .stream()
                            .filter(mv -> mv.getMoveId().equals(movement.getId()))
                            .filter(mt -> mt.getMaterialId().equals(material.getId()))
                            .findFirst();
                    if (moveDetails.isPresent()) {
                        if (movement.getMoveType().equals(1)) {
                            in = in + moveDetails.get().getQuantity();
                        } else {
                            out = out + moveDetails.get().getQuantity();
                        }
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
        List<MaterialBl> collect = mList.stream().filter(e -> e.getMinimum() > e.getBl()).collect(Collectors.toList());
        return collect;
    }

    }
