export interface PaginatedRequest<T> {
    allData?: boolean;
    dtDraw: number;
    dtStart: number;
    dtLength: number;
    sortColumn: string;
    sortDirection: string;
    filter: T;
}
export interface PaginatedResponse<T> {
    draw: number;
    recordsTotal: number;
    recordsFiltered: number;
    data: T;
}