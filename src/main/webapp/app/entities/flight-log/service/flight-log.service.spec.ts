import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IFlightLog } from '../flight-log.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../flight-log.test-samples';

import { FlightLogService, RestFlightLog } from './flight-log.service';

const requireRestSample: RestFlightLog = {
  ...sampleWithRequiredData,
  departureTime: sampleWithRequiredData.departureTime?.toJSON(),
  arrivalTime: sampleWithRequiredData.arrivalTime?.toJSON(),
};

describe('FlightLog Service', () => {
  let service: FlightLogService;
  let httpMock: HttpTestingController;
  let expectedResult: IFlightLog | IFlightLog[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(FlightLogService);
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

    it('should create a FlightLog', () => {
      const flightLog = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(flightLog).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a FlightLog', () => {
      const flightLog = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(flightLog).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a FlightLog', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of FlightLog', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a FlightLog', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addFlightLogToCollectionIfMissing', () => {
      it('should add a FlightLog to an empty array', () => {
        const flightLog: IFlightLog = sampleWithRequiredData;
        expectedResult = service.addFlightLogToCollectionIfMissing([], flightLog);
        expect(expectedResult).toEqual([flightLog]);
      });

      it('should not add a FlightLog to an array that contains it', () => {
        const flightLog: IFlightLog = sampleWithRequiredData;
        const flightLogCollection: IFlightLog[] = [
          {
            ...flightLog,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addFlightLogToCollectionIfMissing(flightLogCollection, flightLog);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a FlightLog to an array that doesn't contain it", () => {
        const flightLog: IFlightLog = sampleWithRequiredData;
        const flightLogCollection: IFlightLog[] = [sampleWithPartialData];
        expectedResult = service.addFlightLogToCollectionIfMissing(flightLogCollection, flightLog);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(flightLog);
      });

      it('should add only unique FlightLog to an array', () => {
        const flightLogArray: IFlightLog[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const flightLogCollection: IFlightLog[] = [sampleWithRequiredData];
        expectedResult = service.addFlightLogToCollectionIfMissing(flightLogCollection, ...flightLogArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const flightLog: IFlightLog = sampleWithRequiredData;
        const flightLog2: IFlightLog = sampleWithPartialData;
        expectedResult = service.addFlightLogToCollectionIfMissing([], flightLog, flightLog2);
        expect(expectedResult).toEqual([flightLog, flightLog2]);
      });

      it('should accept null and undefined values', () => {
        const flightLog: IFlightLog = sampleWithRequiredData;
        expectedResult = service.addFlightLogToCollectionIfMissing([], null, flightLog, undefined);
        expect(expectedResult).toEqual([flightLog]);
      });

      it('should return initial array if no FlightLog is added', () => {
        const flightLogCollection: IFlightLog[] = [sampleWithRequiredData];
        expectedResult = service.addFlightLogToCollectionIfMissing(flightLogCollection, undefined, null);
        expect(expectedResult).toEqual(flightLogCollection);
      });
    });

    describe('compareFlightLog', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareFlightLog(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 15441 };
        const entity2 = null;

        const compareResult1 = service.compareFlightLog(entity1, entity2);
        const compareResult2 = service.compareFlightLog(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 15441 };
        const entity2 = { id: 23292 };

        const compareResult1 = service.compareFlightLog(entity1, entity2);
        const compareResult2 = service.compareFlightLog(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 15441 };
        const entity2 = { id: 15441 };

        const compareResult1 = service.compareFlightLog(entity1, entity2);
        const compareResult2 = service.compareFlightLog(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
