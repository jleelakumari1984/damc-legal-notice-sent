export interface NoticeReport {
    id: number;
    title: string;
    noticeSno: number;
    noticeName: string;
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
    name: string;
    description: string;
}

export interface NoticeType {
    id: number;
    name: string;
    description: string;
    createdUserName: string;
    excelMapCount: number;
    smsActiveCount: number;
    smsInactiveCount: number;
    whatsappActiveCount: number;
    whatsappInactiveCount: number;
    mailActiveCount: number;
    mailInactiveCount: number;

}


export interface NoticeExcelMappingResponse {
    id: number;
    noticeId: number;
    excelFieldName: string;
    dbFieldName: string;
    isAgreement: number;
    isCustomerName: number;
    isMobile: number;
    isMandatory: number;
    isAttachment: number;
    createdAt: string;
}

export interface NoticeExcelMappingRequest {
    noticeId: number;
    excelFieldName: string;
    isAgreement: number;
    isCustomerName: number;
     isMobile: number;
    isMandatory: number;
    isAttachment: number;
    isCustomer: number;
}

export interface SmsTemplate {
    id: number;
    noticeId: number;
    peid: string;
    senderId: string;
    routeId: string;
    userTemplateContent: string;
    templateContent: string;
    messageLength: number;
    numberOfMessage: number;
    templateId: string;
    channel: string;
    dcs: number;
    flashSms: number;
    status: number;
    approveStatus: number;
    rejectionReason?: string;
    createdAt: Date;
    ownTemplate: boolean;
}

export interface SmsUserTemplateRequest {
    noticeId: number;
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
export interface SmsPendingTemplateFilter {
}
export interface SmsPendingTemplateResponse {
    id: number;
    noticeId: number;
    noticeName: string;
    userName: string;
    userTemplateContent: string;
    templateContent: string;
    messageLength: number;
    numberOfMessage: number;
    createdAt: Date;
}
export interface WhatsappPendingTemplateFilter {
}
export interface WhatsappPendingTemplateResponse {
    id: number;
    noticeId: number;
    noticeName: string;
    userName: string;
    userTemplateContent: string;
    templateContent: string;
    messageLength: number;
    numberOfMessage: number;
    createdAt: Date;
}
export interface WhatsappTemplate {
    id: number;
    noticeId: number;
    templateName: string;
    templatePath: string;
    userTemplateContent: string;
    templateContent: string;
    messageLength: number;
    numberOfMessage: number;
    templateLang: string;
    status: number;
    approveStatus: number;
    rejectionReason?: string;
    createdAt: Date;
    ownTemplate: boolean;
}

export interface WhatsappUserTemplateRequest {
    noticeId: number;
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



export interface SmsApproveTemplateRequest {
    peid: string;
    senderId: string;
    routeId: string;
    templateContent: string;
    templateId: string;
    channel: string;
    dcs: number;
    flashSms: number;
}

export interface SmsRejectTemplateRequest {
    rejectionReason: string;
}


export interface WhatsappApproveTemplateRequest {
    templateName: string;
    templateContent: string;
    templateLang: string;
}

export interface WhatsappRejectTemplateRequest {
    rejectionReason: string;
}