import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IAircraft } from '../aircraft.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../aircraft.test-samples';

import { AircraftService } from './aircraft.service';

const requireRestSample: IAircraft = {
  ...sampleWithRequiredData,
};

describe('Aircraft Service', () => {
  let service: AircraftService;
  let httpMock: HttpTestingController;
  let expectedResult: IAircraft | IAircraft[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(AircraftService);
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

    it('should create a Aircraft', () => {
      const aircraft = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(aircraft).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Aircraft', () => {
      const aircraft = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(aircraft).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Aircraft', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Aircraft', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Aircraft', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addAircraftToCollectionIfMissing', () => {
      it('should add a Aircraft to an empty array', () => {
        const aircraft: IAircraft = sampleWithRequiredData;
        expectedResult = service.addAircraftToCollectionIfMissing([], aircraft);
        expect(expectedResult).toEqual([aircraft]);
      });

      it('should not add a Aircraft to an array that contains it', () => {
        const aircraft: IAircraft = sampleWithRequiredData;
        const aircraftCollection: IAircraft[] = [
          {
            ...aircraft,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addAircraftToCollectionIfMissing(aircraftCollection, aircraft);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Aircraft to an array that doesn't contain it", () => {
        const aircraft: IAircraft = sampleWithRequiredData;
        const aircraftCollection: IAircraft[] = [sampleWithPartialData];
        expectedResult = service.addAircraftToCollectionIfMissing(aircraftCollection, aircraft);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(aircraft);
      });

      it('should add only unique Aircraft to an array', () => {
        const aircraftArray: IAircraft[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const aircraftCollection: IAircraft[] = [sampleWithRequiredData];
        expectedResult = service.addAircraftToCollectionIfMissing(aircraftCollection, ...aircraftArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const aircraft: IAircraft = sampleWithRequiredData;
        const aircraft2: IAircraft = sampleWithPartialData;
        expectedResult = service.addAircraftToCollectionIfMissing([], aircraft, aircraft2);
        expect(expectedResult).toEqual([aircraft, aircraft2]);
      });

      it('should accept null and undefined values', () => {
        const aircraft: IAircraft = sampleWithRequiredData;
        expectedResult = service.addAircraftToCollectionIfMissing([], null, aircraft, undefined);
        expect(expectedResult).toEqual([aircraft]);
      });

      it('should return initial array if no Aircraft is added', () => {
        const aircraftCollection: IAircraft[] = [sampleWithRequiredData];
        expectedResult = service.addAircraftToCollectionIfMissing(aircraftCollection, undefined, null);
        expect(expectedResult).toEqual(aircraftCollection);
      });
    });

    describe('compareAircraft', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareAircraft(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 14531 };
        const entity2 = null;

        const compareResult1 = service.compareAircraft(entity1, entity2);
        const compareResult2 = service.compareAircraft(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 14531 };
        const entity2 = { id: 8931 };

        const compareResult1 = service.compareAircraft(entity1, entity2);
        const compareResult2 = service.compareAircraft(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 14531 };
        const entity2 = { id: 14531 };

        const compareResult1 = service.compareAircraft(entity1, entity2);
        const compareResult2 = service.compareAircraft(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
