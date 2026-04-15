import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

export interface ConfirmOptions {
  title?: string;
  message: string;
  confirmLabel?: string;
  confirmClass?: string;
}

interface ConfirmRequest extends ConfirmOptions {
  resolve: (value: boolean) => void;
}

@Injectable({ providedIn: 'root' })
export class ConfirmModalService {
  readonly request$ = new Subject<ConfirmRequest>();

  confirm(options: ConfirmOptions): Promise<boolean> {
    return new Promise<boolean>((resolve) => {
      this.request$.next({ ...options, resolve });
    });
  }
}
