package at.stores.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialBl {

    private Long no;

    private Long id;

    private String describe;

    private Long bl;

    private Long in;
    private Long out;
    private Long minimum;
    private LocalDate moveDate;
    private String billNo;
    private Float price;
    private Float total;
}
