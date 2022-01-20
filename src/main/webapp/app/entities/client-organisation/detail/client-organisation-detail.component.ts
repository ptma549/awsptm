import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IClientOrganisation } from '../client-organisation.model';

@Component({
  selector: 'jhi-client-organisation-detail',
  templateUrl: './client-organisation-detail.component.html',
})
export class ClientOrganisationDetailComponent implements OnInit {
  clientOrganisation: IClientOrganisation | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ clientOrganisation }) => {
      this.clientOrganisation = clientOrganisation;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
