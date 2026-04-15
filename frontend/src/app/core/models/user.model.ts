import { PaginatedRequest } from "./datatable.model";

export interface User {
    id: number;
    displayName: string;
    loginName: string;
    userEmail: string;
    userMobileSms: string;
    userMobileWhatsapp: string;
    accessLevel: number;
    enabled: boolean;
    createdAt: Date;
    lastLoginDate: Date;
    smsCredits: number;
    whatsappCredits: number;
    mailCredits: number;
}

export interface UserRequest {
    displayName: string;
    loginName: string;
    password?: string;
    userEmail: string;
    userMobileSms: string;
    userMobileWhatsapp: string;
    accessLevel: number;
    enabled: boolean;
}

export interface UserPaginatedRequest extends PaginatedRequest {

}