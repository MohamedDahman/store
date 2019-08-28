package at.stores.store.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Data
@Entity
@Table(name="PERMISSION")
@AllArgsConstructor
@NoArgsConstructor
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="description", length = 35, nullable = false)
    @Size(min=1 , max=35)
    private String describe;

    public Permission(@Size(min = 1, max = 35) String describe) {
        this.describe = describe;
    }
}
