import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IGoogleAddress } from '../google-address.model';

@Component({
  selector: 'jhi-google-address-detail',
  templateUrl: './google-address-detail.component.html',
})
export class GoogleAddressDetailComponent implements OnInit {
  googleAddress: IGoogleAddress | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ googleAddress }) => {
      this.googleAddress = googleAddress;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
