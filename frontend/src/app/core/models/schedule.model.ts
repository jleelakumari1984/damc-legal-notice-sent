
export interface SendSampleRequest {
  processSno: number;
  mobileNumber: string;
  sendSms: boolean;
  sendWhatsapp: boolean;
  rowData: Record<string, unknown>;
}


