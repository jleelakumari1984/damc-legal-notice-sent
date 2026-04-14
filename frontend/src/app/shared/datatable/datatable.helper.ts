import { Injectable } from '@angular/core';
import { DataTable } from './base-datatable';

declare const $: any;

@Injectable({ providedIn: 'root' })
export class DatatableHelper {
  constructor() {
    // Suppress DataTables "Requested unknown parameter" and other non-critical warnings
    $.fn.dataTable.ext.errMode = 'none';
  }

  /** Initialize a plain (non-AJAX) DataTable from existing DOM rows. */
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

  /** Initialize a DataTable instance by calling its build() method. */
  initTable(tableId: string, dt: DataTable): void {
    this.initWithAjax(tableId, dt.build());
  }

  /** Initialize a DataTable with a full options object (AJAX, columns, callbacks, etc.). */
  initWithAjax(tableId: string, options: object): void {
    const table = $(tableId);
    if ($.fn.dataTable.isDataTable(tableId)) {
      table.DataTable().destroy();
    }
    table.DataTable(options);
  }

  /** Reload the AJAX data source without resetting the current page position. */
  reload(tableId: string): void {
    if ($.fn.dataTable.isDataTable(tableId)) {
      $(tableId).DataTable().ajax.reload(null, false);
    }
  }

  /** Destroy an existing DataTable, returning the table to plain HTML. */
  destroy(tableId: string): void {
    if ($.fn.dataTable.isDataTable(tableId)) {
      $(tableId).DataTable().destroy();
    }
  }
}
