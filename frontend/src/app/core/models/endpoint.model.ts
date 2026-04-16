
export interface SmsEndpoint {
    id: number;
    name: string;
    host: string;
    port: number;
    username: string;
    apiPath: string;
    status: number;
    createdAt: string;
}

export interface SmsEndpointRequest {
    name: string;
    host: string;
    port: number;
    username: string;
    password?: string;
    apiPath: string;
    status: number;
}


export interface WhatsappEndpoint {
    id: number;
    name: string;
    host: string;
    port: number;
    username: string;
    apiPath: string;
    status: number;
    createdAt: string;
}

export interface WhatsappEndpointRequest {
    name: string;
    host: string;
    port: number;
    username: string;
    password?: string;
    apiPath: string;
    status: number;
}

// User SMS Credential
export interface UserSmsCredential {
    id: number;
    userId: number;
    url: string;
    userName: string;
    live: boolean;
    testMobileNumber: string | null;
    createdAt: string;
    updatedAt: string;
}

export interface UserSmsCredentialRequest {
    url: string;
    userName: string;
    password?: string;
    live: boolean;
    testMobileNumber?: string | null;
}

// User WhatsApp Credential
export interface UserWhatsAppCredential {
    id: number;
    userId: number;
    url: string;
    attachmentDownloadUrl: string | null;
    live: boolean;
    testMobileNumber: string | null;
    createdAt: string;
    updatedAt: string;
}

export interface UserWhatsAppCredentialRequest {
    url: string;
    accessToken: string;
    attachmentDownloadUrl?: string | null;
    live: boolean;
    testMobileNumber?: string | null;
}
