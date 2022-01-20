import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CertificateService } from '../service/certificate.service';
import { ICertificate, Certificate } from '../certificate.model';
import { IVisit } from 'app/entities/visit/visit.model';
import { VisitService } from 'app/entities/visit/service/visit.service';

import { CertificateUpdateComponent } from './certificate-update.component';

describe('Certificate Management Update Component', () => {
  let comp: CertificateUpdateComponent;
  let fixture: ComponentFixture<CertificateUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let certificateService: CertificateService;
  let visitService: VisitService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CertificateUpdateComponent],
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
      .overrideTemplate(CertificateUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CertificateUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    certificateService = TestBed.inject(CertificateService);
    visitService = TestBed.inject(VisitService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Visit query and add missing value', () => {
      const certificate: ICertificate = { id: 456 };
      const visit: IVisit = { id: 1828 };
      certificate.visit = visit;

      const visitCollection: IVisit[] = [{ id: 19607 }];
      jest.spyOn(visitService, 'query').mockReturnValue(of(new HttpResponse({ body: visitCollection })));
      const additionalVisits = [visit];
      const expectedCollection: IVisit[] = [...additionalVisits, ...visitCollection];
      jest.spyOn(visitService, 'addVisitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ certificate });
      comp.ngOnInit();

      expect(visitService.query).toHaveBeenCalled();
      expect(visitService.addVisitToCollectionIfMissing).toHaveBeenCalledWith(visitCollection, ...additionalVisits);
      expect(comp.visitsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const certificate: ICertificate = { id: 456 };
      const visit: IVisit = { id: 16042 };
      certificate.visit = visit;

      activatedRoute.data = of({ certificate });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(certificate));
      expect(comp.visitsSharedCollection).toContain(visit);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Certificate>>();
      const certificate = { id: 123 };
      jest.spyOn(certificateService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ certificate });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: certificate }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(certificateService.update).toHaveBeenCalledWith(certificate);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Certificate>>();
      const certificate = new Certificate();
      jest.spyOn(certificateService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ certificate });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: certificate }));
      saveSubject.complete();

      // THEN
      expect(certificateService.create).toHaveBeenCalledWith(certificate);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Certificate>>();
      const certificate = { id: 123 };
      jest.spyOn(certificateService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ certificate });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(certificateService.update).toHaveBeenCalledWith(certificate);
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
