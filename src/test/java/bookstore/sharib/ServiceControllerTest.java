package bookstore.sharib;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import static org.hamcrest.Matchers.equalTo;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceControllerTest {
	private static final Logger log = LoggerFactory.getLogger(ServiceControllerTest.class);
	private static final String baseUri = "http://localhost:8080/"; 
    
	@BeforeAll
	public static void setUp() throws Exception {
		log.info("Starting tests...");
		RestAssured.baseURI = baseUri;
	}
	
	@Test
	@Order(1)
	public void whenRequestHealth_thenOK() {
    	log.info("whenRequestHealth_thenOK()");
		//given().log().uri().when().get("/actuator/health");
		Response response = RestAssured.get("/actuator/health");
		response.then().assertThat().statusCode(200).log().all();
	}

	@Test
	@Order(2)
	public void whenRequestGetFind_thenOK() {
    	log.info("whenRequestGetFind_thenOK()");
		Response response = RestAssured.get("/bookstore/public/find?title=1984&author=George%20Orwell");
		response.then().assertThat().statusCode(200).log().all();
		response.then().assertThat().body("title", equalTo("1984"));
	}
	
	@Test
	@Order(3)
	public void whenRequestGetFind_thenNotOK() {
    	log.info("whenRequestGetFind_thenNotOK()");
		Response response = RestAssured.get("/bookstore/public/find?title=1994&author=George%20Orwell");
		response.then().assertThat().statusCode(401).log().all();
	}
	
	@Test
	@Order(4)
	public void whenRequestPostAddBook_thenOK() {
    	log.info("whenRequestPostAddBook_thenOK()");
		try {
			JSONObject requestBody = new JSONObject(); 
			requestBody.put("isbn", "1234567891123");
			requestBody.put("title", "Test Book");
			JSONArray authorArray = new JSONArray();
			authorArray.put("Test Author");
			requestBody.put("authors", authorArray);
			requestBody.put("yearPublished", 2024);
			requestBody.put("price", 19.99);
			requestBody.put("genre", "Test Genre");
			log.info("requestBody: {}", requestBody.toString());
			RequestSpecification request = RestAssured.given();
			request.header("Content-Type", "application/json");
			request.body(requestBody.toString()); 
			request.auth().basic("user", "password");
			request.when()
			.post("/bookstore/addBook")
			.then()
			.assertThat()
			.statusCode(HttpStatus.OK.value());
			log.info("Book {} added successfully.", "Test Book");
		} catch (Exception ex) {
			log.warn("Exception occurred while adding book {}. ", 
				"Test Book");
			log.warn("Exception: ", ex);
		}
	}
	
	@Test
	@Order(5)
	public void whenRequestPutUpdateBook_thenOK() {
    	log.info("whenRequestPutUpdateBook_thenOK()");
		try {
			JSONObject requestBody = new JSONObject(); 
			requestBody.put("isbn", "1234567891123");
			requestBody.put("title", "Test Book");
			JSONArray authorArray = new JSONArray();
			authorArray.put("Test Author");
			requestBody.put("authors", authorArray);
			requestBody.put("yearPublished", 2024);
			requestBody.put("price", 19.99);
			requestBody.put("genre", "Test Genre");
			log.info("requestBody: {}", requestBody.toString());
			RequestSpecification request = RestAssured.given();
			request.header("Content-Type", "application/json");
			request.body(requestBody.toString()); 
			request.auth().basic("user", "password");
			request.when()
			.put("/bookstore/1234567891123")
			.then()
			.assertThat()
			.statusCode(HttpStatus.OK.value());
			log.info("Book {} updated successfully.", "Test Book");
		} catch (Exception ex) {
			log.warn("Exception occurred while updating book {}. ", 
				"Test Book");
			log.warn("Exception: ", ex);
		}
	}

	@Test
	@Order(6)
	public void whenRequestDeleteDeleteBook_thenOK() {
    	log.info("whenRequestDeleteDeleteBook_thenOK()");
		try {
			RequestSpecification request = RestAssured.given();
			request.header("Content-Type", "application/json");
			request.auth().basic("user", "password");
			request.when()
			.delete("/bookstore/1234567891123")
			.then()
			.assertThat()
			.statusCode(HttpStatus.OK.value());
			log.info("Book {} deleted successfully.", "Test Book");
		} catch (Exception ex) {
			log.warn("Exception occurred while deleting book {}. ", 
				"Test Book");
			log.warn("Exception: ", ex);
		}
	}

	@AfterAll
	public static void cleanUp(){
		log.info("@AfterAll cleanUp() method called");
	}
}
