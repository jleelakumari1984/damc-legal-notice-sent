import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';

import { StorageService } from '../services/storage.service';

@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {
  constructor(private readonly storageService: StorageService, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const allowedLevels: number[] = route.data['allowedLevels'] ?? [];
    const userLevel = this.storageService.getUser()?.accessLevel;
    if (userLevel !== undefined && allowedLevels.includes(userLevel)) {
      return true;
    }
    this.router.navigate(['/notice']);
    return false;
  }
}
