import { Injectable } from '@angular/core';

declare const $: any;

@Injectable({ providedIn: 'root' })
export class DatatableHelper {
  init(tableId: string): void {
    const table = $(tableId);
    if ($.fn.dataTable.isDataTable(tableId)) {
      table.DataTable().destroy();
    }
    table.DataTable({
      paging: true,
      searching: true,
      ordering: true,
      responsive: true,
      pageLength: 10
    });
  }
}
