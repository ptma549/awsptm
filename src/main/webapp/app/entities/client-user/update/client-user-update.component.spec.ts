import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ClientUserService } from '../service/client-user.service';
import { IClientUser, ClientUser } from '../client-user.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IClientOrganisation } from 'app/entities/client-organisation/client-organisation.model';
import { ClientOrganisationService } from 'app/entities/client-organisation/service/client-organisation.service';

import { ClientUserUpdateComponent } from './client-user-update.component';

describe('ClientUser Management Update Component', () => {
  let comp: ClientUserUpdateComponent;
  let fixture: ComponentFixture<ClientUserUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let clientUserService: ClientUserService;
  let userService: UserService;
  let clientOrganisationService: ClientOrganisationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ClientUserUpdateComponent],
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
      .overrideTemplate(ClientUserUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ClientUserUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    clientUserService = TestBed.inject(ClientUserService);
    userService = TestBed.inject(UserService);
    clientOrganisationService = TestBed.inject(ClientOrganisationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const clientUser: IClientUser = { id: 456 };
      const user: IUser = { id: 75396 };
      clientUser.user = user;

      const userCollection: IUser[] = [{ id: 39424 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ clientUser });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call ClientOrganisation query and add missing value', () => {
      const clientUser: IClientUser = { id: 456 };
      const client: IClientOrganisation = { id: 44493 };
      clientUser.client = client;

      const clientOrganisationCollection: IClientOrganisation[] = [{ id: 88556 }];
      jest.spyOn(clientOrganisationService, 'query').mockReturnValue(of(new HttpResponse({ body: clientOrganisationCollection })));
      const additionalClientOrganisations = [client];
      const expectedCollection: IClientOrganisation[] = [...additionalClientOrganisations, ...clientOrganisationCollection];
      jest.spyOn(clientOrganisationService, 'addClientOrganisationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ clientUser });
      comp.ngOnInit();

      expect(clientOrganisationService.query).toHaveBeenCalled();
      expect(clientOrganisationService.addClientOrganisationToCollectionIfMissing).toHaveBeenCalledWith(
        clientOrganisationCollection,
        ...additionalClientOrganisations
      );
      expect(comp.clientOrganisationsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const clientUser: IClientUser = { id: 456 };
      const user: IUser = { id: 9589 };
      clientUser.user = user;
      const client: IClientOrganisation = { id: 22442 };
      clientUser.client = client;

      activatedRoute.data = of({ clientUser });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(clientUser));
      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.clientOrganisationsSharedCollection).toContain(client);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ClientUser>>();
      const clientUser = { id: 123 };
      jest.spyOn(clientUserService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ clientUser });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: clientUser }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(clientUserService.update).toHaveBeenCalledWith(clientUser);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ClientUser>>();
      const clientUser = new ClientUser();
      jest.spyOn(clientUserService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ clientUser });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: clientUser }));
      saveSubject.complete();

      // THEN
      expect(clientUserService.create).toHaveBeenCalledWith(clientUser);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ClientUser>>();
      const clientUser = { id: 123 };
      jest.spyOn(clientUserService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ clientUser });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(clientUserService.update).toHaveBeenCalledWith(clientUser);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackUserById', () => {
      it('Should return tracked User primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackUserById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackClientOrganisationById', () => {
      it('Should return tracked ClientOrganisation primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackClientOrganisationById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
