package bookstore.sharib;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag(name = "Bookstore API", description = "Bookstore API for managing books and authors")
@RestController("/bookstore")
public class RESTController {
	private final BooksRepository booksRepository;
	//@Autowired
	//private RESTService restService;
	private static final Logger log = LoggerFactory.getLogger(RESTController.class);
	
	public RESTController(AuthorsRepository authRepository, BooksRepository booksRepository)  {
		this.booksRepository = booksRepository;
	}
	
	//Add a new book
	@Tag(name = "Add a new book", description = "authors must be a JSON array of author names, e.g. [\"Author1\", \"Author2\"]")
	@PostMapping("/addBook")
	@PreAuthorize("hasAuthority('ADMIN')")
	Books addBook(@RequestBody Books newBook) {
		log.info("Adding new book: {}", newBook.toString());
		long isbn = newBook.getIsbn();
		if ((isbn <= 0) || (String.valueOf(isbn).length() != 13)) {
			log.warn("ISBN field is invalid for book: {}", newBook.toString());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ISBN field must be a 13-digit number");
		}
		if (booksRepository.existsById(newBook.getIsbn())) {
			log.warn("Book with this ISBN already exists! {}", newBook.toString());
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Book with this ISBN already exists!");
		}
		String title = newBook.getTitle();
		if (title == null || title.isEmpty()) {
			log.warn("Title field is empty for book: {}", newBook.toString());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title field cannot be empty");
		}
		List<String> authors = newBook.getAuthors();
		if (authors == null || authors.isEmpty()) {
			log.warn("Authors field is empty for book: {}", newBook.toString());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Authors field cannot be empty");
		}
		log.info("authors.toString(): {}", authors.toString());
		int yearPublished = newBook.getYearPublished();
		if (yearPublished < 0 || yearPublished > 9999) {
			log.warn("Year Published field is invalid for book: {}", newBook.toString());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Year Published field must be a valid year");
		}
		double price = newBook.getPrice();
		if (price < 0) {
			log.warn("Price field is invalid for book: {}", newBook.toString());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price field must be a non-negative number");
		}
		log.info("Added new book: {}", newBook.toString());
		return booksRepository.save(newBook);
	}

	//Update an existing book
	@Tag(name = "Update an existing book if it exists", description = "authors must be a JSON array of author names, e.g. [\"Author1\", \"Author2\"]")
	@PutMapping("/{ISBN}")
	@PreAuthorize("hasAuthority('ADMIN')")
	Books updateBook(@RequestBody Books newBook, @PathVariable Long ISBN) {
		log.info("Updating book: {}", newBook.toString());
		if ((ISBN <= 0) || (String.valueOf(ISBN).length() != 13)) {
			log.warn("ISBN field is invalid for book: {}", newBook.toString());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ISBN field must be a 13-digit number");
		}
		if (!booksRepository.existsById(ISBN)) {
			log.warn("Book with this ISBN does not exist! {}", newBook.toString());
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book with ISBN "+ ISBN + " not found.");
		}
		long isbn = newBook.getIsbn();
		if ((isbn <= 0) || (String.valueOf(isbn).length() != 13 || (isbn != ISBN))) {
			log.warn("isbn field is invalid for book: {}", newBook.toString());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ISBN field in the body must be a 13-digit number. And match the ISBN in the path variable.");
		}
		String title = newBook.getTitle();
		if (title == null || title.isEmpty()) {
			log.warn("Title field is empty for book: {}", newBook.toString());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title field cannot be empty");
		}
		List<String> authors = newBook.getAuthors();
		if (authors == null || authors.isEmpty()) {
			log.warn("Authors field is empty for book: {}", newBook.toString());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Authors field cannot be empty");
		}
		log.info("authors.toString(): {}", authors.toString());
		int yearPublished = newBook.getYearPublished();
		if (yearPublished < 0 || yearPublished > 9999) {
			log.warn("Year Published field is invalid for book: {}", newBook.toString());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Year Published field must be a valid year");
		}
		double price = newBook.getPrice();
		if (price < 0) {
			log.warn("Price field is invalid for book: {}", newBook.toString());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price field must be a non-negative number");
		}

		return booksRepository.findById(ISBN)
			.map(book -> {
				book.setTitle(newBook.getTitle());
				book.setAuthors(newBook.getAuthors());
				book.setYearPublished(newBook.getYearPublished());
				book.setPrice(newBook.getPrice());
				book.setGenre(newBook.getGenre());
				log.info("Updated book: {}", book.toString());
				return booksRepository.save(book);
			})
			.orElseGet(() -> {
				log.info("Book with ISBN {} not found.", ISBN);
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book with ISBN "+ ISBN + " not found.");
			});
	}

	@Tag(name = "Delete a book by ISBN, throws 404 if book not found")
	@DeleteMapping("/{ISBN}")
	@PreAuthorize("hasAuthority('ADMIN')")
    public void delete(@PathVariable Long ISBN) {
		log.info("Deleting book with ISBN: {}", ISBN);
		if (!booksRepository.existsById(ISBN)) {
			log.warn("Book with ISBN {} not found for deletion", ISBN);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book with ISBN " + ISBN + " not found");
		}
		booksRepository.deleteById(ISBN);
		log.info("Deleted book with ISBN: {}", ISBN);
	}

	@Tag(name = "Find books either by title or author name or both")
	@GetMapping("/public/find")
	Books findBook(@RequestParam(required = false) String title, 
					@RequestParam(required = false) String author) {
		log.info("Finding books with title: {} and author: {}", title, author);
		Books foundBook = booksRepository.findByTitleOrAuthor(title, Arrays.asList(author));
		if (foundBook == null) {
			log.warn("No books found with title: {} and author: {}", title, author);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No books found with title: " 
			+ title + " and author: " + author);
		}
		log.info("Found book {} with title: {} and author: {}",	foundBook.toString(), 
					foundBook.getTitle(), 
					foundBook.getAuthors());
		return foundBook; 
	}
}
