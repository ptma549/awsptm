import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { EvidenceService } from '../service/evidence.service';
import { IEvidence, Evidence } from '../evidence.model';
import { IVisit } from 'app/entities/visit/visit.model';
import { VisitService } from 'app/entities/visit/service/visit.service';

import { EvidenceUpdateComponent } from './evidence-update.component';

describe('Evidence Management Update Component', () => {
  let comp: EvidenceUpdateComponent;
  let fixture: ComponentFixture<EvidenceUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let evidenceService: EvidenceService;
  let visitService: VisitService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [EvidenceUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(EvidenceUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EvidenceUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    evidenceService = TestBed.inject(EvidenceService);
    visitService = TestBed.inject(VisitService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Visit query and add missing value', () => {
      const evidence: IEvidence = { id: 456 };
      const visit: IVisit = { id: 3823 };
      evidence.visit = visit;

      const visitCollection: IVisit[] = [{ id: 249 }];
      jest.spyOn(visitService, 'query').mockReturnValue(of(new HttpResponse({ body: visitCollection })));
      const additionalVisits = [visit];
      const expectedCollection: IVisit[] = [...additionalVisits, ...visitCollection];
      jest.spyOn(visitService, 'addVisitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ evidence });
      comp.ngOnInit();

      expect(visitService.query).toHaveBeenCalled();
      expect(visitService.addVisitToCollectionIfMissing).toHaveBeenCalledWith(visitCollection, ...additionalVisits);
      expect(comp.visitsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const evidence: IEvidence = { id: 456 };
      const visit: IVisit = { id: 42739 };
      evidence.visit = visit;

      activatedRoute.data = of({ evidence });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(evidence));
      expect(comp.visitsSharedCollection).toContain(visit);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Evidence>>();
      const evidence = { id: 123 };
      jest.spyOn(evidenceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ evidence });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: evidence }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(evidenceService.update).toHaveBeenCalledWith(evidence);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Evidence>>();
      const evidence = new Evidence();
      jest.spyOn(evidenceService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ evidence });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: evidence }));
      saveSubject.complete();

      // THEN
      expect(evidenceService.create).toHaveBeenCalledWith(evidence);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Evidence>>();
      const evidence = { id: 123 };
      jest.spyOn(evidenceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ evidence });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(evidenceService.update).toHaveBeenCalledWith(evidence);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackVisitById', () => {
      it('Should return tracked Visit primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackVisitById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
