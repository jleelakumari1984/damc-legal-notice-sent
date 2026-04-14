import { PaginatedRequest } from "./datatable.model";


export interface SmsLog {
  id: number;
  sendTo: string;
  message: string;
  sendStatus: number;
  sendAt: Date;
  ackId: string;
  receivedStatus: string;
  receivedAt: Date;
  errorMessage: string;

}

export interface SmsLogResponse {
  items: SmsLog[];
}

export interface SmsLogRequest extends PaginatedRequest {
  status: string;
}