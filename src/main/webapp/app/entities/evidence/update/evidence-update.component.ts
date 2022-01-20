import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IEvidence, Evidence } from '../evidence.model';
import { EvidenceService } from '../service/evidence.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IVisit } from 'app/entities/visit/visit.model';
import { VisitService } from 'app/entities/visit/service/visit.service';

@Component({
  selector: 'jhi-evidence-update',
  templateUrl: './evidence-update.component.html',
})
export class EvidenceUpdateComponent implements OnInit {
  isSaving = false;

  visitsSharedCollection: IVisit[] = [];

  editForm = this.fb.group({
    id: [],
    name: [],
    url: [],
    image: [],
    imageContentType: [],
    visit: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected evidenceService: EvidenceService,
    protected visitService: VisitService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ evidence }) => {
      this.updateForm(evidence);

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

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const evidence = this.createFromForm();
    if (evidence.id !== undefined) {
      this.subscribeToSaveResponse(this.evidenceService.update(evidence));
    } else {
      this.subscribeToSaveResponse(this.evidenceService.create(evidence));
    }
  }

  trackVisitById(index: number, item: IVisit): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEvidence>>): void {
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

  protected updateForm(evidence: IEvidence): void {
    this.editForm.patchValue({
      id: evidence.id,
      name: evidence.name,
      url: evidence.url,
      image: evidence.image,
      imageContentType: evidence.imageContentType,
      visit: evidence.visit,
    });

    this.visitsSharedCollection = this.visitService.addVisitToCollectionIfMissing(this.visitsSharedCollection, evidence.visit);
  }

  protected loadRelationshipsOptions(): void {
    this.visitService
      .query()
      .pipe(map((res: HttpResponse<IVisit[]>) => res.body ?? []))
      .pipe(map((visits: IVisit[]) => this.visitService.addVisitToCollectionIfMissing(visits, this.editForm.get('visit')!.value)))
      .subscribe((visits: IVisit[]) => (this.visitsSharedCollection = visits));
  }

  protected createFromForm(): IEvidence {
    return {
      ...new Evidence(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      url: this.editForm.get(['url'])!.value,
      imageContentType: this.editForm.get(['imageContentType'])!.value,
      image: this.editForm.get(['image'])!.value,
      visit: this.editForm.get(['visit'])!.value,
    };
  }
}
