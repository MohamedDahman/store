        /**************************************************
        *               Store System
        *
        *
        * code written by: Mohamed Dahman
        *
        *
        *
        ***************************************************/
package at.stores.store.entity;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name="CATEGORY")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="description", length = 35, nullable = false)
    @Size(min=1 , max=35)
    private String describe;

    public Category(String describe) {
        this.describe = describe;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public Category() {
    }

    public Category(Long id, String describe) {
        this.id = id;
        this.describe = describe;
    }
}
