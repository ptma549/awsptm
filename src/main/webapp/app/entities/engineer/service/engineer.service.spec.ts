import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IEngineer, Engineer } from '../engineer.model';

import { EngineerService } from './engineer.service';

describe('Engineer Service', () => {
  let service: EngineerService;
  let httpMock: HttpTestingController;
  let elemDefault: IEngineer;
  let expectedResult: IEngineer | IEngineer[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(EngineerService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      firstname: 'AAAAAAA',
      lastname: 'AAAAAAA',
      email: 'AAAAAAA',
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

    it('should create a Engineer', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Engineer()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Engineer', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          firstname: 'BBBBBB',
          lastname: 'BBBBBB',
          email: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Engineer', () => {
      const patchObject = Object.assign(
        {
          firstname: 'BBBBBB',
        },
        new Engineer()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Engineer', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          firstname: 'BBBBBB',
          lastname: 'BBBBBB',
          email: 'BBBBBB',
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

    it('should delete a Engineer', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addEngineerToCollectionIfMissing', () => {
      it('should add a Engineer to an empty array', () => {
        const engineer: IEngineer = { id: 123 };
        expectedResult = service.addEngineerToCollectionIfMissing([], engineer);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(engineer);
      });

      it('should not add a Engineer to an array that contains it', () => {
        const engineer: IEngineer = { id: 123 };
        const engineerCollection: IEngineer[] = [
          {
            ...engineer,
          },
          { id: 456 },
        ];
        expectedResult = service.addEngineerToCollectionIfMissing(engineerCollection, engineer);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Engineer to an array that doesn't contain it", () => {
        const engineer: IEngineer = { id: 123 };
        const engineerCollection: IEngineer[] = [{ id: 456 }];
        expectedResult = service.addEngineerToCollectionIfMissing(engineerCollection, engineer);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(engineer);
      });

      it('should add only unique Engineer to an array', () => {
        const engineerArray: IEngineer[] = [{ id: 123 }, { id: 456 }, { id: 42622 }];
        const engineerCollection: IEngineer[] = [{ id: 123 }];
        expectedResult = service.addEngineerToCollectionIfMissing(engineerCollection, ...engineerArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const engineer: IEngineer = { id: 123 };
        const engineer2: IEngineer = { id: 456 };
        expectedResult = service.addEngineerToCollectionIfMissing([], engineer, engineer2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(engineer);
        expect(expectedResult).toContain(engineer2);
      });

      it('should accept null and undefined values', () => {
        const engineer: IEngineer = { id: 123 };
        expectedResult = service.addEngineerToCollectionIfMissing([], null, engineer, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(engineer);
      });

      it('should return initial array if no Engineer is added', () => {
        const engineerCollection: IEngineer[] = [{ id: 123 }];
        expectedResult = service.addEngineerToCollectionIfMissing(engineerCollection, undefined, null);
        expect(expectedResult).toEqual(engineerCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
