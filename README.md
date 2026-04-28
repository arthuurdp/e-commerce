# E-Commerce Backend API

A full-featured e-commerce REST API built with **Spring Boot**, developed as a personal learning project to deepen my understanding of modern backend development in Java. The project covers everything from authentication and payments to shipping integration and automated testing.

---

### API Documentation
Swagger UI is available at:
```
http://localhost:8080/swagger-ui/index.html
```

---

## Educational Purpose

This project was built entirely for learning purposes. The goal was to go beyond simple tutorials and build something that mirrors real-world backend systems — including third-party integrations, security layers, database migrations, and a comprehensive test suite.

Throughout development, I challenged myself to think about concerns like:
- How do you design a secure and scalable REST API?
- How should a checkout flow look under the hood?
- How do you handle webhooks?
- How do you write unit and automated tests?

---

## Concepts Learned

### Architecture & Design
- **Layered architecture** — separating Controllers, Services, Repositories, and Mappers cleanly
- **Module-based package structure** — grouping code by feature/domain (auth, cart, order, payment, shipping, etc.) rather than by technical layer
- **DTO pattern** — using dedicated request/response records to decouple the API contract from internal entities
- **MapStruct** for compile-time, type-safe object mapping between entities and DTOs

### Security
- **Spring Security** with stateless JWT authentication
- Custom `SecurityFilter` that extracts and validates tokens on every request
- Role-based access control (`ROLE_USER`, `ROLE_ADMIN`) using `@PreAuthorize`
- Custom `AuthenticationEntryPoint` and `AccessDeniedHandler` for structured JSON error responses
- **Rate limiting** with Bucket4j to protect sensitive endpoints (login, password reset, email verification) from brute-force attacks

### Database
- **JPA/Hibernate** for ORM with entity relationships (`@OneToMany`, `@ManyToMany`, `@OneToOne`)
- **Flyway** for version-controlled database migrations — every schema change is tracked and reproducible
- `@EntityGraph` to avoid N+1 query problems on paginated endpoints
- JPA Specifications for dynamic, composable product filtering (by name, by category)

### Email & Verification Flow
- **Spring Mail** (JavaMailSender) for sending transactional emails
- Multi-step verification flows: email confirmation, password change confirmation, and password reset — each using short-lived, single-use 6-digit codes stored in dedicated token tables
- Separation between `EmailSenderService` (low-level sending) and `EmailService` (business logic)

### Payments — Stripe
- Stripe Checkout Session creation with dynamic line items (products + freight)
- Support for multiple payment methods: **credit card**, **PIX**, and **boleto**
- Webhook verification using HMAC signature validation (`Stripe-Signature` header)
- Handling `checkout.session.completed` and `checkout.session.expired` events to update order and payment status automatically
- Cart clearing and stock restoration triggered by webhook events

### Shipping — Melhor Envio
- Integration with the **Melhor Envio API** to calculate real freight options based on postal code and package dimensions
- Automated label purchase and generation after payment confirmation
- Tracking URL generation for customers
- Webhook handling with HMAC-SHA256 signature verification to receive shipping status updates (posted, in transit, delivered, cancelled)

### CEP Lookup — ViaCEP
- Integration with **ViaCEP** to auto-fill address fields (street, neighborhood, city, state) from a Brazilian postal code
- Cities and states are pre-seeded in the database — all 5,570 Brazilian municipalities are available

### File Storage
- Local file storage for product images and user profile pictures
- Files served statically via a configured resource handler (`/uploads/**`)
- UUID-based filenames to avoid collisions and prevent path traversal

### Testing
- **Unit tests** with JUnit 5 + Mockito — testing service logic in complete isolation
- **Integration tests** with `@SpringBootTest` + `MockMvc` — testing the full HTTP request/response cycle
- In-memory **H2 database** for tests (Flyway disabled, schema created by Hibernate)
- Mocking of external dependencies (`JavaMailSender`, `MelhorEnvioClient`, `ViaCepClient`, Stripe) to keep tests fast and deterministic
- A full **Happy Path test** that simulates the entire user journey: register → verify email → login → add to cart → calculate freight → create address → checkout → simulate Stripe webhook → verify order paid → verify shipping label generated → verify cart cleared

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5 |
| Security | Spring Security + JWT (Auth0 java-jwt) |
| ORM | Spring Data JPA + Hibernate |
| Database | MySQL 8 (production), H2 (tests) |
| Migrations | Flyway |
| Object Mapping | MapStruct |
| Validation | Jakarta Bean Validation |
| Rate Limiting | Bucket4j |
| Payments | Stripe Java SDK |
| Shipping | Melhor Envio REST API |
| CEP Lookup | ViaCEP REST API |
| HTTP Client | Spring WebFlux (WebClient) |
| Email | Spring Mail (SMTP/Gmail) |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Build | Maven (Maven Wrapper) |
| Containerization | Docker + Docker Compose |
| Testing | JUnit 5, Mockito, MockMvc |

---

## Project Structure

```
src/main/java/com/arthuurdp/e_commerce/
├── infrastructure/
│   ├── config/          # Spring configs (Security, Stripe, Web, Webhooks, OpenAPI)
│   └── security/        # JWT filter, token service, UserDetails, handlers
├── modules/
│   ├── address/         # Address, City, State management + ViaCEP integration
│   ├── auth/            # Register & login
│   ├── cart/            # Cart management + freight calculation
│   ├── category/        # Product categories
│   ├── checkout/        # Checkout orchestration
│   ├── comment/         # Review comments
│   ├── email/           # Email verification, password change/reset flows
│   ├── myactivity/      # User activity (notifications, reviews, favorites)
│   ├── notification/    # In-app notifications
│   ├── order/           # Order creation and retrieval
│   ├── payment/         # Stripe payment + webhook handling
│   ├── product/         # Product CRUD, images, specifications
│   ├── review/          # Product reviews
│   ├── shipping/        # Melhor Envio integration + webhook handling
│   └── user/            # User profile management
└── shared/
    ├── exceptions/      # Custom exception classes
    ├── storage/         # File storage service
    └── validators/      # CPF and phone validators
```

---

## Getting Started

### Prerequisites
- Java 17+
- Docker & Docker Compose
- A `.env` file based on `.env.example`

### Running with Docker
```bash
cp .env.example .env
# Fill in the required environment variables
docker compose up --build
```

The API will be available at `http://localhost:8080`.

### Running Tests
```bash
./mvnw test
```

---

## Key Environment Variables

| Variable | Description |
|---|---|
| `DB_*` | MySQL connection settings |
| `JWT_SECURITY_TOKEN` | Secret for signing JWT tokens |
| `STRIPE_SECRET_KEY` | Stripe API secret key |
| `STRIPE_WEBHOOK_SECRET` | Secret for verifying Stripe webhook signatures |
| `MELHOR_ENVIO_*` | Melhor Envio API credentials and store info |
| `USER_EMAIL` / `USER_PASSWORD` | Gmail SMTP credentials |
| `STORE_*` | Store information used in shipping labels |

---

## Personal Notes

- So this is my biggest project so far, took me so much time to get it done, but it was very good to develop my backend skills with new tools and techniques that I've never touched before.
- I'm still learning a lot about security and best practices, and I'm always looking for ways to improve my code.
- The main headache for me was the order and payment implementation, which was a bit tricky to get right because I didn't know how to consume an external API.
- I'm looking forward to studying about microservices and how they can be used to build more scalable and maintainable systems.
- Also, I want to learn more about containerization and how it can be used to deploy applications more efficiently.
- Thank you for reading!
