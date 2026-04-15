# Process Change Documentation

**Date:** April 15, 2026  
**Status:** Pending Approval

---

## 1. Current State Summary

| Area | Current Behaviour |
|---|---|
| Roles | Binary: `accessLevel === 1` = SuperAdmin, everything else = User |
| Route Guards | `AuthGuard` only checks login — no role-based route protection |
| Navigation | Users menu hidden for non-SuperAdmin; Credit, Notice, Send, Reports visible to all |
| Templates | User form saves with `userTemplateContent` only; SuperAdmin form saves full fields. No approval workflow. Status is `0/1` (Inactive/Active) |
| Template Approval | Not implemented |
| Session Change | Not implemented |
| SMS/WhatsApp Endpoint Mgmt | Not implemented (endpoint config is embedded in template forms) |

---

## 2. Role System Changes

### 2.1 Three Roles

| Role | `accessLevel` Value |
|---|---|
| Super Admin | `1` |
| Admin | `2` |
| User | `3` |

### 2.2 `AuthService` — New Methods

Add `isAdmin()` and `isUser()` helpers alongside the existing `isSuperAdmin()`:

```
isAdmin(): boolean        → accessLevel === 2
isUser(): boolean         → accessLevel === 3
isSuperOrAdmin(): boolean → accessLevel === 1 || accessLevel === 2
```

---

## 3. Route Guard Changes

### 3.1 New Guard — `RoleGuard`

A parameterized guard that accepts allowed `accessLevel[]`:

| Route | Allowed Roles |
|---|---|
| `/users` | SuperAdmin, Admin |
| `/credit` | SuperAdmin, Admin |
| `/sms-endpoints` *(new)* | SuperAdmin, Admin |
| `/whatsapp-endpoints` *(new)* | SuperAdmin, Admin |
| `/template-approvals` *(new)* | SuperAdmin, Admin |

All other routes (notice, send-notices, reports) remain accessible to all authenticated users.

---

## 4. Navigation Changes (`auth-layout.component.html`)

| Menu Item | Current Visibility | New Visibility |
|---|---|---|
| Notice(s) | All | All |
| Send Notice | All | All |
| Report(s) | All | All |
| Users | SuperAdmin only | SuperAdmin + Admin |
| Credit | All | SuperAdmin + Admin only |
| Template Approvals *(new)* | — | SuperAdmin + Admin |
| SMS Endpoints *(new)* | — | SuperAdmin + Admin |
| WhatsApp Endpoints *(new)* | — | SuperAdmin + Admin |
| Switch Session *(new)* | — | All roles |

---

## 5. Template Approval Workflow

This is the largest change.

### 5.1 Template Status Model

Current `status`: `0` = Inactive, `1` = Active

New `approveStatus` field added to `SmsTemplate` and `WhatsappTemplate`:

| Value | Meaning |
|---|---|
| `PENDING` | User submitted, awaiting admin review |
| `APPROVED` | Admin approved, endpoint details set, active for sending |
| `REJECTED` | Admin rejected |

The existing `status` (0/1) remains as an Activate/Deactivate toggle **after** approval.

### 5.2 User Flow (Create/Edit Template)

**Before:** User submits → template immediately active/inactive.

**After:**
1. User fills `userTemplateContent` and submits.
2. Template saved with `approveStatus = PENDING`, `status = 0`.
3. Table shows a "Pending Approval" badge on the user's template list.
4. User **cannot edit** a template once it is `APPROVED` — only Admin can.

### 5.3 Admin/SuperAdmin Approval Flow

**New page: `/template-approvals`**

- Lists all templates with `approveStatus = PENDING` across all users, grouped by SMS / WhatsApp tabs.
- Admin clicks **Approve** → a side-panel/modal opens showing:

  **For SMS approval** (Admin fills in endpoint details):
  - Sender ID *(required)*
  - Template ID *(required)*
  - PEID, Route ID, Channel, DCS, Flash SMS *(optional)*
  - Shows user's `userTemplateContent` as read-only reference

  **For WhatsApp approval** (Admin fills in endpoint details):
  - Template Name *(required)*
  - Template Language *(required)*
  - Template Path *(optional)*
  - Shows user's `userTemplateContent` as read-only reference

- Admin submits → `approveStatus` set to `APPROVED`, `status = 1`, endpoint fields saved.
- Admin can **Reject** with an optional reason → `approveStatus = REJECTED`.

### 5.4 Impact on Existing Template Components

| Component | Change |
|---|---|
| `SmsTemplateFormUserComponent` | On submit → API call sets `approveStatus = PENDING`. Show info message "Template submitted for approval." |
| `WhatsappTemplateFormUserComponent` | Same as above |
| `SmsTemplateComponent` (table) | User view adds `Approval Status` column with colour badges (Pending=yellow, Approved=green, Rejected=red). Remove edit button for APPROVED records. |
| `WhatsappTemplateComponent` (table) | Same as above |
| `SmsTemplateFormComponent` (admin) | Removed from template creation. Moved to the approval panel where admin fills endpoint details during approval. |
| `WhatsappTemplateFormComponent` (admin) | Same as above |

---

## 6. New Pages / Components to Create

### 6.1 Template Approvals Page — `/template-approvals`

- Module: `template-approvals.module.ts`
- Components:
  - `template-approvals.component` — tabbed view (SMS | WhatsApp)
  - `sms-approval-form.component` — modal for approving SMS templates
  - `whatsapp-approval-form.component` — modal for approving WhatsApp templates
- DataTable: server-side, filter by `approveStatus = PENDING` by default, with tab to see all.

### 6.2 Session Switch — Available in All Roles

- A **"Switch Session"** button in the top navbar.
- Opens a modal listing all users (from `/users` API) except the current user.
- On selection: calls a new API endpoint `/auth/switch-session?userId=X` which returns a new JWT token for that user's context.
- The original session token is saved in a separate `localStorage` key so user can **switch back**.
- Visible to all roles.

### 6.3 SMS Endpoint Management — `/sms-endpoints`

- Admin UI to configure SMS gateway credentials (host, port, username — not password in DB per security requirement).
- Password stored in server file system only — the form will send the password once for file-system storage; it will never be returned to the frontend in subsequent reads (masked `****`).

### 6.4 WhatsApp Endpoint Management — `/whatsapp-endpoints`

- Same pattern as SMS Endpoints above.

---

## 7. Model Changes

### 7.1 `SmsTemplate` Interface

```typescript
// Add to existing interface
approveStatus: 'PENDING' | 'APPROVED' | 'REJECTED';
rejectionReason?: string;
```

### 7.2 `WhatsappTemplate` Interface

```typescript
// Add to existing interface
approveStatus: 'PENDING' | 'APPROVED' | 'REJECTED';
rejectionReason?: string;
```

### 7.3 `UserResponse` Interface (in `AuthService`)

```typescript
// Change
accessLevel: 1 | 2 | 3;
// Add (optional convenience)
role?: 'SUPER_ADMIN' | 'ADMIN' | 'USER';
```

---

## 8. Security Changes

| Item | Change |
|---|---|
| Credit page route | Guard with `RoleGuard([1, 2])` |
| Users page route | Guard with `RoleGuard([1, 2])` |
| Template approval route | Guard with `RoleGuard([1, 2])` |
| Endpoint config routes | Guard with `RoleGuard([1, 2])` |
| SMS/WhatsApp endpoint passwords | Never stored in DB. Frontend sends once, backend writes to file system. Never returned to client. |

---

## 9. Summary of Files Affected / Created

| Action | File |
|---|---|
| Modify | `src/app/core/services/auth/auth.service.ts` — add `isAdmin()`, `isUser()`, `isSuperOrAdmin()` |
| Create | `src/app/core/guards/role.guard.ts` |
| Modify | `src/app/app-routing.module.ts` — apply `RoleGuard` to sensitive routes, add new routes |
| Modify | `src/app/layouts/auth-layout/auth-layout.component.html` — updated nav visibility |
| Modify | `src/app/core/models/notices.model.ts` — add `approveStatus`, `rejectionReason` |
| Modify | `src/app/shared/datatable/sms-templates-datatable.ts` — add approveStatus column |
| Modify | `src/app/shared/datatable/whatsapp-templates-datatable.ts` — add approveStatus column |
| Modify | `src/app/pages/notice/sms-template/sms-template.component.ts/.html` — add approval status column |
| Modify | `src/app/pages/notice/whatsapp-template/whatsapp-template.component.ts/.html` — add approval status column |
| Modify | `src/app/pages/notice/sms-template/sms-template-form-user/` — show "pending" feedback after submit |
| Modify | `src/app/pages/notice/whatsapp-template/whatsapp-template-form-user/` — show "pending" feedback after submit |
| Modify | `src/app/core/services/notice-template.service.ts` — add approval API calls |
| Create | `src/app/pages/template-approvals/` — module + components (tabs, SMS approval form, WhatsApp approval form) |
| Create | `src/app/pages/sms-endpoints/` — module + component |
| Create | `src/app/pages/whatsapp-endpoints/` — module + component |
| Create | `src/app/shared/switch-session/` — modal component (used in auth-layout) |

---

## 10. Implementation Order (Suggested)

1. Role system — `AuthService` + `RoleGuard`
2. Route guard application + navigation updates
3. Model changes (`notices.model.ts`)
4. Datatable column updates (approveStatus badges)
5. User template form feedback (PENDING state)
6. Template Approvals page (approval + rejection flow)
7. Session Switch modal
8. SMS Endpoint Management page
9. WhatsApp Endpoint Management page
