import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IAirport } from '../airport.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../airport.test-samples';

import { AirportService } from './airport.service';

const requireRestSample: IAirport = {
  ...sampleWithRequiredData,
};

describe('Airport Service', () => {
  let service: AirportService;
  let httpMock: HttpTestingController;
  let expectedResult: IAirport | IAirport[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(AirportService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Airport', () => {
      const airport = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(airport).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Airport', () => {
      const airport = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(airport).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Airport', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Airport', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Airport', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addAirportToCollectionIfMissing', () => {
      it('should add a Airport to an empty array', () => {
        const airport: IAirport = sampleWithRequiredData;
        expectedResult = service.addAirportToCollectionIfMissing([], airport);
        expect(expectedResult).toEqual([airport]);
      });

      it('should not add a Airport to an array that contains it', () => {
        const airport: IAirport = sampleWithRequiredData;
        const airportCollection: IAirport[] = [
          {
            ...airport,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addAirportToCollectionIfMissing(airportCollection, airport);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Airport to an array that doesn't contain it", () => {
        const airport: IAirport = sampleWithRequiredData;
        const airportCollection: IAirport[] = [sampleWithPartialData];
        expectedResult = service.addAirportToCollectionIfMissing(airportCollection, airport);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(airport);
      });

      it('should add only unique Airport to an array', () => {
        const airportArray: IAirport[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const airportCollection: IAirport[] = [sampleWithRequiredData];
        expectedResult = service.addAirportToCollectionIfMissing(airportCollection, ...airportArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const airport: IAirport = sampleWithRequiredData;
        const airport2: IAirport = sampleWithPartialData;
        expectedResult = service.addAirportToCollectionIfMissing([], airport, airport2);
        expect(expectedResult).toEqual([airport, airport2]);
      });

      it('should accept null and undefined values', () => {
        const airport: IAirport = sampleWithRequiredData;
        expectedResult = service.addAirportToCollectionIfMissing([], null, airport, undefined);
        expect(expectedResult).toEqual([airport]);
      });

      it('should return initial array if no Airport is added', () => {
        const airportCollection: IAirport[] = [sampleWithRequiredData];
        expectedResult = service.addAirportToCollectionIfMissing(airportCollection, undefined, null);
        expect(expectedResult).toEqual(airportCollection);
      });
    });

    describe('compareAirport', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareAirport(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 18443 };
        const entity2 = null;

        const compareResult1 = service.compareAirport(entity1, entity2);
        const compareResult2 = service.compareAirport(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 18443 };
        const entity2 = { id: 14999 };

        const compareResult1 = service.compareAirport(entity1, entity2);
        const compareResult2 = service.compareAirport(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 18443 };
        const entity2 = { id: 18443 };

        const compareResult1 = service.compareAirport(entity1, entity2);
        const compareResult2 = service.compareAirport(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
