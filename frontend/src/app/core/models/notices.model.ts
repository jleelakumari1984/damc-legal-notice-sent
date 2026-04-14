import { ProcessExcelMapping } from "./excel.model";

export interface Notice {
    id: number;
    title: string;
    processSno: number;
    processName: string;
    status: string;
    sendSms: boolean;
    sendWhatsapp: boolean;
    totalItems: number;
    successCount: number;
    failedCount: number;
    pendingCount: number;
    createdAt: string;
    processedAt: string;
}

export interface NoticeRequest {
    title: string;
    processSno: number;
    sendSms: boolean;
    sendWhatsapp: boolean;
}

export interface NoticeType {
    id: number;
    name: string;
    excelMap: ProcessExcelMapping[];
}

export interface NoticeTypeRequest {
    name: string;
}


export interface NoticeExcelMappingResponse {
    id: number;
    processId: number;
    excelFieldName: string;
    dbFieldName: string;
    isKey: number;
    isMobile: number;
    isMandatory: number;
    isAttachment: number;
    createdAt: string;
}

export interface NoticeExcelMappingRequest {
    processId: number;
    excelFieldName: string;
    dbFieldName: string;
    isKey: number;
    isMobile: number;
    isMandatory: number;
    isAttachment: number;
}
