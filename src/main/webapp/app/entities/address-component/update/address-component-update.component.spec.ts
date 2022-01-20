import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { AddressComponentService } from '../service/address-component.service';
import { IAddressComponent, AddressComponent } from '../address-component.model';
import { IGoogleAddress } from 'app/entities/google-address/google-address.model';
import { GoogleAddressService } from 'app/entities/google-address/service/google-address.service';
import { IAddressType } from 'app/entities/address-type/address-type.model';
import { AddressTypeService } from 'app/entities/address-type/service/address-type.service';

import { AddressComponentUpdateComponent } from './address-component-update.component';

describe('AddressComponent Management Update Component', () => {
  let comp: AddressComponentUpdateComponent;
  let fixture: ComponentFixture<AddressComponentUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let addressComponentService: AddressComponentService;
  let googleAddressService: GoogleAddressService;
  let addressTypeService: AddressTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [AddressComponentUpdateComponent],
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
      .overrideTemplate(AddressComponentUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AddressComponentUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    addressComponentService = TestBed.inject(AddressComponentService);
    googleAddressService = TestBed.inject(GoogleAddressService);
    addressTypeService = TestBed.inject(AddressTypeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call GoogleAddress query and add missing value', () => {
      const addressComponent: IAddressComponent = { id: 456 };
      const address: IGoogleAddress = { id: 75191 };
      addressComponent.address = address;

      const googleAddressCollection: IGoogleAddress[] = [{ id: 63623 }];
      jest.spyOn(googleAddressService, 'query').mockReturnValue(of(new HttpResponse({ body: googleAddressCollection })));
      const additionalGoogleAddresses = [address];
      const expectedCollection: IGoogleAddress[] = [...additionalGoogleAddresses, ...googleAddressCollection];
      jest.spyOn(googleAddressService, 'addGoogleAddressToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ addressComponent });
      comp.ngOnInit();

      expect(googleAddressService.query).toHaveBeenCalled();
      expect(googleAddressService.addGoogleAddressToCollectionIfMissing).toHaveBeenCalledWith(
        googleAddressCollection,
        ...additionalGoogleAddresses
      );
      expect(comp.googleAddressesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call AddressType query and add missing value', () => {
      const addressComponent: IAddressComponent = { id: 456 };
      const type: IAddressType = { id: 14526 };
      addressComponent.type = type;

      const addressTypeCollection: IAddressType[] = [{ id: 87940 }];
      jest.spyOn(addressTypeService, 'query').mockReturnValue(of(new HttpResponse({ body: addressTypeCollection })));
      const additionalAddressTypes = [type];
      const expectedCollection: IAddressType[] = [...additionalAddressTypes, ...addressTypeCollection];
      jest.spyOn(addressTypeService, 'addAddressTypeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ addressComponent });
      comp.ngOnInit();

      expect(addressTypeService.query).toHaveBeenCalled();
      expect(addressTypeService.addAddressTypeToCollectionIfMissing).toHaveBeenCalledWith(addressTypeCollection, ...additionalAddressTypes);
      expect(comp.addressTypesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const addressComponent: IAddressComponent = { id: 456 };
      const address: IGoogleAddress = { id: 49226 };
      addressComponent.address = address;
      const type: IAddressType = { id: 36366 };
      addressComponent.type = type;

      activatedRoute.data = of({ addressComponent });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(addressComponent));
      expect(comp.googleAddressesSharedCollection).toContain(address);
      expect(comp.addressTypesSharedCollection).toContain(type);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<AddressComponent>>();
      const addressComponent = { id: 123 };
      jest.spyOn(addressComponentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ addressComponent });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: addressComponent }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(addressComponentService.update).toHaveBeenCalledWith(addressComponent);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<AddressComponent>>();
      const addressComponent = new AddressComponent();
      jest.spyOn(addressComponentService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ addressComponent });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: addressComponent }));
      saveSubject.complete();

      // THEN
      expect(addressComponentService.create).toHaveBeenCalledWith(addressComponent);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<AddressComponent>>();
      const addressComponent = { id: 123 };
      jest.spyOn(addressComponentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ addressComponent });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(addressComponentService.update).toHaveBeenCalledWith(addressComponent);
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

    describe('trackAddressTypeById', () => {
      it('Should return tracked AddressType primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackAddressTypeById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
