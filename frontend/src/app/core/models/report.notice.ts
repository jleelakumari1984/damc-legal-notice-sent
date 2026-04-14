import { SmsLog } from "./sms.model";
import { WhatsappLog } from "./whatsapp.model";


export interface NoticeItemDetail {
    id: number;
    agreementNumber: string;
    status: string;
    failureReason: string;
    processedAt: string;
    excelData: Record<string, unknown>;
    attachements: string;
    smsLogs: SmsLog[];
    whatsappLogs: WhatsappLog[];
}

export interface NoticeReportDetail {
    id: number;
    processSno: number;
    originalFileName: string;
    sendSms: boolean;
    sendWhatsapp: boolean;
    status: string;
    createdAt: string;
    processedAt: string;
    failureReason: string;
    items: NoticeReportItem[];
}
export interface NoticeReportItem {
    id: number;
    agreementNumber: string;
    excelData: Record<string, unknown>;
    status: string;
    failureReason: string;
    processedAt: string;
    attachements: string;
}


export interface NoticeReportSummary {
    id: number;
    processName: number;
    originalFileName: string;
    zipFilePath: string;
    extractedFolderPath: string;
    sendSms: boolean;
    sendWhatsapp: boolean;
    status: string;
    processedAt: string;
    failureReason: string;
    createdAt: string;
}

export interface NoticeReportItem {
    id: number;
    agreementNumber: string;
    excelData: Record<string, unknown>;
    status: string;
    failureReason: string;
    processedAt: string;
    attachements: string;
}



export interface NoticeReportItemDetail {
    id: number;
    agreementNumber: string;
    status: string;
    failureReason: string;
    processedAt: string;
    excelData: Record<string, unknown>;
    attachements: string;
    smsLogs: SmsLog[];
    whatsappLogs: WhatsappLog[];
}

export interface NoticeReportDetail {
    summary: NoticeReportSummary;
    items: NoticeReportItem[];
}
