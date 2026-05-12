# upload-api — Backend API Contract

Short reference for frontend integration. Base URL: `http://localhost:8080`

---

## Environment variables

Copy `.env.example` to `.env` and fill in values before running.

| Variable | Default | Notes |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/upload_db?...` | MySQL connection string |
| `DB_USERNAME` | `upload_user` | |
| `DB_PASSWORD` | *(none)* | Required — no default |
| `JWT_SECRET` | dev placeholder | Use a long random string in production |
| `JWT_EXPIRATION_MS` | `86400000` | 24 hours |
| `S3_BUCKET_NAME` | `prints-jogos` | |
| `S3_REGION` | `sa-east-1` | |

---

## Running tests

```bash
cd upload-api
mvn test       # runs all 17 tests against H2 in-memory DB (no MySQL needed)
mvn compile    # compiles production sources
```

---

## Authentication flow

1. **Signup** — `POST /auth/signup` with `{nome, email, senha}` → returns user object
2. **Login** — `POST /auth/login` with `{email, senha}` → returns a raw JWT string
3. **Authenticated requests** — include the token in every request header:
   ```
   Authorization: Bearer <token>
   ```

---

## Error response shape

All errors return JSON in this shape:

```json
{
  "timestamp": "2024-01-01T12:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Email ou senha inválidos",
  "path": "/auth/login"
}
```

Common status codes:

| Code | Meaning |
|---|---|
| 400 | Bad request / validation failed |
| 401 | Not authenticated (missing/expired token, or bad credentials) |
| 403 | Authenticated but not allowed (wrong owner, not admin) |
| 404 | Resource not found |
| 413 | File too large (limit: 10 MB) |
| 500 | Internal server error |

---

## Endpoints

### Auth — `/auth/**` (public)

| Method | Path | Body | Response |
|---|---|---|---|
| POST | `/auth/signup` | `{nome, email*, senha*}` | `UserResponseDTO` |
| POST | `/auth/login` | `{email, senha}` | JWT string |
| GET | `/auth/teste-token` | — | `"Token OK, userId = N"` |

`*` = required field. Signup returns 400 if email or senha are blank.

`UserResponseDTO`:
```json
{ "id": 1, "nome": "Daniel", "email": "a@b.com", "role": "USER" }
```

---

### Prints — `/prints/**`

GET list and GET by user are public. All mutations require a valid JWT.

| Method | Path | Auth | Notes |
|---|---|---|---|
| GET | `/prints` | no | List all prints |
| GET | `/prints/{id}` | yes | Returns 403 if not owner |
| GET | `/prints/user/{userId}` | yes | Returns 403 if not own profile and not admin |
| POST | `/prints/upload` | yes | `multipart/form-data`: `file` (image), `game`, `description` |
| PUT | `/prints/{id}` | yes | `{game, description}` — owner or admin |
| PATCH | `/prints/{id}/description` | yes | `?newDescription=...` — owner or admin |
| DELETE | `/prints/{id}` | yes | owner or admin |
| DELETE | `/prints/all` | yes (admin) | 403 if not admin |

`PrintResponseDTO`:
```json
{
  "id": 1,
  "filename": "photo.jpg",
  "game": "Elden Ring",
  "description": "First playthrough",
  "url": "https://prints-jogos.s3.sa-east-1.amazonaws.com/<uuid>_photo.jpg",
  "uploadDate": "2024-01-01T12:00:00",
  "username": "Daniel"
}
```

Upload rules:
- Content-Type must be `image/*` (jpeg, png, gif, webp, etc.)
- Max file size: 10 MB
- Filename is sanitized and prefixed with a UUID to prevent overwrites

---

### Upload — `/api/upload` (direct S3 upload)

| Method | Path | Auth | Notes |
|---|---|---|---|
| POST | `/api/upload` | yes | `multipart/form-data`: `file` — returns URL string |

Same upload rules as `/prints/upload`. Returns the full S3 URL as plain text.

---

### Users — `/user/**` (authenticated)

| Method | Path | Auth | Notes |
|---|---|---|---|
| GET | `/user` | yes (admin) | List all users |
| GET | `/user/{id}` | yes | Own profile or admin |
| POST | `/user` | yes | Create user (admin sets role) |
| PATCH | `/user/{id}` | yes | Update own profile or admin |
| DELETE | `/user/{id}` | yes | Own account or admin |
