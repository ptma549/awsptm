import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IMaterial, Material } from '../material.model';
import { MaterialService } from '../service/material.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IVisit } from 'app/entities/visit/visit.model';
import { VisitService } from 'app/entities/visit/service/visit.service';

@Component({
  selector: 'jhi-material-update',
  templateUrl: './material-update.component.html',
})
export class MaterialUpdateComponent implements OnInit {
  isSaving = false;

  visitsSharedCollection: IVisit[] = [];

  editForm = this.fb.group({
    id: [],
    name: [],
    description: [],
    quantity: [],
    unitCost: [],
    visit: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected materialService: MaterialService,
    protected visitService: VisitService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ material }) => {
      this.updateForm(material);

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
    const material = this.createFromForm();
    if (material.id !== undefined) {
      this.subscribeToSaveResponse(this.materialService.update(material));
    } else {
      this.subscribeToSaveResponse(this.materialService.create(material));
    }
  }

  trackVisitById(index: number, item: IVisit): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMaterial>>): void {
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

  protected updateForm(material: IMaterial): void {
    this.editForm.patchValue({
      id: material.id,
      name: material.name,
      description: material.description,
      quantity: material.quantity,
      unitCost: material.unitCost,
      visit: material.visit,
    });

    this.visitsSharedCollection = this.visitService.addVisitToCollectionIfMissing(this.visitsSharedCollection, material.visit);
  }

  protected loadRelationshipsOptions(): void {
    this.visitService
      .query()
      .pipe(map((res: HttpResponse<IVisit[]>) => res.body ?? []))
      .pipe(map((visits: IVisit[]) => this.visitService.addVisitToCollectionIfMissing(visits, this.editForm.get('visit')!.value)))
      .subscribe((visits: IVisit[]) => (this.visitsSharedCollection = visits));
  }

  protected createFromForm(): IMaterial {
    return {
      ...new Material(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      description: this.editForm.get(['description'])!.value,
      quantity: this.editForm.get(['quantity'])!.value,
      unitCost: this.editForm.get(['unitCost'])!.value,
      visit: this.editForm.get(['visit'])!.value,
    };
  }
}
