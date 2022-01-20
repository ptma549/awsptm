import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IAddressComponent, AddressComponent } from '../address-component.model';

import { AddressComponentService } from './address-component.service';

describe('AddressComponent Service', () => {
  let service: AddressComponentService;
  let httpMock: HttpTestingController;
  let elemDefault: IAddressComponent;
  let expectedResult: IAddressComponent | IAddressComponent[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(AddressComponentService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      longName: 'AAAAAAA',
      shortName: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a AddressComponent', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new AddressComponent()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a AddressComponent', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          longName: 'BBBBBB',
          shortName: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a AddressComponent', () => {
      const patchObject = Object.assign(
        {
          longName: 'BBBBBB',
          shortName: 'BBBBBB',
        },
        new AddressComponent()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of AddressComponent', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          longName: 'BBBBBB',
          shortName: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a AddressComponent', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addAddressComponentToCollectionIfMissing', () => {
      it('should add a AddressComponent to an empty array', () => {
        const addressComponent: IAddressComponent = { id: 123 };
        expectedResult = service.addAddressComponentToCollectionIfMissing([], addressComponent);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(addressComponent);
      });

      it('should not add a AddressComponent to an array that contains it', () => {
        const addressComponent: IAddressComponent = { id: 123 };
        const addressComponentCollection: IAddressComponent[] = [
          {
            ...addressComponent,
          },
          { id: 456 },
        ];
        expectedResult = service.addAddressComponentToCollectionIfMissing(addressComponentCollection, addressComponent);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a AddressComponent to an array that doesn't contain it", () => {
        const addressComponent: IAddressComponent = { id: 123 };
        const addressComponentCollection: IAddressComponent[] = [{ id: 456 }];
        expectedResult = service.addAddressComponentToCollectionIfMissing(addressComponentCollection, addressComponent);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(addressComponent);
      });

      it('should add only unique AddressComponent to an array', () => {
        const addressComponentArray: IAddressComponent[] = [{ id: 123 }, { id: 456 }, { id: 24374 }];
        const addressComponentCollection: IAddressComponent[] = [{ id: 123 }];
        expectedResult = service.addAddressComponentToCollectionIfMissing(addressComponentCollection, ...addressComponentArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const addressComponent: IAddressComponent = { id: 123 };
        const addressComponent2: IAddressComponent = { id: 456 };
        expectedResult = service.addAddressComponentToCollectionIfMissing([], addressComponent, addressComponent2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(addressComponent);
        expect(expectedResult).toContain(addressComponent2);
      });

      it('should accept null and undefined values', () => {
        const addressComponent: IAddressComponent = { id: 123 };
        expectedResult = service.addAddressComponentToCollectionIfMissing([], null, addressComponent, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(addressComponent);
      });

      it('should return initial array if no AddressComponent is added', () => {
        const addressComponentCollection: IAddressComponent[] = [{ id: 123 }];
        expectedResult = service.addAddressComponentToCollectionIfMissing(addressComponentCollection, undefined, null);
        expect(expectedResult).toEqual(addressComponentCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
