import { SmsLog } from "./sms.model";
import { WhatsappLog } from "./whatsapp.model";

export interface NoticeReportFilter {
    noticeName?: string;
    userId?: number;
    status?: string;
    fromDate?: Date;
    toDate?: Date;
}
export interface NoticeReportSummary {
    id: number;
    noticeName: string;
    originalFileName: string;
    zipFilePath: string;
    extractedFolderPath: string;
    sendSms: boolean;
    sendWhatsapp: boolean;
    status: string;
    processedAt: string;
    failureReason: string;
    createdAt: string;
    totalItems: number;
    pendingItems: number;
    processingItems: number;
    completedItems: number;
    failedItems: number;
}

export interface NoticeReportItem {
    id: number;
    agreementNumber: string;
    excelData: Record<string, unknown>;
    status: string;
    failureReason: string;
    processedAt: string;
    attachments: string;
}


export interface NoticeReportItemDetail {
    id: number;
    agreementNumber: string;
    status: string;
    failureReason: string;
    processedAt: string;
    excelData: Record<string, unknown>;
    attachments: string;
    smsLogs: SmsLog[];
    whatsappLogs: WhatsappLog[];
}

export interface NoticeReportDetail {
    summary: NoticeReportSummary;
    items: NoticeReportItem[];
}

export interface NoticeReportSmsDetail {
    summary: NoticeReportSummary;
    items: SmsLog[];
}

export interface NoticeReportWhatsappDetail {
    summary: NoticeReportSummary;
    items: WhatsappLog[];
}