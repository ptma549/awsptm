import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { AddressTypeService } from '../service/address-type.service';
import { IAddressType, AddressType } from '../address-type.model';

import { AddressTypeUpdateComponent } from './address-type-update.component';

describe('AddressType Management Update Component', () => {
  let comp: AddressTypeUpdateComponent;
  let fixture: ComponentFixture<AddressTypeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let addressTypeService: AddressTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [AddressTypeUpdateComponent],
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
      .overrideTemplate(AddressTypeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AddressTypeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    addressTypeService = TestBed.inject(AddressTypeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const addressType: IAddressType = { id: 456 };

      activatedRoute.data = of({ addressType });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(addressType));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<AddressType>>();
      const addressType = { id: 123 };
      jest.spyOn(addressTypeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ addressType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: addressType }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(addressTypeService.update).toHaveBeenCalledWith(addressType);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<AddressType>>();
      const addressType = new AddressType();
      jest.spyOn(addressTypeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ addressType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: addressType }));
      saveSubject.complete();

      // THEN
      expect(addressTypeService.create).toHaveBeenCalledWith(addressType);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<AddressType>>();
      const addressType = { id: 123 };
      jest.spyOn(addressTypeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ addressType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(addressTypeService.update).toHaveBeenCalledWith(addressType);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
