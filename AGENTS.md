# Project Instructions

## Technology Stack
- Java 21
- Spring Boot 3.5
- React 19
- PostgreSQL
- Docker
- Redis
- Keycloak

## Coding Standards
- Follow SOLID principles.
- Use constructor injection only.
- Do not use field injection.
- Use Lombok where appropriate.
- Keep methods under 30 lines.
- Avoid duplicate code.

## API Standards
- All APIs must return ResponseEntity<ApiResponse<T>>.
- Use global exception handling.
- Validate input using Jakarta Validation.

## Logging
- Use SLF4J.
- Never log passwords or tokens.
- Log request IDs for tracing.

## Database
- Use JPA.
- Never use native SQL unless required.
- Use Flyway migrations.

## Testing
- JUnit 5
- Mockito
- Minimum 80% coverage.

## Security
- JWT authentication
- Role-based authorization
- No hardcoded secrets

## Output Style
- Explain changes before generating code.
- Generate complete classes.
- Mention any assumptions.