# Process Change Document — System Design Alignment

## Current State vs. Required State

---

## 1. Role-Based Access Control (RBAC)

### Current State
- `UserAccessLevelEnum`: `SUPER_ADMIN(1)`, `ADMIN(2)`, `USER(3)` — **already exists**
- `@RequiresAccess(level)` annotation + `AccessCheckAspect` — **already exists**
- `LoginUserDao.isSuperAdmin()` only checks for level 1 — **Admin (level 2) is not distinguished**
- Controllers use `isSuperAdmin()` to branch between admin and user service paths

### Required Change
| # | Change | File(s) Affected |
|---|--------|-----------------|
| R1 | Add `isAdmin()` to `LoginUserDao` — returns `true` when `accessLevel <= 2` (SuperAdmin or Admin) | `LoginUserDao.java` |
| R2 | Replace all `isSuperAdmin()` branching in controllers with `isAdmin()` so both SuperAdmin and Admin go through the admin path | `NoticeSmsMappingController`, `NoticeWhatsappMappingController` |
| R3 | Annotate all admin-only endpoints with `@RequiresAccess(level = UserAccessLevelEnum.ADMIN)` instead of hardcoding in service logic | Both mapping controllers, `CreditManagementController` |

---

## 2. Template Approval Workflow

### Current State
- SMS and WhatsApp templates have a `status` column (integer) — **already exists** in both entities
- There is **no approval endpoint** — status can only be changed on create/update
- When approving, admin should also set the **final sending endpoint info** (e.g., `peid`, `senderId`, `routeId`, `templateId`, `channel` for SMS; `templateName`, `templateLang` for WhatsApp)
- Currently, these fields are set at creation time by admin — there is **no separate approve step**

### Required Change
| # | Change | File(s) Affected |
|---|--------|-----------------|
| A1 | Define `status` semantics as an enum:   `ACTIVE(1)`, `INACTIVE(0)`  | New `enums/TemplateStatus.java` |
| A1 | Define `approveStatus` semantics as an enum: `PENDING(0)`,  `APPROVED(1)`, `REJECTED(3)` | New `enums/TemplateApproveStatus.java` |
| A2 | Add `approvedBy` and `approvedAt` columns to both `master_process_sms_config_details` and `master_process_whatsapp_config_details` | `MasterProcessSmsConfigDetailEntity`, `MasterProcessWhatsappConfigDetailEntity`, `update_statements.sql` |
| A3 | When a **User** creates a template → `status = PENDING(0)`, endpoint fields are blank | `NoticeSmsMappingUserServiceImpl`, `NoticeWhatsappMappingUserServiceImpl` |
| A4 | When **Admin/SuperAdmin** creates a template → `status = ACTIVE(1)`, endpoint fields set immediately (current behavior, keep) | No change needed |
| A5 | New `PATCH /api/notice-mappings/sms/approve/{id}` endpoint — Admin only, accepts endpoint fields + sets `status = ACTIVE`, records `approvedBy`, `approvedAt` | New `NoticeSmsApproveDto`, updated `NoticeSmsMappingAdminService` + controller |
| A6 | New `PATCH /api/notice-mappings/whatsapp/approve/{id}` endpoint — same pattern | New `NoticeWhatsappApproveDto`, updated `NoticeWhatsappMappingAdminService` + controller |
| A7 | New `PATCH /api/notice-mappings/sms/{id}/status` endpoint — Admin only, activate (`1`) or deactivate (`2`) a template | `NoticeSmsMappingAdminService` + controller |
| A8 | Same for WhatsApp | `NoticeWhatsappMappingAdminService` + controller |

---

## 3. Activate / Deactivate Templates (User-side)

### Current State
- Users can set `status` on create/update — **not controlled**, no workflow

### Required Change
| # | Change |
|---|--------|
| U1 | Users can **request** activate/deactivate (`PATCH /api/notice-mappings/sms/{id}/status`) but it requires Admin approval (sets back to `PENDING`) |
| U2 | Or simpler: Users cannot change status directly — only Admin can activate/deactivate. User create always → `PENDING` |

> **Decision needed**: Should the user be able to directly activate/deactivate their own templates, or must it always go through Admin approval?

---

## 4. Session Change (Impersonation)

### Current State
- `AuthController` has `POST /api/auth/login` only — **no session change endpoint**
- `LoginUserDao` holds the current session user via JWT

### Required Change
| # | Change | File(s) Affected |
|---|--------|-----------------|
| S1 | New `POST /api/auth/switch-session` endpoint — takes a `targetUserId`, requires `isAdmin()` OR a permission flag on the current user | New `SwitchSessionDto`, `AuthController`, `AuthService` |
| S2 | Add `canSwitchSession` boolean column to `login_details` table to control which regular users are permitted to switch | `LoginDetailEntity`, `update_statements.sql` |
| S3 | On switch, generate a new JWT token for the target user (impersonation token) so the client can call APIs as that user | `JwtUtil`, `AuthServiceImpl` |

---

## 5. SMS & WhatsApp Endpoint Credential Management (Admin UI)

### Current State
- SMS credentials (`url`, `userName`, `password`) are in `application.properties` / `SmsProperties`
- WhatsApp credentials (`url`, `accessToken`) are in `application.properties` / `WhatsAppProperties`
- **Stored in application config, not file system** — violates the security requirement

### Required Change
| # | Change | File(s) Affected |
|---|--------|-----------------|
| E1 | Move SMS credentials to a JSON/properties file on the file system (path configured via `LocationProperties`) — read at startup and on admin update | `SmsProperties`, `LocationProperties`, new `SmsCredentialService` |
| E2 | Same for WhatsApp credentials | `WhatsAppProperties`, new `WhatsAppCredentialService` |
| E3 | New `PUT /api/admin/sms-endpoint` endpoint — SuperAdmin/Admin only, writes new credentials to the file | New controller + service |
| E4 | New `PUT /api/admin/whatsapp-endpoint` endpoint — same | New controller + service |
| E5 | Credentials file must **never** be served via any API response — only write-only endpoints | Validation in service |

---

## 6. Admin `isAdmin()` Role Helper — Missing Check

### Current State
- `LoginUserDao.isSuperAdmin()` returns `true` only for level 1
- Admin (level 2) **falls into the user path** in the mapping controllers

### Required Change
This is covered by **R1** above but is critical — without it, Admins cannot approve templates or manage users.

---

## 7. No Breaking Changes — Existing Endpoints Preserved

All existing endpoints remain. Changes are additive (new endpoints, new fields, DB migration via `update_statements.sql`).

---

## Summary Table — New Files to Create

| File | Purpose |
|------|---------|
| `enums/TemplateStatus.java` | `PENDING`, `ACTIVE`, `INACTIVE`, `REJECTED` |
| `dto/notice/NoticeSmsApproveDto.java` | Approve payload with SMS endpoint fields |
| `dto/notice/NoticeWhatsappApproveDto.java` | Approve payload with WhatsApp endpoint fields |
| `dto/notice/TemplateStatusUpdateDto.java` | `{ status }` for activate/deactivate |
| `dto/user/SwitchSessionDto.java` | `{ targetUserId }` |
| `service/notice/NoticeSmsApprovalService.java` + impl | Approve, activate/deactivate SMS templates |
| `service/notice/NoticeWhatsappApprovalService.java` + impl | Same for WhatsApp |
| `service/admin/SmsCredentialService.java` + impl | Read/write SMS credentials file |
| `service/admin/WhatsAppCredentialService.java` + impl | Read/write WhatsApp credentials file |
| `controller/admin/EndpointCredentialController.java` | `PUT /api/admin/sms-endpoint` + whatsapp |

---

## DB Migration (`update_statements.sql`) — New Columns

```sql
-- Template approval tracking
ALTER TABLE master_process_sms_config_details
ADD COLUMN approve_status tinyint,
  ADD COLUMN approved_by BIGINT,
  ADD COLUMN approved_at DATETIME;

ALTER TABLE master_process_whatsapp_config_details
ADD COLUMN approve_status tinyint,
  ADD COLUMN approved_by BIGINT,
  ADD COLUMN approved_at DATETIME;

-- Session switch permission
ALTER TABLE login_details
  ADD COLUMN can_switch_session BOOLEAN DEFAULT FALSE;
```

---
DATETIME Fileds Need to Create LocalDateTime java type.
## Open Decisions

| # | Question | Default if not answered |
|---|----------|------------------------|
| OD1 | Should users be able to activate/deactivate their own templates directly, or must it always go through Admin approval? | Admin-only (U2) | yes theis is seperate filed for approval status
| OD2 | Should impersonation (session switch) produce a new JWT or reuse the current session with a header override? | New JWT (S3) | New JWT (S3)
| OD3 | Should rejected templates be permanently locked or allow the user to resubmit? | Allow resubmit (new `status = PENDING` on update) |Allow resubmit
