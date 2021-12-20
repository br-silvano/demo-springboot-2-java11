package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.dto.JwtResponse;
import com.example.demo.entities.Category;
import com.example.demo.entities.OrderItem;
import com.example.demo.entities.Product;
import com.example.demo.entities.User;
import com.example.demo.entities.enums.OrderStatus;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class DemoApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	private static final String USERNAME = "bob@gmail.com";
	private static final String PASSWORD = "123456";

	private static final String BASE_URL = "http://localhost:%d";

	@Nested
	@Order(1)
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	public class UserTests {

		private final TestRestClient restClient;

		private final String AUTH_URL = String.format(BASE_URL.concat("/auth/token"), port);
		private final String URL = String.format(BASE_URL.concat("/api/users"), port);

		public UserTests() {
			restClient = new TestRestClient(restTemplate);
		}

		@Test
		@Order(value = 1)
		public void insertUserReturnUser() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			User newUser = new User(null, "Maria", "maria@gmail.com", "988888888", "123456");
			
			ResponseEntity<User> response = restClient.post(URL, credentials, newUser, User.class);

			User user = response.getBody();

			assertEquals(HttpStatus.CREATED, response.getStatusCode());
			assertEquals(true, user.getId() > 0);
		}

		@Test
		@Order(value = 2)
		public void loginUserReturnToken() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);
			assertEquals(true, credentials.getAccessToken() != null);
		}

		@Test
		@Order(value = 3)
		public void getAllUsersReturnListOfUsers() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);
			
			String url = URL.concat("?page=0&size=10&sort=id&sort=desc");

			ResponseEntity<Page<User>> response = restClient.get(url, credentials);

			assertEquals(HttpStatus.OK, response.getStatusCode());
		}

		@Test
		@Order(value = 4)
		public void getUserByIdReturnUser() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			String url = String.format(URL.concat("/%d"), 1);

			ResponseEntity<User> response = restClient.get(url, credentials, User.class);

			User user = response.getBody();

			assertEquals(HttpStatus.OK, response.getStatusCode());
			assertEquals(true, user.getId() > 0);
			assertEquals(true, user.getOrders().size() == 0);
		}

		@Test
		@Order(value = 5)
		public void getUserByIdReturnNotFound() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			String url = String.format(URL.concat("/%d"), 0);

			ResponseEntity<User> response = restClient.get(url, credentials, User.class);

			assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		}

		@Test
		@Order(value = 6)
		public void updateUserReturnUser() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			Long id = 1L;

			String url = String.format(URL.concat("/%d"), id);

			User updateUser = new User(id, "Bob Brown", USERNAME, "999999999", PASSWORD);

			ResponseEntity<User> response = restClient.put(url, credentials, updateUser, User.class);

			User user = response.getBody();

			assertEquals(HttpStatus.OK, response.getStatusCode());
			assertEquals(true, user.getId() > 0);
		}

		@Test
		@Order(value = 7)
		public void updateUserReturnNotFound() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			Long id = 0L;

			String url = String.format(URL.concat("/%d"), id);

			User updateUser = new User(id, "Bob Brown", USERNAME, "999999999", PASSWORD);

			ResponseEntity<User> response = restClient.put(url, credentials, updateUser, User.class);

			assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		}

		@Test
		@Order(value = 8)
		public void deleteUserReturnNoContent() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			Long id = 2L;

			String url = String.format(URL.concat("/%d"), id);

			User deleteUser = new User(id, null, null, null, null);

			ResponseEntity<User> response = restClient.delete(url, credentials, deleteUser);

			assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		}

		@Test
		@Order(value = 9)
		public void deleteUserReturnNotFound() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			Long id = 2L;

			String url = String.format(URL.concat("/%d"), id);

			User deleteUser = new User(id, null, null, null, null);

			ResponseEntity<User> response = restClient.delete(url, credentials, deleteUser);

			assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		}

	}

	@Nested
	@Order(2)
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	public class CategoryTests {

		private final TestRestClient restClient;

		private final String AUTH_URL = String.format(BASE_URL.concat("/auth/token"), port);
		private final String URL = String.format(BASE_URL.concat("/api/categories"), port);

		public CategoryTests() {
			restClient = new TestRestClient(restTemplate);
		}

		@Test
		@Order(value = 1)
		@RepeatedTest(value = 2)
		public void insertCategoryReturnCategory() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			Category newCategory = new Category(null, "Computers");

			ResponseEntity<Category> response = restClient.post(URL, credentials, newCategory, Category.class);

			Category category = response.getBody();

			assertEquals(HttpStatus.CREATED, response.getStatusCode());
			assertEquals(true, category.getId() > 0);
		}

		@Test
		@Order(value = 2)
		public void getAllCategoriesReturnListOfCategories() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			String url = URL.concat("?page=0&size=10&sort=id&sort=desc");
			
			ResponseEntity<List<Category>> response = restClient.get(url, credentials);

			assertEquals(HttpStatus.OK, response.getStatusCode());
		}

		@Test
		@Order(value = 3)
		public void getCategoryByIdReturnCategory() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			String url = String.format(URL.concat("/%d"), 1);

			ResponseEntity<Category> response = restClient.get(url, credentials, Category.class);

			Category category = response.getBody();

			assertEquals(HttpStatus.OK, response.getStatusCode());
			assertEquals(true, category.getId() > 0);
			assertEquals(true, category.getProducts().size() == 0);
		}

		@Test
		@Order(value = 4)
		public void getCategoryByIdReturnNotFound() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			String url = String.format(URL.concat("/%d"), 0);

			ResponseEntity<Category> response = restClient.get(url, credentials, Category.class);

			assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		}

		@Test
		@Order(value = 5)
		public void updateCategoryReturnCategory() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			Long id = 1L;

			String url = String.format(URL.concat("/%d"), id);

			Category updateCategory = new Category(id, "Electronics");

			ResponseEntity<Category> response = restClient.put(url, credentials, updateCategory, Category.class);

			Category category = response.getBody();

			assertEquals(HttpStatus.OK, response.getStatusCode());
			assertEquals(true, category.getId() > 0);
		}

		@Test
		@Order(value = 6)
		public void updateCategoryReturnNotFound() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			Long id = 0L;

			String url = String.format(URL.concat("/%d"), id);

			Category updateCategory = new Category(id, "Electronics");

			ResponseEntity<Category> response = restClient.put(url, credentials, updateCategory, Category.class);

			assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		}

		@Test
		@Order(value = 7)
		public void deleteCategoryReturnNoContent() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			Long id = 2L;

			String url = String.format(URL.concat("/%d"), id);

			Category deleteCategory = new Category(id, "Electronics");

			ResponseEntity<Category> response = restClient.delete(url, credentials, deleteCategory);

			assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		}

		@Test
		@Order(value = 8)
		public void deleteCategoryReturnNotFound() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			Long id = 2L;

			String url = String.format(URL.concat("/%d"), id);

			Category deleteCategory = new Category(id, "Electronics");

			ResponseEntity<Category> response = restClient.delete(url, credentials, deleteCategory);

			assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		}

	}

	@Nested
	@Order(3)
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	public class ProductTests {

		private final TestRestClient restClient;

		private final String AUTH_URL = String.format(BASE_URL.concat("/auth/token"), port);
		private final String URL = String.format(BASE_URL.concat("/api/products"), port);

		public ProductTests() {
			restClient = new TestRestClient(restTemplate);
		}

		@Test
		@Order(value = 1)
		@RepeatedTest(value = 2)
		public void insertProductReturnProduct() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			Product newProduct = new Product(null, "Smart TV", "Nulla eu imperdiet purus. Maecenas ante.", 2190.0, "");

			newProduct.getCategories().add(new Category(1L, null));

			ResponseEntity<Product> responseEntity = restClient.post(URL, credentials, newProduct, Product.class);

			Product product = responseEntity.getBody();

			assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
			assertEquals(true, product.getId() > 0);
			assertEquals(true, product.getCategories().size() > 0);
		}

		@Test
		@Order(value = 2)
		public void getAllProductsReturnListOfProducts() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			String url = URL.concat("?page=0&size=10&sort=id&sort=desc");
			
			ResponseEntity<List<Product>> responseEntity = restClient.get(url, credentials);

			assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		}

		@Test
		@Order(value = 3)
		public void getProductByIdReturnProduct() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			String url = String.format(URL.concat("/%d"), 1);

			ResponseEntity<Product> response = restClient.get(url, credentials, Product.class);

			Product product = response.getBody();

			assertEquals(HttpStatus.OK, response.getStatusCode());
			assertEquals(true, product.getId() > 0);
			assertEquals(true, product.getOrders().size() == 0);
			assertEquals(true, product.getCategories().size() == 1);
		}

		@Test
		@Order(value = 4)
		public void getProductByIdReturnNotFound() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			String url = String.format(URL.concat("/%d"), 0);

			ResponseEntity<Product> response = restClient.get(url, credentials, Product.class);

			assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		}

		@Test
		@Order(value = 5)
		public void updateProductReturnProduct() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			Long id = 1L;

			String url = String.format(URL.concat("/%d"), id);

			Product updateProduct = new Product(id, "Smart TV", "Nulla eu imperdiet purus. Maecenas ante.", 2199.0, "");

			updateProduct.getCategories().add(new Category(1L, null));

			ResponseEntity<Product> response = restClient.put(url, credentials, updateProduct, Product.class);

			Product product = response.getBody();

			assertEquals(HttpStatus.OK, response.getStatusCode());
			assertEquals(true, product.getId() > 0);
			assertEquals(true, product.getCategories().size() > 0);
		}

		@Test
		@Order(value = 6)
		public void updateProductReturnNotFound() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			Long id = 0L;

			String url = String.format(URL.concat("/%d"), id);

			Product updateProduct = new Product(id, "Smart TV", "Nulla eu imperdiet purus. Maecenas ante.", 2199.0, "");

			updateProduct.getCategories().add(new Category(1L, null));

			ResponseEntity<Product> response = restClient.put(url, credentials, updateProduct, Product.class);

			assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		}

		@Test
		@Order(value = 7)
		public void deleteProductReturnNoContent() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			Long id = 2L;

			String url = String.format(URL.concat("/%d"), id);

			Product deleteProduct = new Product(id, null, null, null, null);

			ResponseEntity<Product> response = restClient.delete(url, credentials, deleteProduct);

			assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		}

		@Test
		@Order(value = 8)
		public void deleteProductReturnNotFound() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			Long id = 2L;

			String url = String.format(URL.concat("/%d"), id);

			Product deleteProduct = new Product(id, null, null, null, null);

			ResponseEntity<Product> response = restClient.delete(url, credentials, deleteProduct);

			assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		}

	}

	@Nested
	@Order(4)
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	public class OrderTests {

		private final TestRestClient restClient;

		private final String AUTH_URL = String.format(BASE_URL.concat("/auth/token"), port);
		private final String URL = String.format(BASE_URL.concat("/api/orders"), port);

		public OrderTests() {
			restClient = new TestRestClient(restTemplate);
		}

		@Test
		@Order(value = 1)
		@RepeatedTest(value = 2)
		public void createOrderReturnOrder() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			User user = new User(1L, null, null, null, null);

			com.example.demo.entities.Order newOrder = new com.example.demo.entities.Order(null,
					Instant.parse("2021-12-12T15:21:22Z"), OrderStatus.WATTING_PAYMENT, user);
			Product product = new Product(1l, null, null, null, null);

			OrderItem orderItem = new OrderItem(null, product, 1, 2199.0);

			newOrder.getItems().add(orderItem);

			ResponseEntity<com.example.demo.entities.Order> response = restClient.post(URL, credentials, newOrder,
					com.example.demo.entities.Order.class);

			com.example.demo.entities.Order order = response.getBody();

			assertEquals(HttpStatus.CREATED, response.getStatusCode());
			assertEquals(true, order.getId() > 0);
			assertEquals(OrderStatus.WATTING_PAYMENT, order.getOrderStatus());
			assertEquals(true, order.getItems().size() > 0);
			assertEquals(2199.0, order.getTotal());
		}

		@Test
		@Order(value = 2)
		public void getAllOrdersReturnListOfOrders() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			String url = URL.concat("?page=0&size=10&sort=id&sort=desc");
			
			ResponseEntity<List<com.example.demo.entities.Order>> responseEntity = restClient.get(url, credentials);

			assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		}

		@Test
		@Order(value = 3)
		public void getOrderByIdReturnOrder() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			String url = String.format(URL.concat("/%d"), 1);

			ResponseEntity<com.example.demo.entities.Order> response = restClient.get(url, credentials,
					com.example.demo.entities.Order.class);

			com.example.demo.entities.Order order = response.getBody();

			assertEquals(HttpStatus.OK, response.getStatusCode());
			assertEquals(true, order.getId() > 0);
			assertEquals(true, order.getItems().size() > 0);
		}

		@Test
		@Order(value = 4)
		public void paidOrderReturnOrder() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			Long id = 1L;

			String url = String.format(URL.concat("/%d/paid"), id);

			com.example.demo.entities.Order paidOrder = new com.example.demo.entities.Order(id, null,
					OrderStatus.PAID, null);

			ResponseEntity<com.example.demo.entities.Order> response = restClient.put(url, credentials, paidOrder,
					com.example.demo.entities.Order.class);

			com.example.demo.entities.Order order = response.getBody();

			assertEquals(HttpStatus.OK, response.getStatusCode());
			assertEquals(true, order.getId() > 0);
			assertEquals(OrderStatus.PAID, order.getOrderStatus());
			assertEquals(true, order.getPayment().getId() == order.getId());
		}

		@Test
		@Order(value = 5)
		public void paidOrderReturnBadRequest() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			Long id = 1L;

			String url = String.format(URL.concat("/%d/paid"), id);

			com.example.demo.entities.Order paidOrder = new com.example.demo.entities.Order(id, null,
					OrderStatus.PAID, null);

			ResponseEntity<com.example.demo.entities.Order> response = restClient.put(url, credentials, paidOrder,
					com.example.demo.entities.Order.class);

			assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		}

		@Test
		@Order(value = 6)
		public void paidOrderReturnNotFound() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			Long id = 0L;

			String url = String.format(URL.concat("/%d/paid"), id);

			com.example.demo.entities.Order paidOrder = new com.example.demo.entities.Order(id, null,
					OrderStatus.PAID, null);

			ResponseEntity<com.example.demo.entities.Order> response = restClient.put(url, credentials, paidOrder,
					com.example.demo.entities.Order.class);

			assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		}

		@Test
		@Order(value = 7)
		public void shippedOrderReturnOrder() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			Long id = 1L;

			String url = String.format(URL.concat("/%d/shipped"), id);

			com.example.demo.entities.Order shippedOrder = new com.example.demo.entities.Order(id, null,
					OrderStatus.SHIPPED, null);

			ResponseEntity<com.example.demo.entities.Order> response = restClient.put(url, credentials, shippedOrder,
					com.example.demo.entities.Order.class);

			com.example.demo.entities.Order order = response.getBody();

			assertEquals(HttpStatus.OK, response.getStatusCode());
			assertEquals(true, order.getId() > 0);
			assertEquals(OrderStatus.SHIPPED, order.getOrderStatus());
		}

		@Test
		@Order(value = 8)
		public void shippedOrderReturnBadRequest() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			Long id = 1L;

			String url = String.format(URL.concat("/%d/shipped"), id);

			com.example.demo.entities.Order shippedOrder = new com.example.demo.entities.Order(id, null,
					OrderStatus.SHIPPED, null);

			ResponseEntity<com.example.demo.entities.Order> response = restClient.put(url, credentials, shippedOrder,
					com.example.demo.entities.Order.class);

			assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		}

		@Test
		@Order(value = 9)
		public void shippedOrderReturnNotFound() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			Long id = 0L;

			String url = String.format(URL.concat("/%d/shipped"), id);

			com.example.demo.entities.Order shippedOrder = new com.example.demo.entities.Order(id, null,
					OrderStatus.SHIPPED, null);

			ResponseEntity<com.example.demo.entities.Order> response = restClient.put(url, credentials, shippedOrder,
					com.example.demo.entities.Order.class);

			assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		}

		@Test
		@Order(value = 10)
		public void deliveredOrderReturnOrder() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			Long id = 1L;

			String url = String.format(URL.concat("/%d/delivered"), id);

			com.example.demo.entities.Order deliveredOrder = new com.example.demo.entities.Order(id, null,
					OrderStatus.DELIVERED, null);

			ResponseEntity<com.example.demo.entities.Order> response = restClient.put(url, credentials,
					deliveredOrder, com.example.demo.entities.Order.class);

			com.example.demo.entities.Order order = response.getBody();

			assertEquals(HttpStatus.OK, response.getStatusCode());
			assertEquals(true, order.getId() > 0);
			assertEquals(OrderStatus.DELIVERED, order.getOrderStatus());
		}

		@Test
		@Order(value = 11)
		public void deliveredOrderReturnBadRequest() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			Long id = 1L;

			String url = String.format(URL.concat("/%d/delivered"), id);

			com.example.demo.entities.Order deliveredOrder = new com.example.demo.entities.Order(id, null,
					OrderStatus.DELIVERED, null);

			ResponseEntity<com.example.demo.entities.Order> response = restClient.put(url, credentials,
					deliveredOrder, com.example.demo.entities.Order.class);

			assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		}

		@Test
		@Order(value = 12)
		public void deliveredOrderReturnNotFound() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			Long id = 0L;

			String url = String.format(URL.concat("/%d/delivered"), id);

			com.example.demo.entities.Order deliveredOrder = new com.example.demo.entities.Order(id, null,
					OrderStatus.DELIVERED, null);

			ResponseEntity<com.example.demo.entities.Order> response = restClient.put(url, credentials,
					deliveredOrder, com.example.demo.entities.Order.class);

			assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		}

		@Test
		@Order(value = 13)
		public void cancelOrderReturnOrder() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			Long id = 2L;

			String url = String.format(URL.concat("/%d/cancel"), id);

			com.example.demo.entities.Order cancelOrder = new com.example.demo.entities.Order(id, null,
					OrderStatus.CANCELED, null);

			ResponseEntity<com.example.demo.entities.Order> response = restClient.put(url, credentials, cancelOrder,
					com.example.demo.entities.Order.class);

			com.example.demo.entities.Order order = response.getBody();

			assertEquals(HttpStatus.OK, response.getStatusCode());
			assertEquals(true, order.getId() > 0);
			assertEquals(OrderStatus.CANCELED, order.getOrderStatus());
		}

		@Test
		@Order(value = 14)
		public void cancelOrderReturnBadRequest() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			Long id = 1L;

			String url = String.format(URL.concat("/%d/cancel"), id);

			com.example.demo.entities.Order cancelOrder = new com.example.demo.entities.Order(id, null,
					OrderStatus.CANCELED, null);

			ResponseEntity<com.example.demo.entities.Order> response = restClient.put(url, credentials, cancelOrder,
					com.example.demo.entities.Order.class);

			assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		}

		@Test
		@Order(value = 15)
		public void cancelOrderReturnNotFound() throws Exception {
			JwtResponse credentials = restClient.login(AUTH_URL, USERNAME, PASSWORD);

			Long id = 0L;

			String url = String.format(URL.concat("/%d/cancel"), id);

			com.example.demo.entities.Order cancelOrder = new com.example.demo.entities.Order(id, null,
					OrderStatus.CANCELED, null);

			ResponseEntity<com.example.demo.entities.Order> response = restClient.put(url, credentials, cancelOrder,
					com.example.demo.entities.Order.class);

			assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		}
	}

}