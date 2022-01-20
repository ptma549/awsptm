import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IGoogleAddress, GoogleAddress } from '../google-address.model';
import { GoogleAddressService } from '../service/google-address.service';

@Component({
  selector: 'jhi-google-address-update',
  templateUrl: './google-address-update.component.html',
})
export class GoogleAddressUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    position: [],
    url: [],
    html: [],
    formatted: [],
    types: [],
  });

  constructor(protected googleAddressService: GoogleAddressService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ googleAddress }) => {
      this.updateForm(googleAddress);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const googleAddress = this.createFromForm();
    if (googleAddress.id !== undefined) {
      this.subscribeToSaveResponse(this.googleAddressService.update(googleAddress));
    } else {
      this.subscribeToSaveResponse(this.googleAddressService.create(googleAddress));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGoogleAddress>>): void {
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

  protected updateForm(googleAddress: IGoogleAddress): void {
    this.editForm.patchValue({
      id: googleAddress.id,
      position: googleAddress.position,
      url: googleAddress.url,
      html: googleAddress.html,
      formatted: googleAddress.formatted,
      types: googleAddress.types,
    });
  }

  protected createFromForm(): IGoogleAddress {
    return {
      ...new GoogleAddress(),
      id: this.editForm.get(['id'])!.value,
      position: this.editForm.get(['position'])!.value,
      url: this.editForm.get(['url'])!.value,
      html: this.editForm.get(['html'])!.value,
      formatted: this.editForm.get(['formatted'])!.value,
      types: this.editForm.get(['types'])!.value,
    };
  }
}
