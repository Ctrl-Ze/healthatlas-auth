# healthatlas-auth (Cerberus)

**Codename:** Cerberus — *the guardian of the HealthAtlas gates.*

Cerberus is the authentication and authorization service of the HealthAtlas ecosystem.  
It guards user identities, issues JWT access tokens, enforces access control, and provides foundational security APIs for the entire platform.

---

## Responsibilities
- User registration and credential management
- Secure login with password hashing (BCrypt)
- JWT generation and validation for authenticated access
- Role-based authorization via embedded claims
- Exception mapping and consistent error responses
- Trace ID propagation for observability and debugging
- Integration with other services through standardized tokens

---

## Security Notes

Cerberus uses **JWT (JSON Web Tokens)** for stateless authentication.

### Current Token Structure
Includes:
- `sub` — username
- `upn` — user email
- `user_id` — internal numeric ID (stable across renames)
- `groups` — user roles (e.g. `USER`, `ADMIN`)
- `iat` / `exp` — issued and expiration times
- `iss` — token issuer (`healthatlas`)

### TODO (Security Roadmap)
- [ ] Move private signing key to a secure secret store (Vault / KMS)
- [ ] Implement key rotation and expose a JWKS endpoint
- [ ] Add refresh tokens for long-lived sessions
- [ ] Introduce permissions-based scopes
- [ ] Implement `/auth/me` endpoint to return user info from JWT
- [ ] Integrate with Athena for cross-service identity verification

---

## Related Services

| Service | Codename | Description |
|----------|-----------|-------------|
| healthatlas-core | Athena | Aggregation and orchestration layer |
| healthatlas-ocr | Hermes | OCR and document extraction |
| healthatlas-ingest | Iris / Pan | Wearable ingestion |
| healthatlas-analytics | Themis | Insights and analytics |
| healthatlas-audit | Mnemosyne | Audit and trace logging |
| healthatlas-notify | Echo | Alerts and notifications |
| healthatlas-ai | Chiron | AI and guidance layer |
| healthatlas-web | Helios | Frontend dashboard |

---

## Local Development

Cerberus runs locally with Quarkus and PostgreSQL.  
Use Docker Compose for a full stack environment:

```bash
./gradlew build -x test
docker-compose up --build
