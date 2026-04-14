
export interface Credit {
    id: number;
    userId: number;
    userName: string;
    channel: string;
    credits: number;
    pricePerUnit: number;
    type: string;
    description: string;
    status: string;
    createdAt: string;
}

export interface CreditRequest {
    userId: number;
    channel: string;
    credits: number;
    pricePerUnit: number;
    type: string;
    description: string;
}
