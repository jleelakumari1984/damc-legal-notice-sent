
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
