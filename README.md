# Blog Application (Maven)

This is a Maven-based Spring Boot blog application skeleton with:
- User registration & login (JWT)
- OAuth2 social login (Google/GitHub) — placeholders in application.properties
- Basic Post entity with create/list endpoints
- H2 in-memory DB for quick testing

To run:
1. mvn clean package
2. mvn spring-boot:run

Replace OAuth client IDs/secrets and the JWT secret for production use.
