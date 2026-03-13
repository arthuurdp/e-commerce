package com.arthuurdp.e_commerce.integration;

import com.arthuurdp.e_commerce.modules.auth.dtos.LoginRequest;
import com.arthuurdp.e_commerce.modules.auth.dtos.LoginResponse;
import com.arthuurdp.e_commerce.modules.cart.CartRepository;
import com.arthuurdp.e_commerce.modules.category.CategoryRepository;
import com.arthuurdp.e_commerce.modules.category.entity.Category;
import com.arthuurdp.e_commerce.modules.product.ProductRepository;
import com.arthuurdp.e_commerce.modules.product.entity.Product;
import com.arthuurdp.e_commerce.modules.product.entity.ProductImage;
import com.arthuurdp.e_commerce.modules.user.UserRepository;
import com.arthuurdp.e_commerce.modules.user.entity.User;
import com.arthuurdp.e_commerce.modules.user.enums.Gender;
import com.arthuurdp.e_commerce.modules.user.enums.Role;
import com.arthuurdp.e_commerce.modules.cart.entity.Cart;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {

    @Autowired protected MockMvc mockMvc;
    @Autowired protected ObjectMapper objectMapper;
    @Autowired protected UserRepository userRepository;
    @Autowired protected ProductRepository productRepository;
    @Autowired protected CategoryRepository categoryRepository;
    @Autowired protected CartRepository cartRepository;
    @Autowired protected PasswordEncoder passwordEncoder;

    @MockitoBean
    protected JavaMailSender javaMailSender;

    protected User savedUser;
    protected User savedAdmin;
    protected Category savedCategory;
    protected Product savedProduct;

    @BeforeEach
    void setUpBase() {
        savedUser  = createUser("user@test.com",  "52998224725", Role.ROLE_USER,  true);
        savedAdmin = createUser("admin@test.com", "11144477735", Role.ROLE_ADMIN, true);
        savedCategory = createCategory("Electronics");
        savedProduct  = createProduct(savedCategory, 10);
    }

    protected User createUser(String email, String cpf, Role role, boolean emailVerified) {
        Cart cart = cartRepository.save(new Cart());
        User user = new User(
                "Test", "User", email,
                passwordEncoder.encode("password123"),
                cpf, "11987654321",
                LocalDate.of(1995, 6, 15), Gender.MALE, role
        );
        user.setEmailVerified(emailVerified);
        user.setCart(cart);
        return userRepository.save(user);
    }

    protected Category createCategory(String name) {
        return categoryRepository.save(new Category(name));
    }

    protected Product createProduct(Category category, int stock) {
        Product product = new Product(
                "Test Laptop", "A great laptop",
                BigDecimal.valueOf(2500), stock,
                1.5, 30, 20, 5
        );
        ProductImage image = new ProductImage("https://example.com/laptop.jpg");
        image.setProduct(product);
        image.setMainImage(true);
        product.getImages().add(image);
        product.addCategory(category);
        return productRepository.save(product);
    }

    protected String loginAndGetToken(String email) throws Exception {
        LoginRequest req = new LoginRequest(email, "password123");
        String body = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(body, LoginResponse.class).token();
    }

    protected String userToken() throws Exception {
        return loginAndGetToken("user@test.com");
    }

    protected String adminToken() throws Exception {
        return loginAndGetToken("admin@test.com");
    }

    protected String bearer(String token) {
        return "Bearer " + token;
    }
}
