package at.stores.store.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "MOVEMENT")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Movement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "move_date", nullable = false)
    private LocalDate moveDate;

    @Column(name = "user_no", nullable = false)
    private Long userNo;

    @Column(name = "move_type", nullable = false)
    private Integer moveType;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "bill_no", nullable = false)
    private String billNo;

    @Column(name = "provider", nullable = false)
    private Long provider;


}
