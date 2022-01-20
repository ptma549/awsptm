import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IAddress, Address } from '../address.model';
import { AddressService } from '../service/address.service';
import { IGoogleAddress } from 'app/entities/google-address/google-address.model';
import { GoogleAddressService } from 'app/entities/google-address/service/google-address.service';

@Component({
  selector: 'jhi-address-update',
  templateUrl: './address-update.component.html',
})
export class AddressUpdateComponent implements OnInit {
  isSaving = false;

  googleAddressesCollection: IGoogleAddress[] = [];

  editForm = this.fb.group({
    id: [],
    postcode: [],
    number: [],
    position: [],
    addressLine1: [],
    addressLine2: [],
    town: [],
    county: [],
    googleAddress: [],
  });

  constructor(
    protected addressService: AddressService,
    protected googleAddressService: GoogleAddressService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ address }) => {
      this.updateForm(address);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const address = this.createFromForm();
    if (address.id !== undefined) {
      this.subscribeToSaveResponse(this.addressService.update(address));
    } else {
      this.subscribeToSaveResponse(this.addressService.create(address));
    }
  }

  trackGoogleAddressById(index: number, item: IGoogleAddress): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAddress>>): void {
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

  protected updateForm(address: IAddress): void {
    this.editForm.patchValue({
      id: address.id,
      postcode: address.postcode,
      number: address.number,
      position: address.position,
      addressLine1: address.addressLine1,
      addressLine2: address.addressLine2,
      town: address.town,
      county: address.county,
      googleAddress: address.googleAddress,
    });

    this.googleAddressesCollection = this.googleAddressService.addGoogleAddressToCollectionIfMissing(
      this.googleAddressesCollection,
      address.googleAddress
    );
  }

  protected loadRelationshipsOptions(): void {
    this.googleAddressService
      .query({ 'addressId.specified': 'false' })
      .pipe(map((res: HttpResponse<IGoogleAddress[]>) => res.body ?? []))
      .pipe(
        map((googleAddresses: IGoogleAddress[]) =>
          this.googleAddressService.addGoogleAddressToCollectionIfMissing(googleAddresses, this.editForm.get('googleAddress')!.value)
        )
      )
      .subscribe((googleAddresses: IGoogleAddress[]) => (this.googleAddressesCollection = googleAddresses));
  }

  protected createFromForm(): IAddress {
    return {
      ...new Address(),
      id: this.editForm.get(['id'])!.value,
      postcode: this.editForm.get(['postcode'])!.value,
      number: this.editForm.get(['number'])!.value,
      position: this.editForm.get(['position'])!.value,
      addressLine1: this.editForm.get(['addressLine1'])!.value,
      addressLine2: this.editForm.get(['addressLine2'])!.value,
      town: this.editForm.get(['town'])!.value,
      county: this.editForm.get(['county'])!.value,
      googleAddress: this.editForm.get(['googleAddress'])!.value,
    };
  }
}
