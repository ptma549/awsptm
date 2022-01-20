import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IAddressType, AddressType } from '../address-type.model';

import { AddressTypeService } from './address-type.service';

describe('AddressType Service', () => {
  let service: AddressTypeService;
  let httpMock: HttpTestingController;
  let elemDefault: IAddressType;
  let expectedResult: IAddressType | IAddressType[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(AddressTypeService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      position: 0,
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

    it('should create a AddressType', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new AddressType()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a AddressType', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          position: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a AddressType', () => {
      const patchObject = Object.assign(
        {
          position: 1,
        },
        new AddressType()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of AddressType', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          position: 1,
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

    it('should delete a AddressType', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addAddressTypeToCollectionIfMissing', () => {
      it('should add a AddressType to an empty array', () => {
        const addressType: IAddressType = { id: 123 };
        expectedResult = service.addAddressTypeToCollectionIfMissing([], addressType);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(addressType);
      });

      it('should not add a AddressType to an array that contains it', () => {
        const addressType: IAddressType = { id: 123 };
        const addressTypeCollection: IAddressType[] = [
          {
            ...addressType,
          },
          { id: 456 },
        ];
        expectedResult = service.addAddressTypeToCollectionIfMissing(addressTypeCollection, addressType);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a AddressType to an array that doesn't contain it", () => {
        const addressType: IAddressType = { id: 123 };
        const addressTypeCollection: IAddressType[] = [{ id: 456 }];
        expectedResult = service.addAddressTypeToCollectionIfMissing(addressTypeCollection, addressType);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(addressType);
      });

      it('should add only unique AddressType to an array', () => {
        const addressTypeArray: IAddressType[] = [{ id: 123 }, { id: 456 }, { id: 2690 }];
        const addressTypeCollection: IAddressType[] = [{ id: 123 }];
        expectedResult = service.addAddressTypeToCollectionIfMissing(addressTypeCollection, ...addressTypeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const addressType: IAddressType = { id: 123 };
        const addressType2: IAddressType = { id: 456 };
        expectedResult = service.addAddressTypeToCollectionIfMissing([], addressType, addressType2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(addressType);
        expect(expectedResult).toContain(addressType2);
      });

      it('should accept null and undefined values', () => {
        const addressType: IAddressType = { id: 123 };
        expectedResult = service.addAddressTypeToCollectionIfMissing([], null, addressType, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(addressType);
      });

      it('should return initial array if no AddressType is added', () => {
        const addressTypeCollection: IAddressType[] = [{ id: 123 }];
        expectedResult = service.addAddressTypeToCollectionIfMissing(addressTypeCollection, undefined, null);
        expect(expectedResult).toEqual(addressTypeCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
