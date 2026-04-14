export interface ProcessExcelMapping {
    id: number;
    excelFieldName: string;
    isMandatory: number;
    isAttachment: number;
    isKey: number;
}


export interface ValidationRow {
    agreementNumber: string;
    excelData: string;
}

export interface NoticeFileData {
    columnNames: string[];
    rows: ValidationRow[];
}

export interface ValidationResponse {
    scheduleId: number;
    originalZipFile: string;
    extractedFolder: string;
    status: string;
    fileData: NoticeFileData;
}

export interface ExcelPreviewRow {
    data: Record<string, unknown>;
}

export interface ExcelPreview {
    columnNames: string[];
    rows: ExcelPreviewRow[];
}
