package at.stores.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MoveDTO {


    private Long id;
    private String barcode;
    private String materialName;
    private Long quantity;
    private Long quantityBalance;
    private Float price;
    private Float allPrice;
    private Integer rowNo;
    private Float total;
    private Long materialId;
    private LocalDate moveDate;

    public MoveDTO(Integer rowNo) {
        this.rowNo = rowNo;
    }
}
