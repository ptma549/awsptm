import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ClientOrganisationService } from '../service/client-organisation.service';
import { IClientOrganisation, ClientOrganisation } from '../client-organisation.model';

import { ClientOrganisationUpdateComponent } from './client-organisation-update.component';

describe('ClientOrganisation Management Update Component', () => {
  let comp: ClientOrganisationUpdateComponent;
  let fixture: ComponentFixture<ClientOrganisationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let clientOrganisationService: ClientOrganisationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ClientOrganisationUpdateComponent],
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
      .overrideTemplate(ClientOrganisationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ClientOrganisationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    clientOrganisationService = TestBed.inject(ClientOrganisationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const clientOrganisation: IClientOrganisation = { id: 456 };

      activatedRoute.data = of({ clientOrganisation });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(clientOrganisation));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ClientOrganisation>>();
      const clientOrganisation = { id: 123 };
      jest.spyOn(clientOrganisationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ clientOrganisation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: clientOrganisation }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(clientOrganisationService.update).toHaveBeenCalledWith(clientOrganisation);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ClientOrganisation>>();
      const clientOrganisation = new ClientOrganisation();
      jest.spyOn(clientOrganisationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ clientOrganisation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: clientOrganisation }));
      saveSubject.complete();

      // THEN
      expect(clientOrganisationService.create).toHaveBeenCalledWith(clientOrganisation);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ClientOrganisation>>();
      const clientOrganisation = { id: 123 };
      jest.spyOn(clientOrganisationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ clientOrganisation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(clientOrganisationService.update).toHaveBeenCalledWith(clientOrganisation);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
