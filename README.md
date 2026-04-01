# DAMC Legal Notice Sender

Production-ready web application scaffold for legal notice scheduling and delivery.

## Stack
- Backend: Java 17, Spring Boot 3, Spring Security (JWT), Spring Data JPA (Hibernate), MySQL 8
- Frontend: Angular 17 (TypeScript), Bootstrap 5, jQuery, DataTables, Bootstrap Datepicker
- Database: MySQL 8

## Repository Ignore Setup
- Backend ignore file: `backend/.gitignore`
	- Ignores Maven target output, IDE metadata, logs, local env files, and generated runtime upload data.
- Frontend ignore file: `frontend/.gitignore`
	- Ignores node_modules, Angular/dist output, logs, IDE metadata, env files, and coverage artifacts.

## Frontend Layouts
- Non-authenticated layout: `NonAuthLayoutComponent`
	- Hosts public routes (currently login route).
- Authenticated layout: `AuthLayoutComponent`
	- Hosts protected routes behind `AuthGuard`.
	- Provides common top navigation and logout action.

### Route Structure
- Public shell:
	- `/login`
- Protected shell:
	- `/send-notices`

## Functional Flow
1. User logs in with credentials from `login_details`.
2. User opens Send Notices page and selects:
	- Notice Type
	- ZIP file upload (must contain excel + PDFs)
	- Channel options (SMS, WhatsApp)
3. Backend saves zip and extracts it.
4. Backend validates:
	- Excel exists in zip
	- Required columns exist
	- For each row, expected PDF exists
5. Backend stores schedule and per-agreement items in:
	- `scheduled_notices`
	- `scheduled_notice_items`
6. Cron job reads pending schedules and sends one-by-one through configured SMS/WhatsApp services.

## Existing Master Tables Used
- `login_details`
- `master_process_template_details`
- `master_process_sms_config_details`
- `master_process_whatsapp_config_details`

## New Tables
Created automatically from backend startup SQL:
- `scheduled_notices`
- `scheduled_notice_items`

## Backend Setup
1. Go to backend folder:
	- `cd backend`
2. Copy external config template and update values:
	- `copy ..\\config\\application.properties.example ..\\config\\application.properties`
3. Configure DB and JWT in `../config/application.properties`.
4. Ensure MySQL has your existing master tables and login data.
5. Run backend with external properties (not packaged in jar):
	- `mvn spring-boot:run -Dspring-boot.run.arguments="--spring.config.location=file:../config/application.properties"`
6. API base URL:
	- `http://localhost:8080`

### Key APIs
- `POST /api/auth/login`
- `GET /api/notices/types`
- `GET /api/notices`
- `POST /api/notices/schedule` (multipart)

## Frontend Setup
1. Go to frontend folder:
	- `cd frontend`
2. Install dependencies:
	- `npm install`
3. Run app:
	- `npm start`
4. Open:
	- `http://localhost:4200`

## Excel Format Required
Excel first row must include these column headers:
- `AgreementNumber`
- `CustomerName`
- `MobileSms`
- `MobileWhatsapp`
- `PdfFileName`

## Important Notes for Production
- Keep secrets only in external `config/application.properties` (or a secure vault) and never inside project resources.
- Replace SMS/WhatsApp sender stubs with provider API integration.
- Add retries, DLQ style persistence, and provider callbacks if needed.
- Use hashed passwords (BCrypt) in `login_details.password`.
- Move file storage to secure shared/object storage when scaling.

## External Configuration
- Active runtime file should be outside backend module:
	- `config/application.properties`
- This file is intentionally ignored by git and not bundled in the backend jar.