# User Management Microservice

A RESTful user management and authentication microservice built with **Java 17** and **Spring Boot 3.4.4**. It provides
user CRUD operations, registration and login, JWT-based authentication, role-aware endpoint protection, request
validation, centralized exception handling, and interactive API documentation.

> **Project status:** Educational/practice project. Review the security notes below before deploying or exposing it to
> the internet.

## Tech stack

| Technology                     | Purpose                                        |
|--------------------------------|------------------------------------------------|
| Java 17                        | Application language                           |
| Spring Boot 3.4.4 / Spring Web | REST API and application framework             |
| Spring Data JPA / Hibernate    | Persistence and repository access              |
| H2 Database                    | In-memory relational database                  |
| Spring Security                | Authentication and authorization configuration |
| JJWT 0.11.5                    | JWT creation and verification                  |
| Jakarta Bean Validation        | Request data validation                        |
| Springdoc OpenAPI 2.8.6        | Swagger UI and OpenAPI documentation           |
| Maven                          | Dependency management and build                |
| Spring Boot Test               | Automated testing dependencies                 |
| Spring Boot DevTools           | Development convenience                        |

## Features

- User registration and email/password login.
- JWT generation and token validation.
- User creation, retrieval, update and deletion.
- Search for users by ID, username or email.
- List all users and check administrator status.
- Change a user's password after checking the existing password.
- Separate request and response DTOs, including dedicated create, update and password-change requests.
- Password hashing through Spring Security's `PasswordEncoder`.
- Validation annotations on incoming request DTOs.
- Application-specific exceptions and a controller exception handler.
- OpenAPI annotations and Swagger UI documentation.
- Test infrastructure based on Spring Boot's test starter.

## Project architecture

The code is organized by feature, primarily under `auth` and `user`:

```text
src/main/java/com/kata/microserviceUser/
├── MicroserviceUserApplication.java
├── auth/
│   ├── authDTO/               # AuthRequest, AuthResponse
│   ├── controller/            # IAuthController, AuthControllerImpl
│   ├── security/              # JwtAuthenticationFilter, JwtUtil, SecurityConfig
│   └── service/               # AuthService
└── user/
    ├── config/                # Password encoder, validation, user details
    ├── controller/            # IUserController, UserControllerImpl
    ├── entity/                # User JPA entity
    ├── exceptions/            # Custom exceptions and exception handler
    ├── repository/            # UserRepository
    ├── service/               # UserService
    └── userDTO/               # Request/response DTOs and UserMapper

src/main/resources/
├── application.properties
├── schema.sql
└── data.sql
```

The REST controllers delegate to services, which coordinate persistence through `UserRepository`. DTOs define the API's
input/output models so the `User` JPA entity and its password field are not returned directly to clients.

## API endpoints

The following routes are defined by the controller implementations. The application listens on **port 8081** by default.

### Authentication (`/auth`)

| Method | Endpoint         | Description                                             |
|--------|------------------|---------------------------------------------------------|
| `POST` | `/auth/register` | Register a user                                         |
| `POST` | `/auth/login`    | Authenticate and receive a JWT                          |
| `GET`  | `/auth/validate` | Validate a token supplied in the `Authorization` header |

### Users (`/users`)

| Method   | Endpoint                     | Description                |
|----------|------------------------------|----------------------------|
| `GET`    | `/users`                     | List users                 |
| `GET`    | `/users/{userId}`            | Find user by ID            |
| `GET`    | `/users/username/{username}` | Find user by username      |
| `GET`    | `/users/email/{email}`       | Find user by email         |
| `GET`    | `/users/isAdmin/{userId}`    | Check administrator status |
| `POST`   | `/users`                     | Create a user              |
| `PUT`    | `/users/{userId}`            | Update user details        |
| `PUT`    | `/users/password/{userId}`   | Update a user's password   |
| `DELETE` | `/users/{userId}`            | Delete a user              |

**Security behavior:** The current `SecurityConfig` permits GET requests under `/users/**` and requests under
`/auth/**`. It requires `ROLE_ADMIN` for POST, PUT and DELETE operations under `/users/**`, subject to the precise
matcher ordering and filter behavior. Review authorization carefully before using this service beyond a practice
environment.

## Getting started

### Requirements

- JDK **17** or later (the Maven project targets Java 17)
- Maven, or the Maven wrapper if available in your checkout
- A terminal or Java IDE

### 1. Clone the repository

```bash
git clone https://github.com/Veselin86/Microservice_User.git
cd Microservice_User
git checkout develope
```

### 2. Configure the application

The current `src/main/resources/application.properties` configures an **in-memory H2 database** and port **8081**. The
SQL initialization scripts are `schema.sql` and `data.sql`, and Hibernate schema generation is disabled
(`spring.jpa.hibernate.ddl-auto=none`).

Before sharing or deploying the service, replace the hard-coded JWT secret with an environment-based configuration, for
example:

```properties
jwt.secret=${JWT_SECRET}
jwt.expiration=3600000
```

Set `JWT_SECRET` to a strong, randomly generated secret in your local environment. Never commit real secrets to source
control.

### 3. Run the application

Using a local Maven installation:

```bash
mvn spring-boot:run
```

The API should be reachable at `http://localhost:8081` once the application starts successfully.

### 4. Explore the API

With the Springdoc dependency present, the standard documentation URLs are:

- Swagger UI: `http://localhost:8081/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8081/v3/api-docs`

### 5. Run tests

```bash
mvn test
```

The project includes Spring Boot's testing dependency. Consult `src/test` for the available test classes and their
specific coverage.

## Example requests

### Register

```http
POST http://localhost:8081/auth/register
Content-Type: application/json
```

```json
{
  "username": "jdoe",
  "name": "John Doe",
  "email": "jdoe@example.com",
  "password": "replace-with-a-strong-password",
  "admin": false
}
```

The Java request DTO includes an administrator flag (`isAdmin`), but the exact JSON property name should be verified
against Jackson serialization. For security, public registration should not allow clients to grant themselves
administrator privileges.

### Login

```http
POST http://localhost:8081/auth/login
Content-Type: application/json
```

```json
{
  "email": "jdoe@example.com",
  "password": "replace-with-a-strong-password"
}
```

On success, the authentication service generates a JWT, returned in an `AuthResponse` object. Use the issued token as a
bearer token when calling protected endpoints, according to the authentication filter's expected header format.

## Database

The H2 schema defines a `users` table with these columns:

| Column     | Description                |
|------------|----------------------------|
| `user_id`  | Auto-generated primary key |
| `username` | Unique username            |
| `name`     | User display name          |
| `email`    | Unique email               |
| `password` | Stored password hash       |
| `is_admin` | Administrator flag         |

`data.sql` inserts sample accounts for local development. **These seed accounts are for practice only and should not be
used in a public deployment.**

## JWT and authorization

`JwtUtil` issues HMAC-SHA256-signed JWTs with the user's email as subject, along with `id` and `isAdmin` claims. The
configured expiration is **3,600,000 milliseconds (one hour)**. `JwtAuthenticationFilter` is registered ahead of Spring
Security's `UsernamePasswordAuthenticationFilter`.

The service uses Spring Security and role-based authorization checks for administrative operations. A `PasswordEncoder`
is used when registering users and changing passwords.

## Error handling and validation

The project includes a controller exception handler and domain-specific exception classes, including not-found,
invalid-credentials, unauthorized and data-integrity-related errors. Request DTOs use Jakarta Validation constraints
such as `@NotBlank`, `@Email` and `@Size`.

## Security notes and known limitations

This is a practice implementation, **not a production-hardened authentication service**. Before production use:

1. **Remove and rotate the committed JWT secret.** Move the signing key to environment variables or a secret manager.
2. **Prevent privilege escalation at registration.** Do not accept an administrator flag from an untrusted registration
   request.
3. **Review password-change authorization.** Ensure a user can change only their own password unless an authorized
   administrator explicitly performs the action.
4. **Review Spring Security request matchers.** In the current configuration, the broad PUT matcher precedes the
   password-specific matcher; this can affect the intended access rules.
5. **Check token validation handling.** Confirm the `/auth/validate` endpoint strips the `Bearer ` prefix if the client
   sends a standard Authorization header.
6. **Do not expose sample credentials or H2 development data in production.**
7. **Review sensitive logging and entity methods.** The current `User.toString()` includes the password hash and should
   be changed to exclude it.
8. **Review JWT authentication and authorization end-to-end**, including signature validation, authorities, expiry and
   error responses.

## Author

Developed as a Java / Spring Boot microservice practice project.

Repository: [Veselin86/Microservice_User](https://github.com/Veselin86/Microservice_User)

