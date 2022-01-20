import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { JobService } from '../service/job.service';
import { IJob, Job } from '../job.model';
import { IClientUser } from 'app/entities/client-user/client-user.model';
import { ClientUserService } from 'app/entities/client-user/service/client-user.service';
import { IEngineer } from 'app/entities/engineer/engineer.model';
import { EngineerService } from 'app/entities/engineer/service/engineer.service';
import { IAddress } from 'app/entities/address/address.model';
import { AddressService } from 'app/entities/address/service/address.service';

import { JobUpdateComponent } from './job-update.component';

describe('Job Management Update Component', () => {
  let comp: JobUpdateComponent;
  let fixture: ComponentFixture<JobUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let jobService: JobService;
  let clientUserService: ClientUserService;
  let engineerService: EngineerService;
  let addressService: AddressService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [JobUpdateComponent],
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
      .overrideTemplate(JobUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(JobUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    jobService = TestBed.inject(JobService);
    clientUserService = TestBed.inject(ClientUserService);
    engineerService = TestBed.inject(EngineerService);
    addressService = TestBed.inject(AddressService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call ClientUser query and add missing value', () => {
      const job: IJob = { id: 456 };
      const createdBy: IClientUser = { id: 21519 };
      job.createdBy = createdBy;

      const clientUserCollection: IClientUser[] = [{ id: 81175 }];
      jest.spyOn(clientUserService, 'query').mockReturnValue(of(new HttpResponse({ body: clientUserCollection })));
      const additionalClientUsers = [createdBy];
      const expectedCollection: IClientUser[] = [...additionalClientUsers, ...clientUserCollection];
      jest.spyOn(clientUserService, 'addClientUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ job });
      comp.ngOnInit();

      expect(clientUserService.query).toHaveBeenCalled();
      expect(clientUserService.addClientUserToCollectionIfMissing).toHaveBeenCalledWith(clientUserCollection, ...additionalClientUsers);
      expect(comp.clientUsersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Engineer query and add missing value', () => {
      const job: IJob = { id: 456 };
      const assignedTo: IEngineer = { id: 27444 };
      job.assignedTo = assignedTo;

      const engineerCollection: IEngineer[] = [{ id: 87878 }];
      jest.spyOn(engineerService, 'query').mockReturnValue(of(new HttpResponse({ body: engineerCollection })));
      const additionalEngineers = [assignedTo];
      const expectedCollection: IEngineer[] = [...additionalEngineers, ...engineerCollection];
      jest.spyOn(engineerService, 'addEngineerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ job });
      comp.ngOnInit();

      expect(engineerService.query).toHaveBeenCalled();
      expect(engineerService.addEngineerToCollectionIfMissing).toHaveBeenCalledWith(engineerCollection, ...additionalEngineers);
      expect(comp.engineersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Address query and add missing value', () => {
      const job: IJob = { id: 456 };
      const address: IAddress = { id: 74283 };
      job.address = address;

      const addressCollection: IAddress[] = [{ id: 11750 }];
      jest.spyOn(addressService, 'query').mockReturnValue(of(new HttpResponse({ body: addressCollection })));
      const additionalAddresses = [address];
      const expectedCollection: IAddress[] = [...additionalAddresses, ...addressCollection];
      jest.spyOn(addressService, 'addAddressToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ job });
      comp.ngOnInit();

      expect(addressService.query).toHaveBeenCalled();
      expect(addressService.addAddressToCollectionIfMissing).toHaveBeenCalledWith(addressCollection, ...additionalAddresses);
      expect(comp.addressesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const job: IJob = { id: 456 };
      const createdBy: IClientUser = { id: 29338 };
      job.createdBy = createdBy;
      const assignedTo: IEngineer = { id: 33888 };
      job.assignedTo = assignedTo;
      const address: IAddress = { id: 47472 };
      job.address = address;

      activatedRoute.data = of({ job });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(job));
      expect(comp.clientUsersSharedCollection).toContain(createdBy);
      expect(comp.engineersSharedCollection).toContain(assignedTo);
      expect(comp.addressesSharedCollection).toContain(address);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Job>>();
      const job = { id: 123 };
      jest.spyOn(jobService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ job });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: job }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(jobService.update).toHaveBeenCalledWith(job);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Job>>();
      const job = new Job();
      jest.spyOn(jobService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ job });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: job }));
      saveSubject.complete();

      // THEN
      expect(jobService.create).toHaveBeenCalledWith(job);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Job>>();
      const job = { id: 123 };
      jest.spyOn(jobService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ job });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(jobService.update).toHaveBeenCalledWith(job);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackClientUserById', () => {
      it('Should return tracked ClientUser primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackClientUserById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackEngineerById', () => {
      it('Should return tracked Engineer primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackEngineerById(0, entity);
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
