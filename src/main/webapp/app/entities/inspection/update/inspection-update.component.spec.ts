import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { InspectionService } from '../service/inspection.service';
import { IInspection, Inspection } from '../inspection.model';
import { IJob } from 'app/entities/job/job.model';
import { JobService } from 'app/entities/job/service/job.service';
import { IClientUser } from 'app/entities/client-user/client-user.model';
import { ClientUserService } from 'app/entities/client-user/service/client-user.service';
import { IAddress } from 'app/entities/address/address.model';
import { AddressService } from 'app/entities/address/service/address.service';

import { InspectionUpdateComponent } from './inspection-update.component';

describe('Inspection Management Update Component', () => {
  let comp: InspectionUpdateComponent;
  let fixture: ComponentFixture<InspectionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let inspectionService: InspectionService;
  let jobService: JobService;
  let clientUserService: ClientUserService;
  let addressService: AddressService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [InspectionUpdateComponent],
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
      .overrideTemplate(InspectionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(InspectionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    inspectionService = TestBed.inject(InspectionService);
    jobService = TestBed.inject(JobService);
    clientUserService = TestBed.inject(ClientUserService);
    addressService = TestBed.inject(AddressService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Job query and add missing value', () => {
      const inspection: IInspection = { id: 456 };
      const jobs: IJob = { id: 4051 };
      inspection.jobs = jobs;

      const jobCollection: IJob[] = [{ id: 44899 }];
      jest.spyOn(jobService, 'query').mockReturnValue(of(new HttpResponse({ body: jobCollection })));
      const additionalJobs = [jobs];
      const expectedCollection: IJob[] = [...additionalJobs, ...jobCollection];
      jest.spyOn(jobService, 'addJobToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ inspection });
      comp.ngOnInit();

      expect(jobService.query).toHaveBeenCalled();
      expect(jobService.addJobToCollectionIfMissing).toHaveBeenCalledWith(jobCollection, ...additionalJobs);
      expect(comp.jobsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call ClientUser query and add missing value', () => {
      const inspection: IInspection = { id: 456 };
      const createdBy: IClientUser = { id: 77702 };
      inspection.createdBy = createdBy;

      const clientUserCollection: IClientUser[] = [{ id: 55497 }];
      jest.spyOn(clientUserService, 'query').mockReturnValue(of(new HttpResponse({ body: clientUserCollection })));
      const additionalClientUsers = [createdBy];
      const expectedCollection: IClientUser[] = [...additionalClientUsers, ...clientUserCollection];
      jest.spyOn(clientUserService, 'addClientUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ inspection });
      comp.ngOnInit();

      expect(clientUserService.query).toHaveBeenCalled();
      expect(clientUserService.addClientUserToCollectionIfMissing).toHaveBeenCalledWith(clientUserCollection, ...additionalClientUsers);
      expect(comp.clientUsersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Address query and add missing value', () => {
      const inspection: IInspection = { id: 456 };
      const address: IAddress = { id: 47519 };
      inspection.address = address;

      const addressCollection: IAddress[] = [{ id: 14087 }];
      jest.spyOn(addressService, 'query').mockReturnValue(of(new HttpResponse({ body: addressCollection })));
      const additionalAddresses = [address];
      const expectedCollection: IAddress[] = [...additionalAddresses, ...addressCollection];
      jest.spyOn(addressService, 'addAddressToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ inspection });
      comp.ngOnInit();

      expect(addressService.query).toHaveBeenCalled();
      expect(addressService.addAddressToCollectionIfMissing).toHaveBeenCalledWith(addressCollection, ...additionalAddresses);
      expect(comp.addressesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const inspection: IInspection = { id: 456 };
      const jobs: IJob = { id: 1231 };
      inspection.jobs = jobs;
      const createdBy: IClientUser = { id: 54015 };
      inspection.createdBy = createdBy;
      const address: IAddress = { id: 52789 };
      inspection.address = address;

      activatedRoute.data = of({ inspection });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(inspection));
      expect(comp.jobsSharedCollection).toContain(jobs);
      expect(comp.clientUsersSharedCollection).toContain(createdBy);
      expect(comp.addressesSharedCollection).toContain(address);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Inspection>>();
      const inspection = { id: 123 };
      jest.spyOn(inspectionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inspection });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: inspection }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(inspectionService.update).toHaveBeenCalledWith(inspection);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Inspection>>();
      const inspection = new Inspection();
      jest.spyOn(inspectionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inspection });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: inspection }));
      saveSubject.complete();

      // THEN
      expect(inspectionService.create).toHaveBeenCalledWith(inspection);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Inspection>>();
      const inspection = { id: 123 };
      jest.spyOn(inspectionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inspection });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(inspectionService.update).toHaveBeenCalledWith(inspection);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackJobById', () => {
      it('Should return tracked Job primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackJobById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackClientUserById', () => {
      it('Should return tracked ClientUser primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackClientUserById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackAddressById', () => {
      it('Should return tracked Address primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackAddressById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
