import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IAddressComponent, AddressComponent } from '../address-component.model';
import { AddressComponentService } from '../service/address-component.service';

import { AddressComponentRoutingResolveService } from './address-component-routing-resolve.service';

describe('AddressComponent routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: AddressComponentRoutingResolveService;
  let service: AddressComponentService;
  let resultAddressComponent: IAddressComponent | undefined;

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
    routingResolveService = TestBed.inject(AddressComponentRoutingResolveService);
    service = TestBed.inject(AddressComponentService);
    resultAddressComponent = undefined;
  });

  describe('resolve', () => {
    it('should return IAddressComponent returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultAddressComponent = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultAddressComponent).toEqual({ id: 123 });
    });

    it('should return new IAddressComponent if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultAddressComponent = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultAddressComponent).toEqual(new AddressComponent());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as AddressComponent })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultAddressComponent = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultAddressComponent).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
