import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IAddressComponent, AddressComponent } from '../address-component.model';
import { AddressComponentService } from '../service/address-component.service';
import { IGoogleAddress } from 'app/entities/google-address/google-address.model';
import { GoogleAddressService } from 'app/entities/google-address/service/google-address.service';
import { IAddressType } from 'app/entities/address-type/address-type.model';
import { AddressTypeService } from 'app/entities/address-type/service/address-type.service';

@Component({
  selector: 'jhi-address-component-update',
  templateUrl: './address-component-update.component.html',
})
export class AddressComponentUpdateComponent implements OnInit {
  isSaving = false;

  googleAddressesSharedCollection: IGoogleAddress[] = [];
  addressTypesSharedCollection: IAddressType[] = [];

  editForm = this.fb.group({
    id: [],
    longName: [],
    shortName: [],
    address: [],
    type: [],
  });

  constructor(
    protected addressComponentService: AddressComponentService,
    protected googleAddressService: GoogleAddressService,
    protected addressTypeService: AddressTypeService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ addressComponent }) => {
      this.updateForm(addressComponent);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const addressComponent = this.createFromForm();
    if (addressComponent.id !== undefined) {
      this.subscribeToSaveResponse(this.addressComponentService.update(addressComponent));
    } else {
      this.subscribeToSaveResponse(this.addressComponentService.create(addressComponent));
    }
  }

  trackGoogleAddressById(index: number, item: IGoogleAddress): number {
    return item.id!;
  }

  trackAddressTypeById(index: number, item: IAddressType): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAddressComponent>>): void {
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

  protected updateForm(addressComponent: IAddressComponent): void {
    this.editForm.patchValue({
      id: addressComponent.id,
      longName: addressComponent.longName,
      shortName: addressComponent.shortName,
      address: addressComponent.address,
      type: addressComponent.type,
    });

    this.googleAddressesSharedCollection = this.googleAddressService.addGoogleAddressToCollectionIfMissing(
      this.googleAddressesSharedCollection,
      addressComponent.address
    );
    this.addressTypesSharedCollection = this.addressTypeService.addAddressTypeToCollectionIfMissing(
      this.addressTypesSharedCollection,
      addressComponent.type
    );
  }

  protected loadRelationshipsOptions(): void {
    this.googleAddressService
      .query()
      .pipe(map((res: HttpResponse<IGoogleAddress[]>) => res.body ?? []))
      .pipe(
        map((googleAddresses: IGoogleAddress[]) =>
          this.googleAddressService.addGoogleAddressToCollectionIfMissing(googleAddresses, this.editForm.get('address')!.value)
        )
      )
      .subscribe((googleAddresses: IGoogleAddress[]) => (this.googleAddressesSharedCollection = googleAddresses));

    this.addressTypeService
      .query()
      .pipe(map((res: HttpResponse<IAddressType[]>) => res.body ?? []))
      .pipe(
        map((addressTypes: IAddressType[]) =>
          this.addressTypeService.addAddressTypeToCollectionIfMissing(addressTypes, this.editForm.get('type')!.value)
        )
      )
      .subscribe((addressTypes: IAddressType[]) => (this.addressTypesSharedCollection = addressTypes));
  }

  protected createFromForm(): IAddressComponent {
    return {
      ...new AddressComponent(),
      id: this.editForm.get(['id'])!.value,
      longName: this.editForm.get(['longName'])!.value,
      shortName: this.editForm.get(['shortName'])!.value,
      address: this.editForm.get(['address'])!.value,
      type: this.editForm.get(['type'])!.value,
    };
  }
}
