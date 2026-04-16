import { Component, EventEmitter, Output } from '@angular/core';
import { UserFilter } from '../../../core/models/user.model';

declare const $: any;

@Component({
    selector: 'app-user-filter',
    templateUrl: './user-filter.component.html'
})
export class UserFilterComponent {
    @Output() filterChange = new EventEmitter<UserFilter>();

    search = '';
    accessLevel: number | null = null;
    enabled: boolean | null = null;

    open(): void {
        $('#userFilterModal').modal('show');
    }

    apply(): void {
        this.filterChange.emit(this.buildFilter());
        $('#userFilterModal').modal('hide');
    }

    reset(): void {
        this.search = '';
        this.accessLevel = null;
        this.enabled = null;
        this.filterChange.emit({});
        $('#userFilterModal').modal('hide');
    }

    buildFilter(): UserFilter {
        const f: UserFilter = {};
        if (this.search.trim()) f.search = this.search.trim();
        if (this.accessLevel !== null) f.accessLevel = this.accessLevel;
        if (this.enabled !== null) f.enabled = this.enabled;
        return f;
    }
} 
