

export interface SmsLog {
  id: number;
  mobileNumber: string;
  message: string;
  status: string;
  sentAt: string;
  errorMessage: string;
}