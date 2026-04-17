import { inject } from '@angular/core';
import { StorageService } from '../../core/services/storage.service';
import { UserResponse } from '../../core/services/auth/auth.service';

/**
 * Base class for all feature components.
 * Provides convenient accessors to StorageService role helpers.
 * Usage: export class MyComponent extends BaseComponent { ... }
 */
export abstract class BaseComponent {
    protected readonly storageService = inject(StorageService);

    get currentUser(): UserResponse | null {
        return this.storageService.getUser();
    }

    isSuperAdmin(): boolean {
        return this.storageService.isSuperAdmin();
    }

    isAdmin(): boolean {
        return this.storageService.isAdmin();
    }

    isUser(): boolean {
        return this.storageService.isUser();
    }

    isSuperOrAdmin(): boolean {
        return this.storageService.isSuperOrAdmin();
    }

    getUserId(): number | null {
        return this.storageService.getUser()?.id ?? null;
    }
}
