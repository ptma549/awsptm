import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IGoogleAddress, GoogleAddress } from '../google-address.model';

import { GoogleAddressService } from './google-address.service';

describe('GoogleAddress Service', () => {
  let service: GoogleAddressService;
  let httpMock: HttpTestingController;
  let elemDefault: IGoogleAddress;
  let expectedResult: IGoogleAddress | IGoogleAddress[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(GoogleAddressService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      position: 'AAAAAAA',
      url: 'AAAAAAA',
      html: 'AAAAAAA',
      formatted: 'AAAAAAA',
      types: 'AAAAAAA',
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

    it('should create a GoogleAddress', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new GoogleAddress()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a GoogleAddress', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          position: 'BBBBBB',
          url: 'BBBBBB',
          html: 'BBBBBB',
          formatted: 'BBBBBB',
          types: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a GoogleAddress', () => {
      const patchObject = Object.assign(
        {
          position: 'BBBBBB',
          url: 'BBBBBB',
          html: 'BBBBBB',
          formatted: 'BBBBBB',
          types: 'BBBBBB',
        },
        new GoogleAddress()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of GoogleAddress', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          position: 'BBBBBB',
          url: 'BBBBBB',
          html: 'BBBBBB',
          formatted: 'BBBBBB',
          types: 'BBBBBB',
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

    it('should delete a GoogleAddress', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addGoogleAddressToCollectionIfMissing', () => {
      it('should add a GoogleAddress to an empty array', () => {
        const googleAddress: IGoogleAddress = { id: 123 };
        expectedResult = service.addGoogleAddressToCollectionIfMissing([], googleAddress);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(googleAddress);
      });

      it('should not add a GoogleAddress to an array that contains it', () => {
        const googleAddress: IGoogleAddress = { id: 123 };
        const googleAddressCollection: IGoogleAddress[] = [
          {
            ...googleAddress,
          },
          { id: 456 },
        ];
        expectedResult = service.addGoogleAddressToCollectionIfMissing(googleAddressCollection, googleAddress);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a GoogleAddress to an array that doesn't contain it", () => {
        const googleAddress: IGoogleAddress = { id: 123 };
        const googleAddressCollection: IGoogleAddress[] = [{ id: 456 }];
        expectedResult = service.addGoogleAddressToCollectionIfMissing(googleAddressCollection, googleAddress);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(googleAddress);
      });

      it('should add only unique GoogleAddress to an array', () => {
        const googleAddressArray: IGoogleAddress[] = [{ id: 123 }, { id: 456 }, { id: 82609 }];
        const googleAddressCollection: IGoogleAddress[] = [{ id: 123 }];
        expectedResult = service.addGoogleAddressToCollectionIfMissing(googleAddressCollection, ...googleAddressArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const googleAddress: IGoogleAddress = { id: 123 };
        const googleAddress2: IGoogleAddress = { id: 456 };
        expectedResult = service.addGoogleAddressToCollectionIfMissing([], googleAddress, googleAddress2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(googleAddress);
        expect(expectedResult).toContain(googleAddress2);
      });

      it('should accept null and undefined values', () => {
        const googleAddress: IGoogleAddress = { id: 123 };
        expectedResult = service.addGoogleAddressToCollectionIfMissing([], null, googleAddress, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(googleAddress);
      });

      it('should return initial array if no GoogleAddress is added', () => {
        const googleAddressCollection: IGoogleAddress[] = [{ id: 123 }];
        expectedResult = service.addGoogleAddressToCollectionIfMissing(googleAddressCollection, undefined, null);
        expect(expectedResult).toEqual(googleAddressCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
