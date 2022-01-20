import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IClientOrganisation, ClientOrganisation } from '../client-organisation.model';
import { ClientOrganisationService } from '../service/client-organisation.service';

@Component({
  selector: 'jhi-client-organisation-update',
  templateUrl: './client-organisation-update.component.html',
})
export class ClientOrganisationUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [],
    domain: [],
  });

  constructor(
    protected clientOrganisationService: ClientOrganisationService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ clientOrganisation }) => {
      this.updateForm(clientOrganisation);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const clientOrganisation = this.createFromForm();
    if (clientOrganisation.id !== undefined) {
      this.subscribeToSaveResponse(this.clientOrganisationService.update(clientOrganisation));
    } else {
      this.subscribeToSaveResponse(this.clientOrganisationService.create(clientOrganisation));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IClientOrganisation>>): void {
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

  protected updateForm(clientOrganisation: IClientOrganisation): void {
    this.editForm.patchValue({
      id: clientOrganisation.id,
      name: clientOrganisation.name,
      domain: clientOrganisation.domain,
    });
  }

  protected createFromForm(): IClientOrganisation {
    return {
      ...new ClientOrganisation(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      domain: this.editForm.get(['domain'])!.value,
    };
  }
}
