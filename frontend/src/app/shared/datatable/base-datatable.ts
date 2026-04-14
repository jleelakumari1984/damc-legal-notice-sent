import { inject } from "@angular/core";
import { AuthService } from "../../core/services/auth/auth.service";

export abstract class DataTable {
  abstract build(): object;
}
