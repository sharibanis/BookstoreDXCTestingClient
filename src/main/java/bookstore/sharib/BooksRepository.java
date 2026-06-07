package bookstore.sharib;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BooksRepository extends JpaRepository<Books, Long> {

    @Query("SELECT b FROM Books b WHERE b.title = ?1 OR b.authors = ?2")
    Books findByTitleOrAuthor(String title, List<String> author);

}
