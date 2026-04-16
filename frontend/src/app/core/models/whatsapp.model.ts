export interface WhatsappLog {
  id: number;
  sendTo: string;
  message: string;
  sendStatus: number;
  sendAt: Date;
  ackId: string;
  receivedStatus: string;
  errorMessage: string;

}

export interface WhatsappLogResponse {
  items: WhatsappLog[];
}

export interface WhatsappLogRequest  {
  status: string;
}
 