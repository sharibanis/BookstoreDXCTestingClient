package bookstore.sharib;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.opencsv.CSVReader;

@Configuration
public class InitAndLoadDatabase implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(InitAndLoadDatabase.class);
	
	@Value("${authors.csv.filePathAndName}")
	String authorsFilePathAndName;
	@Value("${books.csv.filePathAndName}")
	String booksFilePathAndName;

	private final AuthorsRepository authRepository;
	private final BooksRepository booksRepository;

    public InitAndLoadDatabase(AuthorsRepository authRepository, BooksRepository booksRepository) {
        this.authRepository = authRepository;
        this.booksRepository = booksRepository;
    }

	@Override
    public void run(String... args) throws Exception {
		log.info("Initializing and loading database...");
		initAuthorsRepository(authRepository);
		initBooksRepository(booksRepository);
		log.info("Database initialized and loaded!");
	}

	@Bean
	@Order(1)
	CommandLineRunner initAuthorsRepository(AuthorsRepository repository) {

		return args -> {
			log.info("initAuthorsRepository");
			log.info("From file: " + authorsFilePathAndName);
			File file = new File(authorsFilePathAndName);
			FileReader filereader = new FileReader(file);
				try(CSVReader csvReader = new CSVReader(filereader)) {
					String[] line;
					String lineString = "";
					int insertCount = 0;
					log.info("Uploading file: " + file.getCanonicalPath());
					Authors author = new Authors();
					csvReader.readNext(); //skip first line (header)
					while ((line = csvReader.readNext()) != null) {
						for (String cell : line) {
							lineString += cell.trim() + ", ";
						}
						log.info("lineString: " + lineString);
						if (lineString.trim() != "") {
							author = new Authors();
							String name = line[0].trim();
							author.setName(name);
							String birthday = line[1].trim();
							author.setBirthday(birthday);
						}
						lineString = "";
						log.info("author.toString(): "+author.toString());
						repository.save(author);
						insertCount++;
					}
					log.info("insertCount: " + insertCount);
					log.info("Authors Repository initialized and loaded!");
				}	catch(Exception exception){  
					log.warn("Error initializing Authors Repository: " + exception.toString());
					exception.printStackTrace();
				}
		};
	}
	
	@Bean
	@Order(2)
	CommandLineRunner initBooksRepository(BooksRepository repository) {

		return args -> {
			log.info("initBooksRepository");
			log.info("From file: " + booksFilePathAndName);
			File file = new File(booksFilePathAndName);
			FileReader filereader = new FileReader(file);
				try(CSVReader csvReader = new CSVReader(filereader)) {
					String[] line;
					String lineString = "";
					int insertCount = 0;
					log.info("Uploading file: " + file.getCanonicalPath());
					Books book = new Books();
					csvReader.readNext(); //skip first line (header)
					while ((line = csvReader.readNext()) != null) {
						for (String cell : line) {
							lineString += cell + ", ";
						}
						log.info("lineString: " + lineString);
						if (lineString.trim() != "") {
							book = new Books();
							String isbn = line[0].trim();
							book.setIsbn(Long.valueOf(isbn));
							String title = line[1].trim();
							book.setTitle(title);
							List<String> authors = new ArrayList<String>();
							JSONArray jsonArray = new JSONArray(line[2].trim());
							for (int i = 0; i < jsonArray.length(); i++) {
								String authorName = jsonArray.getString(i);
								if (authorName != null) {
									authors.add(authorName);
								} else {
									log.warn("Author name is null in JSON array for book: " + lineString);
								}
							}
							log.info("Authors: " + authors.toString());
							book.setAuthors(authors);
							int year = Integer.valueOf(line[3].trim());
							book.setYearPublished(year);
							double price = Double.valueOf(line[4].trim());
							book.setPrice(price);
							String genre = line[5].trim();
							book.setGenre(genre);

						}
						lineString = "";
						log.info("book.toString(): "+book.toString());
						repository.save(book);
						insertCount++;
					}
					log.info("insertCount: " + insertCount);
					log.info("Books Repository initialized and loaded!");
				}	catch(Exception exception){  
					log.warn("Error initializing Books Repository: " + exception.toString());
					exception.printStackTrace();
				}
		};
	}

}
