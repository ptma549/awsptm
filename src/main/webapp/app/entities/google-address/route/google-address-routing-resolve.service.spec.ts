import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IGoogleAddress, GoogleAddress } from '../google-address.model';
import { GoogleAddressService } from '../service/google-address.service';

import { GoogleAddressRoutingResolveService } from './google-address-routing-resolve.service';

describe('GoogleAddress routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: GoogleAddressRoutingResolveService;
  let service: GoogleAddressService;
  let resultGoogleAddress: IGoogleAddress | undefined;

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
    routingResolveService = TestBed.inject(GoogleAddressRoutingResolveService);
    service = TestBed.inject(GoogleAddressService);
    resultGoogleAddress = undefined;
  });

  describe('resolve', () => {
    it('should return IGoogleAddress returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultGoogleAddress = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultGoogleAddress).toEqual({ id: 123 });
    });

    it('should return new IGoogleAddress if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultGoogleAddress = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultGoogleAddress).toEqual(new GoogleAddress());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as GoogleAddress })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultGoogleAddress = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultGoogleAddress).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
