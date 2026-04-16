import { Component, EventEmitter, Input, Output } from '@angular/core';

import { User } from '../../../core/models/user.model';

@Component({
  selector: 'app-user-endpoints',
  templateUrl: './user-endpoints.component.html'
})
export class UserEndpointsComponent {
  @Input() user: User | null = null;
  @Output() close = new EventEmitter<void>();

  activeTab: 'sms' | 'whatsapp' = 'sms';
}
