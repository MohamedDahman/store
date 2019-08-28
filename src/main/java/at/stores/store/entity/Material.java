package at.stores.store.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "material")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "classid", nullable = false)
    private Long classId;

    @Column(name = "categoryid", nullable = false)
    private Long categoryId;

    @Column(name = "description", length = 50, nullable = false)
    @Size(min = 1, max = 50)
    private String describe;

    @Column(name = "barcode", nullable = false)
    private String barcode;

    @Column(name = "code", nullable = false)
    private Long code;

    @Column(name = "minimum", nullable = false)
    private Long minimum;

    @Column(name = "buy_price", nullable = false, precision = 2)
    private Float buyPrice;

    @Column(name = "sell_price", nullable = false, precision = 2)
    private Float sellPrice;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Column(name = "unit", nullable = false)
    private Long unit;

    @Column(name = "notes", nullable = true)
    private String notes;



    @Column(name = "profit_margin", nullable = false, precision = 2)
    private Float ProfitMargin;

    public Material(Long id, Long classId, Long categoryId, @Size(min = 1, max = 50) String describe, String barcode, Long code) {
        this.id = id;
        this.classId = classId;
        this.categoryId = categoryId;
        this.describe = describe;
        this.barcode = barcode;
        this.code = code;
    }


    public Material(@Size(min = 1, max = 50) String describe) {
        this.describe = describe;
    }

}
