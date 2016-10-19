package entities;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Scott Faria
 */
@Entity
@Table(name="BOOK")
public class Book {

    // -------------------- Private Variables --------------------

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="BOOK_ID", unique=true, nullable=false)
    private Long id;

    @Column(name="NAME", nullable=false)
    private String name;

    @Temporal(value=TemporalType.DATE)
    @Column(name="PUBLISHED", nullable=false)
    private Date published;

    // -------------------- Public Methods --------------------

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPublished(Date published) {
        this.published = published;
    }

    // -------------------- Overridden Methods --------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id.equals(book.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Book {");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", published=").append(published);
        sb.append(" }");
        return sb.toString();
    }
}
