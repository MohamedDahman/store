package at.stores.store.entity;


import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Table(name = "provider")
@AllArgsConstructor
@NoArgsConstructor
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    public Provider(String name) {
        this.name = name;
    }


}