import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';

import { StorageService } from '../services/storage.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private readonly storageService: StorageService, private readonly router: Router) {}

  canActivate(): boolean {
    if (this.storageService.isAuthenticated()) {
      return true;
    }
    this.router.navigate(['/login']);
    return false;
  }
}
