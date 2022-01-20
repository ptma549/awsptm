import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IClientOrganisation, ClientOrganisation } from '../client-organisation.model';

import { ClientOrganisationService } from './client-organisation.service';

describe('ClientOrganisation Service', () => {
  let service: ClientOrganisationService;
  let httpMock: HttpTestingController;
  let elemDefault: IClientOrganisation;
  let expectedResult: IClientOrganisation | IClientOrganisation[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ClientOrganisationService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      domain: 'AAAAAAA',
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

    it('should create a ClientOrganisation', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new ClientOrganisation()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ClientOrganisation', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          domain: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ClientOrganisation', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
          domain: 'BBBBBB',
        },
        new ClientOrganisation()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ClientOrganisation', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          domain: 'BBBBBB',
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

    it('should delete a ClientOrganisation', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addClientOrganisationToCollectionIfMissing', () => {
      it('should add a ClientOrganisation to an empty array', () => {
        const clientOrganisation: IClientOrganisation = { id: 123 };
        expectedResult = service.addClientOrganisationToCollectionIfMissing([], clientOrganisation);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(clientOrganisation);
      });

      it('should not add a ClientOrganisation to an array that contains it', () => {
        const clientOrganisation: IClientOrganisation = { id: 123 };
        const clientOrganisationCollection: IClientOrganisation[] = [
          {
            ...clientOrganisation,
          },
          { id: 456 },
        ];
        expectedResult = service.addClientOrganisationToCollectionIfMissing(clientOrganisationCollection, clientOrganisation);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ClientOrganisation to an array that doesn't contain it", () => {
        const clientOrganisation: IClientOrganisation = { id: 123 };
        const clientOrganisationCollection: IClientOrganisation[] = [{ id: 456 }];
        expectedResult = service.addClientOrganisationToCollectionIfMissing(clientOrganisationCollection, clientOrganisation);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(clientOrganisation);
      });

      it('should add only unique ClientOrganisation to an array', () => {
        const clientOrganisationArray: IClientOrganisation[] = [{ id: 123 }, { id: 456 }, { id: 31996 }];
        const clientOrganisationCollection: IClientOrganisation[] = [{ id: 123 }];
        expectedResult = service.addClientOrganisationToCollectionIfMissing(clientOrganisationCollection, ...clientOrganisationArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const clientOrganisation: IClientOrganisation = { id: 123 };
        const clientOrganisation2: IClientOrganisation = { id: 456 };
        expectedResult = service.addClientOrganisationToCollectionIfMissing([], clientOrganisation, clientOrganisation2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(clientOrganisation);
        expect(expectedResult).toContain(clientOrganisation2);
      });

      it('should accept null and undefined values', () => {
        const clientOrganisation: IClientOrganisation = { id: 123 };
        expectedResult = service.addClientOrganisationToCollectionIfMissing([], null, clientOrganisation, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(clientOrganisation);
      });

      it('should return initial array if no ClientOrganisation is added', () => {
        const clientOrganisationCollection: IClientOrganisation[] = [{ id: 123 }];
        expectedResult = service.addClientOrganisationToCollectionIfMissing(clientOrganisationCollection, undefined, null);
        expect(expectedResult).toEqual(clientOrganisationCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
