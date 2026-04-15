import { Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';

import { UserService } from '../../../../core/services/user.service';
import { Credit, CreditRequest } from '../../../../core/models/credit.model';
import { CreditService } from '../../../../core/services/credit.service';
import { User } from '../../../../core/models/user.model';

declare const $: any;

@Component({
  selector: 'app-credit-form',
  templateUrl: './credit-form.component.html'
})
export class CreditFormComponent implements OnInit, OnChanges, OnDestroy {
  @Output() saved = new EventEmitter<void>();
  @Input() selectedUserId: number | null = null;

  users: User[] = [];
  saving = false;
  errorMessage = '';

  creditTypes = ['CREDIT', 'DEBIT'];
  channels = ['SMS', 'WHATSAPP', 'EMAIL'];

  form!: FormGroup;

  private typeSub!: Subscription;

  constructor(
    private readonly fb: FormBuilder,
    private readonly service: CreditService,
    private readonly userService: UserService
  ) { }

  ngOnInit(): void {
    this.form = this.fb.group({
      userId: ['', Validators.required],
      channel: ['SMS', Validators.required],
      credits: ['', [Validators.required, Validators.min(1)]],
      pricePerUnit: ['', [Validators.required, Validators.min(0.01)]],
      type: ['CREDIT', Validators.required],
      description: ['', [Validators.required, Validators.maxLength(500)]]
    });
    this.typeSub = this.form.get('type')!.valueChanges.subscribe(type => this.updatePriceValidation(type));
    if (!this.selectedUserId) {
      this.loadUsers();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!this.form) return;
    if (changes['editCredit']) {
      const credit = changes['editCredit'].currentValue as Credit | null;
      if (credit) {
        this.form.setValue({
          userId: credit.userId,
          channel: credit.channel,
          credits: credit.credits,
          pricePerUnit: credit.pricePerUnit,
          type: credit.type,
          description: credit.description
        });
      } else {
        this.form.reset({ userId: '', channel: 'SMS', credits: '', pricePerUnit: '', type: 'CREDIT', description: '' });
      }
      this.errorMessage = '';
    }
  }

  ngOnDestroy(): void {
    this.typeSub?.unsubscribe();
  }

  open(): void {
    if (this.selectedUserId !== null) {
      this.form.get('userId')!.setValue(this.selectedUserId);
    }
    $('#creditModal').modal('show');
  }

  cancel(): void {
    this.form.reset({ userId: this.selectedUserId ?? '', channel: 'SMS', credits: '', pricePerUnit: '', type: 'CREDIT', description: '' });
    this.errorMessage = '';
    $('#creditModal').modal('hide');
  }

  submit(): void {
    if (this.form.invalid) return;
    const request: CreditRequest = {
      userId: Number(this.form.value.userId),
      channel: this.form.value.channel,
      credits: Number(this.form.value.credits),
      pricePerUnit: this.form.value.type === 'CREDIT' ? Number(this.form.value.pricePerUnit) : 0,
      type: this.form.value.type,
      description: this.form.value.description.trim()
    };
    this.saving = true;
    this.errorMessage = '';

    const call = this.service.create(request);

    call.subscribe({
      next: () => {
        this.saving = false;
        this.form.reset({ userId: this.selectedUserId ?? '', channel: 'SMS', credits: '', pricePerUnit: '', type: 'CREDIT', description: '' });
        $('#creditModal').modal('hide');
        this.saved.emit();
      },
      error: () => {
        this.errorMessage = 'Failed to create record.';
        this.saving = false;
      }
    });
  }

  private loadUsers(): void {
    this.userService.getAll().subscribe({
      next: (data) => { this.users = data.data; },
      error: () => { }
    });
  }

  private updatePriceValidation(type: string): void {
    const ctrl = this.form.get('pricePerUnit')!;
    if (type === 'CREDIT') {
      ctrl.setValidators([Validators.required, Validators.min(0.01)]);
    } else {
      ctrl.clearValidators();
      ctrl.setValue('');
    }
    ctrl.updateValueAndValidity();
  }
}
