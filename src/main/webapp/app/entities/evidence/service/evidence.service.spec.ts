import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IEvidence, Evidence } from '../evidence.model';

import { EvidenceService } from './evidence.service';

describe('Evidence Service', () => {
  let service: EvidenceService;
  let httpMock: HttpTestingController;
  let elemDefault: IEvidence;
  let expectedResult: IEvidence | IEvidence[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(EvidenceService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      url: 'AAAAAAA',
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

    it('should create a Evidence', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Evidence()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Evidence', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          url: 'BBBBBB',
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

    it('should partial update a Evidence', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
          url: 'BBBBBB',
          image: 'BBBBBB',
        },
        new Evidence()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Evidence', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          url: 'BBBBBB',
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

    it('should delete a Evidence', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addEvidenceToCollectionIfMissing', () => {
      it('should add a Evidence to an empty array', () => {
        const evidence: IEvidence = { id: 123 };
        expectedResult = service.addEvidenceToCollectionIfMissing([], evidence);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(evidence);
      });

      it('should not add a Evidence to an array that contains it', () => {
        const evidence: IEvidence = { id: 123 };
        const evidenceCollection: IEvidence[] = [
          {
            ...evidence,
          },
          { id: 456 },
        ];
        expectedResult = service.addEvidenceToCollectionIfMissing(evidenceCollection, evidence);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Evidence to an array that doesn't contain it", () => {
        const evidence: IEvidence = { id: 123 };
        const evidenceCollection: IEvidence[] = [{ id: 456 }];
        expectedResult = service.addEvidenceToCollectionIfMissing(evidenceCollection, evidence);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(evidence);
      });

      it('should add only unique Evidence to an array', () => {
        const evidenceArray: IEvidence[] = [{ id: 123 }, { id: 456 }, { id: 58799 }];
        const evidenceCollection: IEvidence[] = [{ id: 123 }];
        expectedResult = service.addEvidenceToCollectionIfMissing(evidenceCollection, ...evidenceArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const evidence: IEvidence = { id: 123 };
        const evidence2: IEvidence = { id: 456 };
        expectedResult = service.addEvidenceToCollectionIfMissing([], evidence, evidence2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(evidence);
        expect(expectedResult).toContain(evidence2);
      });

      it('should accept null and undefined values', () => {
        const evidence: IEvidence = { id: 123 };
        expectedResult = service.addEvidenceToCollectionIfMissing([], null, evidence, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(evidence);
      });

      it('should return initial array if no Evidence is added', () => {
        const evidenceCollection: IEvidence[] = [{ id: 123 }];
        expectedResult = service.addEvidenceToCollectionIfMissing(evidenceCollection, undefined, null);
        expect(expectedResult).toEqual(evidenceCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
