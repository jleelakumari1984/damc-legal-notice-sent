import { PaginatedRequest } from "./datatable.model";
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
    excelMapCount: number;
    smsMapCount: number;
    whatsappMapCount: number;
    mailMapCount: number;
}

export interface NoticeTypeRequest extends PaginatedRequest {
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

export interface SmsTemplate {
    id: number;
    processId: number;
    peid: string;
    senderId: string;
    routeId: string;
    userTemplateContent: string;
    templateContent: string;
    templateId: string;
    channel: string;
    dcs: number;
    flashSms: number;
    status: number;
    approveStatus: number;
    rejectionReason?: string;
    createdAt: string;
}

export interface SmsUserTemplateRequest {
    processId: number;
    userTemplateContent: string;
    status: number;
}

export interface SmsTemplateRequest extends SmsUserTemplateRequest {
    peid: string;
    senderId: string;
    routeId: string;
    templateContent: string;
    templateId: string;
    channel: string;
    dcs: number;
    flashSms: number;
}
export interface SmsPendingTemplateRequest extends PaginatedRequest {
}
export interface SmsPendingTemplate {
    id: number;
    processName: string;
    userName: string;
    userTemplateContent: string;
    createdAt: string;
}
export interface WhatsappPendingTemplateRequest extends PaginatedRequest {
}
export interface WhatsappPendingTemplate {
    id: number;
    processName: string;
    userName: string;
    userTemplateContent: string;
    createdAt: string;
}
export interface WhatsappTemplate {
    id: number;
    processId: number;
    templateName: string;
    templatePath: string;
    userTemplateContent: string;
    templateContent: string;
    templateLang: string;
    status: number;
    approveStatus: number;
    rejectionReason?: string;
    createdAt: string;
}

export interface WhatsappUserTemplateRequest {
    processId: number;
    userTemplateContent: string;
    status: number;
}

export interface WhatsappTemplateRequest extends WhatsappUserTemplateRequest {
    templateName: string;
    templateContent: string;
    templateLang: string;
}

export enum TemplateApprovedStatus {
    PENDING = 0,
    APPROVED = 1,
    REJECT = 3
}