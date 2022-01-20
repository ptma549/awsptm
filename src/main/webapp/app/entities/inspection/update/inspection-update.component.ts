import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IInspection, Inspection } from '../inspection.model';
import { InspectionService } from '../service/inspection.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IJob } from 'app/entities/job/job.model';
import { JobService } from 'app/entities/job/service/job.service';
import { IClientUser } from 'app/entities/client-user/client-user.model';
import { ClientUserService } from 'app/entities/client-user/service/client-user.service';
import { IAddress } from 'app/entities/address/address.model';
import { AddressService } from 'app/entities/address/service/address.service';
import { Priority } from 'app/entities/enumerations/priority.model';

@Component({
  selector: 'jhi-inspection-update',
  templateUrl: './inspection-update.component.html',
})
export class InspectionUpdateComponent implements OnInit {
  isSaving = false;
  priorityValues = Object.keys(Priority);

  jobsSharedCollection: IJob[] = [];
  clientUsersSharedCollection: IClientUser[] = [];
  addressesSharedCollection: IAddress[] = [];

  editForm = this.fb.group({
    id: [],
    priority: [],
    created: [],
    occupiersName: [],
    occupiersHomePhone: [],
    occupiersWorkPhone: [],
    occupiersMobilePhone: [],
    work: [],
    accessInstructions: [],
    updated: [],
    start: [],
    frequency: [],
    jobs: [],
    createdBy: [],
    address: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected inspectionService: InspectionService,
    protected jobService: JobService,
    protected clientUserService: ClientUserService,
    protected addressService: AddressService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ inspection }) => {
      if (inspection.id === undefined) {
        const today = dayjs().startOf('day');
        inspection.created = today;
        inspection.updated = today;
      }

      this.updateForm(inspection);

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('awsptmApp.error', { message: err.message })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const inspection = this.createFromForm();
    if (inspection.id !== undefined) {
      this.subscribeToSaveResponse(this.inspectionService.update(inspection));
    } else {
      this.subscribeToSaveResponse(this.inspectionService.create(inspection));
    }
  }

  trackJobById(index: number, item: IJob): number {
    return item.id!;
  }

  trackClientUserById(index: number, item: IClientUser): number {
    return item.id!;
  }

  trackAddressById(index: number, item: IAddress): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IInspection>>): void {
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

  protected updateForm(inspection: IInspection): void {
    this.editForm.patchValue({
      id: inspection.id,
      priority: inspection.priority,
      created: inspection.created ? inspection.created.format(DATE_TIME_FORMAT) : null,
      occupiersName: inspection.occupiersName,
      occupiersHomePhone: inspection.occupiersHomePhone,
      occupiersWorkPhone: inspection.occupiersWorkPhone,
      occupiersMobilePhone: inspection.occupiersMobilePhone,
      work: inspection.work,
      accessInstructions: inspection.accessInstructions,
      updated: inspection.updated ? inspection.updated.format(DATE_TIME_FORMAT) : null,
      start: inspection.start,
      frequency: inspection.frequency,
      jobs: inspection.jobs,
      createdBy: inspection.createdBy,
      address: inspection.address,
    });

    this.jobsSharedCollection = this.jobService.addJobToCollectionIfMissing(this.jobsSharedCollection, inspection.jobs);
    this.clientUsersSharedCollection = this.clientUserService.addClientUserToCollectionIfMissing(
      this.clientUsersSharedCollection,
      inspection.createdBy
    );
    this.addressesSharedCollection = this.addressService.addAddressToCollectionIfMissing(
      this.addressesSharedCollection,
      inspection.address
    );
  }

  protected loadRelationshipsOptions(): void {
    this.jobService
      .query()
      .pipe(map((res: HttpResponse<IJob[]>) => res.body ?? []))
      .pipe(map((jobs: IJob[]) => this.jobService.addJobToCollectionIfMissing(jobs, this.editForm.get('jobs')!.value)))
      .subscribe((jobs: IJob[]) => (this.jobsSharedCollection = jobs));

    this.clientUserService
      .query()
      .pipe(map((res: HttpResponse<IClientUser[]>) => res.body ?? []))
      .pipe(
        map((clientUsers: IClientUser[]) =>
          this.clientUserService.addClientUserToCollectionIfMissing(clientUsers, this.editForm.get('createdBy')!.value)
        )
      )
      .subscribe((clientUsers: IClientUser[]) => (this.clientUsersSharedCollection = clientUsers));

    this.addressService
      .query()
      .pipe(map((res: HttpResponse<IAddress[]>) => res.body ?? []))
      .pipe(
        map((addresses: IAddress[]) => this.addressService.addAddressToCollectionIfMissing(addresses, this.editForm.get('address')!.value))
      )
      .subscribe((addresses: IAddress[]) => (this.addressesSharedCollection = addresses));
  }

  protected createFromForm(): IInspection {
    return {
      ...new Inspection(),
      id: this.editForm.get(['id'])!.value,
      priority: this.editForm.get(['priority'])!.value,
      created: this.editForm.get(['created'])!.value ? dayjs(this.editForm.get(['created'])!.value, DATE_TIME_FORMAT) : undefined,
      occupiersName: this.editForm.get(['occupiersName'])!.value,
      occupiersHomePhone: this.editForm.get(['occupiersHomePhone'])!.value,
      occupiersWorkPhone: this.editForm.get(['occupiersWorkPhone'])!.value,
      occupiersMobilePhone: this.editForm.get(['occupiersMobilePhone'])!.value,
      work: this.editForm.get(['work'])!.value,
      accessInstructions: this.editForm.get(['accessInstructions'])!.value,
      updated: this.editForm.get(['updated'])!.value ? dayjs(this.editForm.get(['updated'])!.value, DATE_TIME_FORMAT) : undefined,
      start: this.editForm.get(['start'])!.value,
      frequency: this.editForm.get(['frequency'])!.value,
      jobs: this.editForm.get(['jobs'])!.value,
      createdBy: this.editForm.get(['createdBy'])!.value,
      address: this.editForm.get(['address'])!.value,
    };
  }
}
