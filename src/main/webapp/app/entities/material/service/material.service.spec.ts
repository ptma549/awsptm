import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IMaterial, Material } from '../material.model';

import { MaterialService } from './material.service';

describe('Material Service', () => {
  let service: MaterialService;
  let httpMock: HttpTestingController;
  let elemDefault: IMaterial;
  let expectedResult: IMaterial | IMaterial[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(MaterialService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      description: 'AAAAAAA',
      quantity: 0,
      unitCost: 0,
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

    it('should create a Material', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Material()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Material', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          description: 'BBBBBB',
          quantity: 1,
          unitCost: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Material', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
          description: 'BBBBBB',
          quantity: 1,
          unitCost: 1,
        },
        new Material()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Material', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          description: 'BBBBBB',
          quantity: 1,
          unitCost: 1,
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

    it('should delete a Material', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addMaterialToCollectionIfMissing', () => {
      it('should add a Material to an empty array', () => {
        const material: IMaterial = { id: 123 };
        expectedResult = service.addMaterialToCollectionIfMissing([], material);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(material);
      });

      it('should not add a Material to an array that contains it', () => {
        const material: IMaterial = { id: 123 };
        const materialCollection: IMaterial[] = [
          {
            ...material,
          },
          { id: 456 },
        ];
        expectedResult = service.addMaterialToCollectionIfMissing(materialCollection, material);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Material to an array that doesn't contain it", () => {
        const material: IMaterial = { id: 123 };
        const materialCollection: IMaterial[] = [{ id: 456 }];
        expectedResult = service.addMaterialToCollectionIfMissing(materialCollection, material);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(material);
      });

      it('should add only unique Material to an array', () => {
        const materialArray: IMaterial[] = [{ id: 123 }, { id: 456 }, { id: 72831 }];
        const materialCollection: IMaterial[] = [{ id: 123 }];
        expectedResult = service.addMaterialToCollectionIfMissing(materialCollection, ...materialArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const material: IMaterial = { id: 123 };
        const material2: IMaterial = { id: 456 };
        expectedResult = service.addMaterialToCollectionIfMissing([], material, material2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(material);
        expect(expectedResult).toContain(material2);
      });

      it('should accept null and undefined values', () => {
        const material: IMaterial = { id: 123 };
        expectedResult = service.addMaterialToCollectionIfMissing([], null, material, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(material);
      });

      it('should return initial array if no Material is added', () => {
        const materialCollection: IMaterial[] = [{ id: 123 }];
        expectedResult = service.addMaterialToCollectionIfMissing(materialCollection, undefined, null);
        expect(expectedResult).toEqual(materialCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
