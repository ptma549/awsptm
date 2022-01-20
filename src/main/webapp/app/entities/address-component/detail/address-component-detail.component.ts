import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAddressComponent } from '../address-component.model';

@Component({
  selector: 'jhi-address-component-detail',
  templateUrl: './address-component-detail.component.html',
})
export class AddressComponentDetailComponent implements OnInit {
  addressComponent: IAddressComponent | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ addressComponent }) => {
      this.addressComponent = addressComponent;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
