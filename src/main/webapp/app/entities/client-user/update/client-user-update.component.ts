import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IClientUser, ClientUser } from '../client-user.model';
import { ClientUserService } from '../service/client-user.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IClientOrganisation } from 'app/entities/client-organisation/client-organisation.model';
import { ClientOrganisationService } from 'app/entities/client-organisation/service/client-organisation.service';

@Component({
  selector: 'jhi-client-user-update',
  templateUrl: './client-user-update.component.html',
})
export class ClientUserUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];
  clientOrganisationsSharedCollection: IClientOrganisation[] = [];

  editForm = this.fb.group({
    id: [],
    landline: [],
    mobile: [],
    user: [],
    client: [],
  });

  constructor(
    protected clientUserService: ClientUserService,
    protected userService: UserService,
    protected clientOrganisationService: ClientOrganisationService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ clientUser }) => {
      this.updateForm(clientUser);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const clientUser = this.createFromForm();
    if (clientUser.id !== undefined) {
      this.subscribeToSaveResponse(this.clientUserService.update(clientUser));
    } else {
      this.subscribeToSaveResponse(this.clientUserService.create(clientUser));
    }
  }

  trackUserById(index: number, item: IUser): number {
    return item.id!;
  }

  trackClientOrganisationById(index: number, item: IClientOrganisation): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IClientUser>>): void {
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

  protected updateForm(clientUser: IClientUser): void {
    this.editForm.patchValue({
      id: clientUser.id,
      landline: clientUser.landline,
      mobile: clientUser.mobile,
      user: clientUser.user,
      client: clientUser.client,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, clientUser.user);
    this.clientOrganisationsSharedCollection = this.clientOrganisationService.addClientOrganisationToCollectionIfMissing(
      this.clientOrganisationsSharedCollection,
      clientUser.client
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('user')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.clientOrganisationService
      .query()
      .pipe(map((res: HttpResponse<IClientOrganisation[]>) => res.body ?? []))
      .pipe(
        map((clientOrganisations: IClientOrganisation[]) =>
          this.clientOrganisationService.addClientOrganisationToCollectionIfMissing(clientOrganisations, this.editForm.get('client')!.value)
        )
      )
      .subscribe((clientOrganisations: IClientOrganisation[]) => (this.clientOrganisationsSharedCollection = clientOrganisations));
  }

  protected createFromForm(): IClientUser {
    return {
      ...new ClientUser(),
      id: this.editForm.get(['id'])!.value,
      landline: this.editForm.get(['landline'])!.value,
      mobile: this.editForm.get(['mobile'])!.value,
      user: this.editForm.get(['user'])!.value,
      client: this.editForm.get(['client'])!.value,
    };
  }
}
