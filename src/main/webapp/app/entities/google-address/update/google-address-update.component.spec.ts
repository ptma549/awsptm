import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { GoogleAddressService } from '../service/google-address.service';
import { IGoogleAddress, GoogleAddress } from '../google-address.model';

import { GoogleAddressUpdateComponent } from './google-address-update.component';

describe('GoogleAddress Management Update Component', () => {
  let comp: GoogleAddressUpdateComponent;
  let fixture: ComponentFixture<GoogleAddressUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let googleAddressService: GoogleAddressService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [GoogleAddressUpdateComponent],
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
      .overrideTemplate(GoogleAddressUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GoogleAddressUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    googleAddressService = TestBed.inject(GoogleAddressService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const googleAddress: IGoogleAddress = { id: 456 };

      activatedRoute.data = of({ googleAddress });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(googleAddress));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<GoogleAddress>>();
      const googleAddress = { id: 123 };
      jest.spyOn(googleAddressService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ googleAddress });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: googleAddress }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(googleAddressService.update).toHaveBeenCalledWith(googleAddress);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<GoogleAddress>>();
      const googleAddress = new GoogleAddress();
      jest.spyOn(googleAddressService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ googleAddress });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: googleAddress }));
      saveSubject.complete();

      // THEN
      expect(googleAddressService.create).toHaveBeenCalledWith(googleAddress);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<GoogleAddress>>();
      const googleAddress = { id: 123 };
      jest.spyOn(googleAddressService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ googleAddress });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(googleAddressService.update).toHaveBeenCalledWith(googleAddress);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
