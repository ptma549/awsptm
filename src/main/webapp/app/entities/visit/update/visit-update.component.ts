import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IVisit, Visit } from '../visit.model';
import { VisitService } from '../service/visit.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IJob } from 'app/entities/job/job.model';
import { JobService } from 'app/entities/job/service/job.service';

@Component({
  selector: 'jhi-visit-update',
  templateUrl: './visit-update.component.html',
})
export class VisitUpdateComponent implements OnInit {
  isSaving = false;

  jobsSharedCollection: IJob[] = [];

  editForm = this.fb.group({
    id: [],
    arrived: [],
    departed: [],
    description: [],
    actions: [],
    labour: [],
    job: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected visitService: VisitService,
    protected jobService: JobService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ visit }) => {
      if (visit.id === undefined) {
        const today = dayjs().startOf('day');
        visit.arrived = today;
        visit.departed = today;
      }

      this.updateForm(visit);

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
    const visit = this.createFromForm();
    if (visit.id !== undefined) {
      this.subscribeToSaveResponse(this.visitService.update(visit));
    } else {
      this.subscribeToSaveResponse(this.visitService.create(visit));
    }
  }

  trackJobById(index: number, item: IJob): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IVisit>>): void {
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

  protected updateForm(visit: IVisit): void {
    this.editForm.patchValue({
      id: visit.id,
      arrived: visit.arrived ? visit.arrived.format(DATE_TIME_FORMAT) : null,
      departed: visit.departed ? visit.departed.format(DATE_TIME_FORMAT) : null,
      description: visit.description,
      actions: visit.actions,
      labour: visit.labour,
      job: visit.job,
    });

    this.jobsSharedCollection = this.jobService.addJobToCollectionIfMissing(this.jobsSharedCollection, visit.job);
  }

  protected loadRelationshipsOptions(): void {
    this.jobService
      .query()
      .pipe(map((res: HttpResponse<IJob[]>) => res.body ?? []))
      .pipe(map((jobs: IJob[]) => this.jobService.addJobToCollectionIfMissing(jobs, this.editForm.get('job')!.value)))
      .subscribe((jobs: IJob[]) => (this.jobsSharedCollection = jobs));
  }

  protected createFromForm(): IVisit {
    return {
      ...new Visit(),
      id: this.editForm.get(['id'])!.value,
      arrived: this.editForm.get(['arrived'])!.value ? dayjs(this.editForm.get(['arrived'])!.value, DATE_TIME_FORMAT) : undefined,
      departed: this.editForm.get(['departed'])!.value ? dayjs(this.editForm.get(['departed'])!.value, DATE_TIME_FORMAT) : undefined,
      description: this.editForm.get(['description'])!.value,
      actions: this.editForm.get(['actions'])!.value,
      labour: this.editForm.get(['labour'])!.value,
      job: this.editForm.get(['job'])!.value,
    };
  }
}
