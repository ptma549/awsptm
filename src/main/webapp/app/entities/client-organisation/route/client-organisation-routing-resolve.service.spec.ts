import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IClientOrganisation, ClientOrganisation } from '../client-organisation.model';
import { ClientOrganisationService } from '../service/client-organisation.service';

import { ClientOrganisationRoutingResolveService } from './client-organisation-routing-resolve.service';

describe('ClientOrganisation routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: ClientOrganisationRoutingResolveService;
  let service: ClientOrganisationService;
  let resultClientOrganisation: IClientOrganisation | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(ClientOrganisationRoutingResolveService);
    service = TestBed.inject(ClientOrganisationService);
    resultClientOrganisation = undefined;
  });

  describe('resolve', () => {
    it('should return IClientOrganisation returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultClientOrganisation = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultClientOrganisation).toEqual({ id: 123 });
    });

    it('should return new IClientOrganisation if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultClientOrganisation = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultClientOrganisation).toEqual(new ClientOrganisation());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as ClientOrganisation })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultClientOrganisation = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultClientOrganisation).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
