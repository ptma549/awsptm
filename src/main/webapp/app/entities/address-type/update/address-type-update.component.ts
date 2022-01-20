import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IAddressType, AddressType } from '../address-type.model';
import { AddressTypeService } from '../service/address-type.service';

@Component({
  selector: 'jhi-address-type-update',
  templateUrl: './address-type-update.component.html',
})
export class AddressTypeUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [],
    position: [],
  });

  constructor(protected addressTypeService: AddressTypeService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ addressType }) => {
      this.updateForm(addressType);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const addressType = this.createFromForm();
    if (addressType.id !== undefined) {
      this.subscribeToSaveResponse(this.addressTypeService.update(addressType));
    } else {
      this.subscribeToSaveResponse(this.addressTypeService.create(addressType));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAddressType>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(addressType: IAddressType): void {
    this.editForm.patchValue({
      id: addressType.id,
      name: addressType.name,
      position: addressType.position,
    });
  }

  protected createFromForm(): IAddressType {
    return {
      ...new AddressType(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      position: this.editForm.get(['position'])!.value,
    };
  }
}
