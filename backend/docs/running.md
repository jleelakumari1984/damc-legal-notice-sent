# Running the Application

## Prerequisites

| Requirement | Version |
|---|---|
| Java | 21+ |
| Maven | 3.9+ |
| MySQL | 8.0+ |

---

## 1. Database Setup

1. Create the database and run the schema:
   ```sql
   CREATE DATABASE legal_council_notices CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. Import the schema:
   ```bash
   mysql -u root -p legal_council_notices < schema_live.sql
   ```
3. Apply any pending update statements:
   ```bash
   mysql -u root -p legal_council_notices < update_statements.sql
   ```

---

## 2. Configure the Application

Copy `application.properties.example` to `api/src/main/resources/application.properties` and fill in the required values:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/legal_council_notices?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata
spring.datasource.username=<db-user>
spring.datasource.password=<db-password>

# JWT — must be at least 32 characters
app.jwt.secret=<your-secret-key>

# Mail (set live=true for production)
app.mail.live=false
spring.mail.username=<gmail-address>
spring.mail.password=<gmail-app-password>

# WhatsApp (set live=true for production)
app.whats-app.live=false
app.whats-app.access-token=<token>

# SMS (set live=true for production)
app.sms.live=false
app.sms.user-name=<sms-username>
app.sms.password=<sms-password>
```

---

## 3. Build

From the project root (`backend/`), build all three modules in one command:

```bash
mvn clean install
```

This builds `domain` → `worker` → `api` in dependency order.  
The executable jar is produced at:

```
api/target/api-1.0.0.jar
```

---

## 4. Run

```bash
java -jar api/target/api-1.0.0.jar
```

Or with explicit JVM options (recommended for production):

```bash
java -Xms512m -Xmx4g -XX:+UseG1GC -jar api/target/api-1.0.0.jar
```

The server starts on **port 8080** by default.  
Override the port at runtime:

```bash
java -jar api/target/api-1.0.0.jar --server.port=9090
```

---

## 5. API Documentation (Swagger UI)

Once running, open:

```
http://localhost:8080/swagger-ui/index.html
```

---

## 6. Running Tests

```bash
# All tests
mvn test

# A specific module only
mvn test -pl worker
```

---

## Module Overview

| Module | Artifact | Purpose |
|---|---|---|
| `domain` | `domain-1.0.0.jar` | Entities, enums, repositories, DAOs, error types |
| `worker` | `worker-1.0.0.jar` | Schedulers, notification/schedule services, Excel/ZIP/template processing |
| `api` | `api-1.0.0.jar` | Spring Boot executable — REST controllers, DTOs, security, user services |
