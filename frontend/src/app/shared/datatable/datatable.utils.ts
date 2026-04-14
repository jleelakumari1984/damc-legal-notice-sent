import { DatePipe } from "@angular/common";
import { HttpErrorResponse } from "@angular/common/http";
import { ProcessingStatus } from "../../core/models/schedule.model";

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

  try {
    const date = new Date(d);
    if (isNaN(date.getTime())) {
      return '';
    }
    const datePipe = new DatePipe('en-US');
    const dateFormat = datePipe.transform(date, 'dd/MM/yyyy hh:mm a');
    return dateFormat || '';
  } catch (error) {
    console.error('Error formatting date:', error);
    return '';
  }
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
  switch (status?.toUpperCase() as ProcessingStatus) {
    case ProcessingStatus.EXCELUPLOADED: return 'bg-secondary';
    case ProcessingStatus.EXCELPROCESSING: return 'bg-info text-dark';
    case ProcessingStatus.EXCELCOMPLETED: return 'bg-success';
    case ProcessingStatus.EXCELFAILED: return 'bg-danger';
    case ProcessingStatus.PENDING: return 'bg-warning text-dark';
    case ProcessingStatus.PROCESSING: return 'bg-info text-dark';
    case ProcessingStatus.COMPLETED: return 'bg-success';
    case ProcessingStatus.FAILED: return 'bg-danger';
    case ProcessingStatus.START: return 'bg-primary';
    case ProcessingStatus.STOP: return 'bg-secondary';
    default: return 'bg-secondary';
  }
}

/**
 * Global handler for DataTables server-side AJAX errors.
 * Uses pipe(catchError(...)) rather than subscribe error handler to catch
 * both HTTP errors and exceptions thrown inside the next callback.
 */
export function handleAjaxError(
  error: HttpErrorResponse,
  draw: number,
  callback: (data: object) => void,
  onError: (msg: string) => void
): void {
  let message: string;
  switch (error.status) {
    case 400: message = 'Bad request. Please check your input.'; break;
    case 401: message = 'Unauthorized. Please log in again.'; break;
    case 403: message = 'Access denied.'; break;
    case 404: message = 'Requested data not found.'; break;
    case 500: message = 'Internal server error. Please try again later.'; break;
    default: message = error.message || 'Failed to load data. Please try again.'; break;
  }
  onError(message);
  callback({ draw, recordsTotal: 0, recordsFiltered: 0, data: [] });
}

/** Standard DataTables base options shared by all tables. */
export const BASE_DT_OPTIONS = {
  processing: true,
  paging: true,
  searching: true,
  ordering: true,
  responsive: true,
  pageLength: 25,
  lengthMenu: [[25, 100, 1000], [25, 100, 1000]],
};
