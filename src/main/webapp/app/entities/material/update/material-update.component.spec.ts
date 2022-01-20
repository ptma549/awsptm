import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { MaterialService } from '../service/material.service';
import { IMaterial, Material } from '../material.model';
import { IVisit } from 'app/entities/visit/visit.model';
import { VisitService } from 'app/entities/visit/service/visit.service';

import { MaterialUpdateComponent } from './material-update.component';

describe('Material Management Update Component', () => {
  let comp: MaterialUpdateComponent;
  let fixture: ComponentFixture<MaterialUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let materialService: MaterialService;
  let visitService: VisitService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [MaterialUpdateComponent],
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
      .overrideTemplate(MaterialUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MaterialUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    materialService = TestBed.inject(MaterialService);
    visitService = TestBed.inject(VisitService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Visit query and add missing value', () => {
      const material: IMaterial = { id: 456 };
      const visit: IVisit = { id: 33797 };
      material.visit = visit;

      const visitCollection: IVisit[] = [{ id: 10360 }];
      jest.spyOn(visitService, 'query').mockReturnValue(of(new HttpResponse({ body: visitCollection })));
      const additionalVisits = [visit];
      const expectedCollection: IVisit[] = [...additionalVisits, ...visitCollection];
      jest.spyOn(visitService, 'addVisitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ material });
      comp.ngOnInit();

      expect(visitService.query).toHaveBeenCalled();
      expect(visitService.addVisitToCollectionIfMissing).toHaveBeenCalledWith(visitCollection, ...additionalVisits);
      expect(comp.visitsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const material: IMaterial = { id: 456 };
      const visit: IVisit = { id: 78402 };
      material.visit = visit;

      activatedRoute.data = of({ material });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(material));
      expect(comp.visitsSharedCollection).toContain(visit);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Material>>();
      const material = { id: 123 };
      jest.spyOn(materialService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ material });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: material }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(materialService.update).toHaveBeenCalledWith(material);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Material>>();
      const material = new Material();
      jest.spyOn(materialService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ material });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: material }));
      saveSubject.complete();

      // THEN
      expect(materialService.create).toHaveBeenCalledWith(material);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Material>>();
      const material = { id: 123 };
      jest.spyOn(materialService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ material });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(materialService.update).toHaveBeenCalledWith(material);
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
