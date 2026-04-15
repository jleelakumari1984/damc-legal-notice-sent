import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { ConfirmModalService, ConfirmOptions } from './confirm-modal.service';

declare const $: any;

@Component({
  selector: 'app-confirm-modal',
  template: `
    <div class="modal fade" id="globalConfirmModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ title }}</h5>
            <button type="button" class="btn-close" (click)="cancel()"></button>
          </div>
          <div class="modal-body">{{ message }}</div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" (click)="cancel()">Cancel</button>
            <button type="button" [class]="'btn ' + confirmClass" (click)="confirm()">
              {{ confirmLabel }}
            </button>
          </div>
        </div>
      </div>
    </div>
  `
})
export class ConfirmModalComponent implements OnInit, OnDestroy {
  title = 'Confirm';
  message = '';
  confirmLabel = 'Confirm';
  confirmClass = 'btn-primary';

  private resolve: ((value: boolean) => void) | null = null;
  private sub!: Subscription;

  constructor(private readonly confirmService: ConfirmModalService) {}

  ngOnInit(): void {
    this.sub = this.confirmService.request$.subscribe((req) => {
      this.title = req.title ?? 'Confirm';
      this.message = req.message;
      this.confirmLabel = req.confirmLabel ?? 'Confirm';
      this.confirmClass = req.confirmClass ?? 'btn-primary';
      this.resolve = req.resolve;
      $('#globalConfirmModal').modal('show');
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  confirm(): void {
    $('#globalConfirmModal').modal('hide');
    this.resolve?.(true);
    this.resolve = null;
  }

  cancel(): void {
    $('#globalConfirmModal').modal('hide');
    this.resolve?.(false);
    this.resolve = null;
  }
}
