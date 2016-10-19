package db;

import entities.Author;
import entities.Book;
import org.h2.tools.Server;

import javax.persistence.EntityManager;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Scott Faria
 */
public class H2Server {

    // -------------------- Private Statics --------------------

    private static final String CONNECT_STRING = "jdbc:h2:tcp://localhost/db";

    // -------------------- Private Static Methods --------------------

    private static void createDatabaseObjects() throws SQLException {
        try (Connection conn = DriverManager.getConnection(CONNECT_STRING, "test", ""); Statement stmt = conn.createStatement()) {
            //language=H2
            stmt.execute("" +
                "CREATE TABLE AUTHOR (\n" +
                "  AUTHOR_ID INTEGER NOT NULL PRIMARY KEY,\n" +
                "  NAME VARCHAR(100) NOT NULL\n" +
                ")"
            );

            //language=H2
            stmt.execute("CREATE TABLE BOOK (\n" +
                    "  BOOK_ID INTEGER NOT NULL PRIMARY KEY,\n" +
                    "  AUTHOR_ID INTEGER NOT NULL,\n" +
                    "  NAME VARCHAR2(400) NOT NULL,\n" +
                    "  PUBLISHED DATE NOT NULL,\n" +
                    "  FOREIGN KEY (AUTHOR_ID) REFERENCES AUTHOR (AUTHOR_ID)\n" +
                    ")"
            );
        }
    }

    private static void insertTestData() throws SQLException {
        JPA.execute(em -> {
            DateFormat df = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);

            em.getTransaction().begin();
            createEntry(em, "Herman Melville", new Object[] { "Moby Dick", df.parse("October 18, 1851") });
            createEntry(em, "James Joyce",
                new Object[] { "Ulysses", df.parse("February 2, 1922") },
                new Object[] { "Dubliners", df.parse("June 1, 1914") },
                new Object[] { "Finnegans Wake", df.parse("May 4, 1939") }
            );
            createEntry(em, "J. R. R. Tolkien",
                new Object[] { "The Hobbit", df.parse("September 21, 1937") },
                new Object[] { "The Fellowship of the Ring", df.parse("July 29, 1954") },
                new Object[] { "The Two Towers", df.parse("November 11, 1954") },
                new Object[] { "The Return of the King", df.parse("October 20, 1955") }
            );
            em.getTransaction().commit();
            return null;
        });
    }

    private static void createEntry(EntityManager em, String authorName, Object[] ... values) {
        Author author = new Author();
        author.setName(authorName);
        em.persist(author);

        List<Book> books = Stream.of(values)
                .map(oo -> {
                    Book book = new Book();
                    book.setName((String) oo[0]);
                    book.setPublished((Date) oo[1]);
                    em.persist(book);
                    return book;
                })
                .collect(Collectors.toList());

        author.setBooks(books);
        em.merge(author);
    }

    private static void listBooks(String query) {
        JPA.execute(em -> {
            em.createQuery(query, Author.class)
                .getResultList()
                .forEach(author -> System.err.println(author.toString()));
            return null;
        });
    }

    // -------------------- Main --------------------

    public static void main(String[] args) throws Exception {
        try {
            Class.forName("org.h2.Driver");
            try {
                Server.shutdownTcpServer("tcp://localhost", "", true, true);
            } catch (Throwable ignored) {} finally {
                File dbFile = new File("db.h2.db");
                if (dbFile.exists()) {
                    dbFile.delete();
                }
            }

            Server.createTcpServer("-tcpAllowOthers").start();
            createDatabaseObjects();
            insertTestData();

            //language=JPQL
            System.err.println("Naive query...");
            listBooks("select a from Author a");

            //language=JPQL
            System.err.println("Query with join fetch...");
            listBooks("select a from Author a inner join fetch a.books");

            //language=JPQL
            System.err.println("Query with join fetch and distinct...");
            listBooks("select distinct a from Author a inner join fetch a.books");
        } catch (Throwable th) {
            th.printStackTrace(System.err);
            System.exit(1);
        }
    }

}
