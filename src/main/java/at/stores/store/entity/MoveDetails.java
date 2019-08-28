package at.stores.store.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "move_details")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MoveDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "move_id")
    private Long moveId;

    @Column(name = "material_id")
    private Long materialId;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "price", precision = 2)
    private Float price;

    private String barcode;


}
