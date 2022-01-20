import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICertificate, Certificate } from '../certificate.model';

import { CertificateService } from './certificate.service';

describe('Certificate Service', () => {
  let service: CertificateService;
  let httpMock: HttpTestingController;
  let elemDefault: ICertificate;
  let expectedResult: ICertificate | ICertificate[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CertificateService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      imageContentType: 'image/png',
      image: 'AAAAAAA',
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

    it('should create a Certificate', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Certificate()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Certificate', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          image: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Certificate', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
          image: 'BBBBBB',
        },
        new Certificate()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Certificate', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          image: 'BBBBBB',
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

    it('should delete a Certificate', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addCertificateToCollectionIfMissing', () => {
      it('should add a Certificate to an empty array', () => {
        const certificate: ICertificate = { id: 123 };
        expectedResult = service.addCertificateToCollectionIfMissing([], certificate);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(certificate);
      });

      it('should not add a Certificate to an array that contains it', () => {
        const certificate: ICertificate = { id: 123 };
        const certificateCollection: ICertificate[] = [
          {
            ...certificate,
          },
          { id: 456 },
        ];
        expectedResult = service.addCertificateToCollectionIfMissing(certificateCollection, certificate);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Certificate to an array that doesn't contain it", () => {
        const certificate: ICertificate = { id: 123 };
        const certificateCollection: ICertificate[] = [{ id: 456 }];
        expectedResult = service.addCertificateToCollectionIfMissing(certificateCollection, certificate);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(certificate);
      });

      it('should add only unique Certificate to an array', () => {
        const certificateArray: ICertificate[] = [{ id: 123 }, { id: 456 }, { id: 63464 }];
        const certificateCollection: ICertificate[] = [{ id: 123 }];
        expectedResult = service.addCertificateToCollectionIfMissing(certificateCollection, ...certificateArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const certificate: ICertificate = { id: 123 };
        const certificate2: ICertificate = { id: 456 };
        expectedResult = service.addCertificateToCollectionIfMissing([], certificate, certificate2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(certificate);
        expect(expectedResult).toContain(certificate2);
      });

      it('should accept null and undefined values', () => {
        const certificate: ICertificate = { id: 123 };
        expectedResult = service.addCertificateToCollectionIfMissing([], null, certificate, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(certificate);
      });

      it('should return initial array if no Certificate is added', () => {
        const certificateCollection: ICertificate[] = [{ id: 123 }];
        expectedResult = service.addCertificateToCollectionIfMissing(certificateCollection, undefined, null);
        expect(expectedResult).toEqual(certificateCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
