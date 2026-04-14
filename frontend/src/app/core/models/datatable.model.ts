export interface PaginatedRequest {
    dtDraw: number;
    dtStart: number;
    dtLength: number;
    sortColumn: string;
    sortDirection: string;
}
export interface PaginatedResponse<T> {
    draw: number;
    recordsTotal: number;
    recordsFiltered: number;
    data: T;
}