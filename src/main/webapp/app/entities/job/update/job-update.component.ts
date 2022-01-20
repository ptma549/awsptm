import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IJob, Job } from '../job.model';
import { JobService } from '../service/job.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IClientUser } from 'app/entities/client-user/client-user.model';
import { ClientUserService } from 'app/entities/client-user/service/client-user.service';
import { IEngineer } from 'app/entities/engineer/engineer.model';
import { EngineerService } from 'app/entities/engineer/service/engineer.service';
import { IAddress } from 'app/entities/address/address.model';
import { AddressService } from 'app/entities/address/service/address.service';
import { Priority } from 'app/entities/enumerations/priority.model';

@Component({
  selector: 'jhi-job-update',
  templateUrl: './job-update.component.html',
})
export class JobUpdateComponent implements OnInit {
  isSaving = false;
  priorityValues = Object.keys(Priority);

  clientUsersSharedCollection: IClientUser[] = [];
  engineersSharedCollection: IEngineer[] = [];
  addressesSharedCollection: IAddress[] = [];

  editForm = this.fb.group({
    id: [],
    priority: [],
    created: [],
    occupiersName: [],
    occupiersHomePhone: [],
    occupiersWorkPhone: [],
    occupiersMobilePhone: [],
    clientOrderId: [],
    assignedAt: [],
    scheduled: [],
    completed: [],
    invoiceNumber: [],
    fault: [],
    accessInstructions: [],
    updated: [],
    createdBy: [],
    assignedTo: [],
    address: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected jobService: JobService,
    protected clientUserService: ClientUserService,
    protected engineerService: EngineerService,
    protected addressService: AddressService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ job }) => {
      if (job.id === undefined) {
        const today = dayjs().startOf('day');
        job.created = today;
        job.assignedAt = today;
        job.scheduled = today;
        job.completed = today;
        job.updated = today;
      }

      this.updateForm(job);

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
    const job = this.createFromForm();
    if (job.id !== undefined) {
      this.subscribeToSaveResponse(this.jobService.update(job));
    } else {
      this.subscribeToSaveResponse(this.jobService.create(job));
    }
  }

  trackClientUserById(index: number, item: IClientUser): number {
    return item.id!;
  }

  trackEngineerById(index: number, item: IEngineer): number {
    return item.id!;
  }

  trackAddressById(index: number, item: IAddress): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IJob>>): void {
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

  protected updateForm(job: IJob): void {
    this.editForm.patchValue({
      id: job.id,
      priority: job.priority,
      created: job.created ? job.created.format(DATE_TIME_FORMAT) : null,
      occupiersName: job.occupiersName,
      occupiersHomePhone: job.occupiersHomePhone,
      occupiersWorkPhone: job.occupiersWorkPhone,
      occupiersMobilePhone: job.occupiersMobilePhone,
      clientOrderId: job.clientOrderId,
      assignedAt: job.assignedAt ? job.assignedAt.format(DATE_TIME_FORMAT) : null,
      scheduled: job.scheduled ? job.scheduled.format(DATE_TIME_FORMAT) : null,
      completed: job.completed ? job.completed.format(DATE_TIME_FORMAT) : null,
      invoiceNumber: job.invoiceNumber,
      fault: job.fault,
      accessInstructions: job.accessInstructions,
      updated: job.updated ? job.updated.format(DATE_TIME_FORMAT) : null,
      createdBy: job.createdBy,
      assignedTo: job.assignedTo,
      address: job.address,
    });

    this.clientUsersSharedCollection = this.clientUserService.addClientUserToCollectionIfMissing(
      this.clientUsersSharedCollection,
      job.createdBy
    );
    this.engineersSharedCollection = this.engineerService.addEngineerToCollectionIfMissing(this.engineersSharedCollection, job.assignedTo);
    this.addressesSharedCollection = this.addressService.addAddressToCollectionIfMissing(this.addressesSharedCollection, job.address);
  }

  protected loadRelationshipsOptions(): void {
    this.clientUserService
      .query()
      .pipe(map((res: HttpResponse<IClientUser[]>) => res.body ?? []))
      .pipe(
        map((clientUsers: IClientUser[]) =>
          this.clientUserService.addClientUserToCollectionIfMissing(clientUsers, this.editForm.get('createdBy')!.value)
        )
      )
      .subscribe((clientUsers: IClientUser[]) => (this.clientUsersSharedCollection = clientUsers));

    this.engineerService
      .query()
      .pipe(map((res: HttpResponse<IEngineer[]>) => res.body ?? []))
      .pipe(
        map((engineers: IEngineer[]) =>
          this.engineerService.addEngineerToCollectionIfMissing(engineers, this.editForm.get('assignedTo')!.value)
        )
      )
      .subscribe((engineers: IEngineer[]) => (this.engineersSharedCollection = engineers));

    this.addressService
      .query()
      .pipe(map((res: HttpResponse<IAddress[]>) => res.body ?? []))
      .pipe(
        map((addresses: IAddress[]) => this.addressService.addAddressToCollectionIfMissing(addresses, this.editForm.get('address')!.value))
      )
      .subscribe((addresses: IAddress[]) => (this.addressesSharedCollection = addresses));
  }

  protected createFromForm(): IJob {
    return {
      ...new Job(),
      id: this.editForm.get(['id'])!.value,
      priority: this.editForm.get(['priority'])!.value,
      created: this.editForm.get(['created'])!.value ? dayjs(this.editForm.get(['created'])!.value, DATE_TIME_FORMAT) : undefined,
      occupiersName: this.editForm.get(['occupiersName'])!.value,
      occupiersHomePhone: this.editForm.get(['occupiersHomePhone'])!.value,
      occupiersWorkPhone: this.editForm.get(['occupiersWorkPhone'])!.value,
      occupiersMobilePhone: this.editForm.get(['occupiersMobilePhone'])!.value,
      clientOrderId: this.editForm.get(['clientOrderId'])!.value,
      assignedAt: this.editForm.get(['assignedAt'])!.value ? dayjs(this.editForm.get(['assignedAt'])!.value, DATE_TIME_FORMAT) : undefined,
      scheduled: this.editForm.get(['scheduled'])!.value ? dayjs(this.editForm.get(['scheduled'])!.value, DATE_TIME_FORMAT) : undefined,
      completed: this.editForm.get(['completed'])!.value ? dayjs(this.editForm.get(['completed'])!.value, DATE_TIME_FORMAT) : undefined,
      invoiceNumber: this.editForm.get(['invoiceNumber'])!.value,
      fault: this.editForm.get(['fault'])!.value,
      accessInstructions: this.editForm.get(['accessInstructions'])!.value,
      updated: this.editForm.get(['updated'])!.value ? dayjs(this.editForm.get(['updated'])!.value, DATE_TIME_FORMAT) : undefined,
      createdBy: this.editForm.get(['createdBy'])!.value,
      assignedTo: this.editForm.get(['assignedTo'])!.value,
      address: this.editForm.get(['address'])!.value,
    };
  }
}
