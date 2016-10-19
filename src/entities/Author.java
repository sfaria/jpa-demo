package entities;

import javax.persistence.*;
import java.util.List;

/**
 * @author Scott Faria
 */
@Entity
@Table(name="AUTHOR")
public class Author {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="AUTHOR_ID", unique=true, nullable=false)
    private Long id;

    @Column(name="NAME", unique=true, nullable=false)
    private String name;

    @OneToMany
    @JoinColumn(name="AUTHOR_ID", referencedColumnName="AUTHOR_ID")
    private List<Book> books;


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

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    // -------------------- Overridden Methods --------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return id.equals(author.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Author {");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", books=").append(books);
        sb.append(" }");
        return sb.toString();
    }
}
