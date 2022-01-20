import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { AddressService } from '../service/address.service';
import { IAddress, Address } from '../address.model';
import { IGoogleAddress } from 'app/entities/google-address/google-address.model';
import { GoogleAddressService } from 'app/entities/google-address/service/google-address.service';

import { AddressUpdateComponent } from './address-update.component';

describe('Address Management Update Component', () => {
  let comp: AddressUpdateComponent;
  let fixture: ComponentFixture<AddressUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let addressService: AddressService;
  let googleAddressService: GoogleAddressService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [AddressUpdateComponent],
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
      .overrideTemplate(AddressUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AddressUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    addressService = TestBed.inject(AddressService);
    googleAddressService = TestBed.inject(GoogleAddressService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call googleAddress query and add missing value', () => {
      const address: IAddress = { id: 456 };
      const googleAddress: IGoogleAddress = { id: 79637 };
      address.googleAddress = googleAddress;

      const googleAddressCollection: IGoogleAddress[] = [{ id: 74340 }];
      jest.spyOn(googleAddressService, 'query').mockReturnValue(of(new HttpResponse({ body: googleAddressCollection })));
      const expectedCollection: IGoogleAddress[] = [googleAddress, ...googleAddressCollection];
      jest.spyOn(googleAddressService, 'addGoogleAddressToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ address });
      comp.ngOnInit();

      expect(googleAddressService.query).toHaveBeenCalled();
      expect(googleAddressService.addGoogleAddressToCollectionIfMissing).toHaveBeenCalledWith(googleAddressCollection, googleAddress);
      expect(comp.googleAddressesCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const address: IAddress = { id: 456 };
      const googleAddress: IGoogleAddress = { id: 61643 };
      address.googleAddress = googleAddress;

      activatedRoute.data = of({ address });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(address));
      expect(comp.googleAddressesCollection).toContain(googleAddress);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Address>>();
      const address = { id: 123 };
      jest.spyOn(addressService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ address });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: address }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(addressService.update).toHaveBeenCalledWith(address);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Address>>();
      const address = new Address();
      jest.spyOn(addressService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ address });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: address }));
      saveSubject.complete();

      // THEN
      expect(addressService.create).toHaveBeenCalledWith(address);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Address>>();
      const address = { id: 123 };
      jest.spyOn(addressService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ address });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(addressService.update).toHaveBeenCalledWith(address);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackGoogleAddressById', () => {
      it('Should return tracked GoogleAddress primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackGoogleAddressById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
