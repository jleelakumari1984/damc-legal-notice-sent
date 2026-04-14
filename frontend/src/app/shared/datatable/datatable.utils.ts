/** Escape HTML special characters to prevent XSS in rendered cell content. */
export function esc(v: unknown): string {
  return String(v ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

/** Format ISO date string as "dd MMM yyyy" (e.g. "11 Apr 2026"). */
export function formatDate(d: string): string {
  if (!d) return '-';
  const dt = new Date(d);
  const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
                  'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
  return `${String(dt.getDate()).padStart(2, '0')} ${months[dt.getMonth()]} ${dt.getFullYear()}`;
}

/** Format ISO date string as "dd MMM yyyy, hh:mm AM/PM" (e.g. "11 Apr 2026, 02:30 PM"). */
export function formatDateTime(d: string): string {
  if (!d) return '-';
  const dt = new Date(d);
  const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
                  'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
  const h = dt.getHours();
  const ampm = h >= 12 ? 'PM' : 'AM';
  const h12 = String((h % 12) || 12).padStart(2, '0');
  const min = String(dt.getMinutes()).padStart(2, '0');
  return `${String(dt.getDate()).padStart(2, '0')} ${months[dt.getMonth()]} ${dt.getFullYear()}, ${h12}:${min} ${ampm}`;
}

/** Returns a jQuery beforeSend callback that injects the JWT auth header. */
export function authBeforeSend(): (xhr: XMLHttpRequest) => void {
  return (xhr: XMLHttpRequest) => {
    const token = localStorage.getItem('damc_token');
    if (token) {
      xhr.setRequestHeader('Authorization', `Bearer ${token}`);
    }
  };
}

/** Returns a Bootstrap badge class for a ProcessingStatus enum value. */
export function statusBadgeClass(status: string): string {
  switch (status?.toUpperCase()) {
    case 'EXCELUPLOADED':   return 'bg-secondary';
    case 'EXCELPROCESSING': return 'bg-info text-dark';
    case 'EXCELCOMPLETED':  return 'bg-success';
    case 'EXCELFAILED':     return 'bg-danger';
    case 'PENDING':         return 'bg-warning text-dark';
    case 'PROCESSING':      return 'bg-info text-dark';
    case 'COMPLETED':       return 'bg-success';
    case 'FAILED':          return 'bg-danger';
    case 'START':           return 'bg-primary';
    case 'STOP':            return 'bg-secondary';
    default:                return 'bg-secondary';
  }
}

/** Standard DataTables base options shared by all tables. */
export const BASE_DT_OPTIONS = {
  processing: true,
  paging: true,
  searching: true,
  ordering: true,
  responsive: true,
  pageLength: 10
};
