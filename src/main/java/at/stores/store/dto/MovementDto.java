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
public class MovementDto {

    private String providerName;
    private String billNo;
    private String barcode;
    private String materialName;
    private Long quantity;
    private Float price;
    private Float total;
    private Long id;
    private LocalDate moveDate;
}
