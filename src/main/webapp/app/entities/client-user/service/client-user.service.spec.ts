import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IClientUser, ClientUser } from '../client-user.model';

import { ClientUserService } from './client-user.service';

describe('ClientUser Service', () => {
  let service: ClientUserService;
  let httpMock: HttpTestingController;
  let elemDefault: IClientUser;
  let expectedResult: IClientUser | IClientUser[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ClientUserService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      landline: 'AAAAAAA',
      mobile: 'AAAAAAA',
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

    it('should create a ClientUser', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new ClientUser()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ClientUser', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          landline: 'BBBBBB',
          mobile: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ClientUser', () => {
      const patchObject = Object.assign({}, new ClientUser());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ClientUser', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          landline: 'BBBBBB',
          mobile: 'BBBBBB',
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

    it('should delete a ClientUser', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addClientUserToCollectionIfMissing', () => {
      it('should add a ClientUser to an empty array', () => {
        const clientUser: IClientUser = { id: 123 };
        expectedResult = service.addClientUserToCollectionIfMissing([], clientUser);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(clientUser);
      });

      it('should not add a ClientUser to an array that contains it', () => {
        const clientUser: IClientUser = { id: 123 };
        const clientUserCollection: IClientUser[] = [
          {
            ...clientUser,
          },
          { id: 456 },
        ];
        expectedResult = service.addClientUserToCollectionIfMissing(clientUserCollection, clientUser);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ClientUser to an array that doesn't contain it", () => {
        const clientUser: IClientUser = { id: 123 };
        const clientUserCollection: IClientUser[] = [{ id: 456 }];
        expectedResult = service.addClientUserToCollectionIfMissing(clientUserCollection, clientUser);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(clientUser);
      });

      it('should add only unique ClientUser to an array', () => {
        const clientUserArray: IClientUser[] = [{ id: 123 }, { id: 456 }, { id: 38159 }];
        const clientUserCollection: IClientUser[] = [{ id: 123 }];
        expectedResult = service.addClientUserToCollectionIfMissing(clientUserCollection, ...clientUserArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const clientUser: IClientUser = { id: 123 };
        const clientUser2: IClientUser = { id: 456 };
        expectedResult = service.addClientUserToCollectionIfMissing([], clientUser, clientUser2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(clientUser);
        expect(expectedResult).toContain(clientUser2);
      });

      it('should accept null and undefined values', () => {
        const clientUser: IClientUser = { id: 123 };
        expectedResult = service.addClientUserToCollectionIfMissing([], null, clientUser, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(clientUser);
      });

      it('should return initial array if no ClientUser is added', () => {
        const clientUserCollection: IClientUser[] = [{ id: 123 }];
        expectedResult = service.addClientUserToCollectionIfMissing(clientUserCollection, undefined, null);
        expect(expectedResult).toEqual(clientUserCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
