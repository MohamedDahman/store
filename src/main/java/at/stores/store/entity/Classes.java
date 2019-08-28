package at.stores.store.entity;


import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "CLASSES")
public class Classes {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "description", length = 35, nullable = false)
    @Size(min = 1, max = 35)
    private String describe;

    @Column(name = "reorderpoint")
    private Long reOrderPoint;


    public Classes(Long id, @Size(min = 1, max = 35) String describe, Long reOrderPoint) {
        this.id = id;
        this.describe = describe;
        this.reOrderPoint = reOrderPoint;
    }

    public Classes(@Size(min = 1, max = 35) String describe) {
        this.describe = describe;
    }

    public Classes() {
    }

    public Classes(@Size(min = 1, max = 35) String describe, Long reOrderPoint) {
        this.describe = describe;
        this.reOrderPoint = reOrderPoint;
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

    public Long getReOrderPoint() {
        return reOrderPoint;
    }

    public void setReOrderPoint(Long reOrderPoint) {
        this.reOrderPoint = reOrderPoint;
    }
}
