package at.stores.store.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import javax.persistence.*;
import javax.validation.constraints.Size;


@Data
@Table(name = "UNITS")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Units {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "description", length = 35, nullable = false)
    @Size(min = 1, max = 35)
    private String describe;

    public Units(@Size(min = 1, max = 35) String describe) {
        this.describe = describe;
    }
}
