import { Injectable, NgZone } from '@angular/core';
import flatpickr from 'flatpickr';
import Inputmask from 'inputmask';

declare const $: any;

export interface DatetimePickerOptions {
    enableTime?: boolean;
    dateFormat?: string;
    allowInput?: boolean;
    minDate?: string | Date;
    maxDate?: string | Date;
    defaultDate?: string | Date;
    onChange?: (selectedDates: Date[], dateStr: string) => void;
}

export interface Select2Options {
    placeholder?: string;
    allowClear?: boolean;
    width?: string;
    defaultValue?: string;
    [key: string]: unknown;
}

@Injectable({ providedIn: 'root' })
export class FormService {

    constructor(private readonly zone: NgZone) { }

    /**
     * Initialise flatpickr datetime picker on the given CSS selector.
     * onChange callback runs inside Angular's zone so change detection fires.
     */
    initDatetimePicker(
        selector: string,
        onChange?: (date: Date | null, dateStr: string) => void,
        options: DatetimePickerOptions = {}
    ): void {
        this.zone.runOutsideAngular(() => {
            const el = document.querySelector(selector) as HTMLElement;
            if (!el) return;
            flatpickr(el, {
                enableTime: true,
                dateFormat: 'Y-m-d H:i',
                allowInput: true,
                ...options,
                onChange: (selectedDates: Date[], dateStr: string) => {
                    if (onChange) {
                        this.zone.run(() => onChange(selectedDates[0] ?? null, dateStr));
                    }
                }
            });
        });
    }

    /**
     * Destroy flatpickr instance on the given selector.
     */
    destroyDatetimePicker(selector: string): void {
        const el = document.querySelector(selector);
        if (el && (el as any)._flatpickr) {
            (el as any)._flatpickr.destroy();
        }
    }

    /**
     * Initialise select2 on the given CSS selector.
     * onChange callback runs inside Angular's zone so ngModel stays in sync.
     */
    initSelect2(
        selector: string,
        onChange?: (value: string) => void,
        options: Select2Options = {}
    ): void {
        this.zone.runOutsideAngular(() => {
            $(selector).select2({
                width: '100%',
                allowClear: true,
                placeholder: 'Select...',
                ...options,
                dropdownParent: $(selector).closest('.modal').length
                    ? $(selector).closest('.modal')
                    : undefined
            });

            if (onChange) {
                $(selector).on('change.select2', () => {
                    const val: string = $(selector).val() ?? '';
                    this.zone.run(() => onChange(val));
                });

                $(selector).on('set.default', () => {
                    $(selector).val(options.defaultValue ?? '').trigger('change.select2');
                });
            }
        });
    }

    /**
     * Destroy select2 instance on the given selector.
     */
    destroySelect2(selector: string): void {
        this.zone.runOutsideAngular(() => {
            if ($(selector).data('select2')) {
                $(selector).off('change.select2');
                $(selector).select2('destroy');
            }
        });
    }

    /**
     * Apply numeric input mask (digits only, up to 15 characters).
     */
    initNumberMask(selector: string, mask = '9{1,15}'): void {
        this.zone.runOutsideAngular(() => {
            Inputmask({ regex: '[0-9]*', greedy: false, placeholder: '' })
                .mask(document.querySelectorAll(selector));
        });
    }

    /**
     * Remove inputmask from the given selector.
     */
    removeNumberMask(selector: string): void {
        this.zone.runOutsideAngular(() => {
            const elements = document.querySelectorAll(selector);
            elements.forEach(el => Inputmask.remove(el as HTMLElement));
        });
    }
}
