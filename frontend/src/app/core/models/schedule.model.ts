
export interface SendSampleRequest {
  processSno: number;
  mobileNumber: string;
  sendSms: boolean;
  sendWhatsapp: boolean;
  rowData: Record<string, unknown>;
}


export enum ProcessingStatus {
  EXCELUPLOADED   = 'EXCELUPLOADED',
  EXCELPROCESSING = 'EXCELPROCESSING',
  EXCELCOMPLETED  = 'EXCELCOMPLETED',
  EXCELFAILED     = 'EXCELFAILED',
  PENDING         = 'PENDING',
  PROCESSING      = 'PROCESSING',
  COMPLETED       = 'COMPLETED',
  FAILED          = 'FAILED',
  START           = 'START',
  STOP            = 'STOP'
}