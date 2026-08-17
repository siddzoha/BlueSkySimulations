import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IPilotProfile } from '../pilot-profile.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../pilot-profile.test-samples';

import { PilotProfileService } from './pilot-profile.service';

const requireRestSample: IPilotProfile = {
  ...sampleWithRequiredData,
};

describe('PilotProfile Service', () => {
  let service: PilotProfileService;
  let httpMock: HttpTestingController;
  let expectedResult: IPilotProfile | IPilotProfile[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PilotProfileService);
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

    it('should create a PilotProfile', () => {
      const pilotProfile = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(pilotProfile).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PilotProfile', () => {
      const pilotProfile = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(pilotProfile).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PilotProfile', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PilotProfile', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PilotProfile', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addPilotProfileToCollectionIfMissing', () => {
      it('should add a PilotProfile to an empty array', () => {
        const pilotProfile: IPilotProfile = sampleWithRequiredData;
        expectedResult = service.addPilotProfileToCollectionIfMissing([], pilotProfile);
        expect(expectedResult).toEqual([pilotProfile]);
      });

      it('should not add a PilotProfile to an array that contains it', () => {
        const pilotProfile: IPilotProfile = sampleWithRequiredData;
        const pilotProfileCollection: IPilotProfile[] = [
          {
            ...pilotProfile,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPilotProfileToCollectionIfMissing(pilotProfileCollection, pilotProfile);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PilotProfile to an array that doesn't contain it", () => {
        const pilotProfile: IPilotProfile = sampleWithRequiredData;
        const pilotProfileCollection: IPilotProfile[] = [sampleWithPartialData];
        expectedResult = service.addPilotProfileToCollectionIfMissing(pilotProfileCollection, pilotProfile);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(pilotProfile);
      });

      it('should add only unique PilotProfile to an array', () => {
        const pilotProfileArray: IPilotProfile[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const pilotProfileCollection: IPilotProfile[] = [sampleWithRequiredData];
        expectedResult = service.addPilotProfileToCollectionIfMissing(pilotProfileCollection, ...pilotProfileArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const pilotProfile: IPilotProfile = sampleWithRequiredData;
        const pilotProfile2: IPilotProfile = sampleWithPartialData;
        expectedResult = service.addPilotProfileToCollectionIfMissing([], pilotProfile, pilotProfile2);
        expect(expectedResult).toEqual([pilotProfile, pilotProfile2]);
      });

      it('should accept null and undefined values', () => {
        const pilotProfile: IPilotProfile = sampleWithRequiredData;
        expectedResult = service.addPilotProfileToCollectionIfMissing([], null, pilotProfile, undefined);
        expect(expectedResult).toEqual([pilotProfile]);
      });

      it('should return initial array if no PilotProfile is added', () => {
        const pilotProfileCollection: IPilotProfile[] = [sampleWithRequiredData];
        expectedResult = service.addPilotProfileToCollectionIfMissing(pilotProfileCollection, undefined, null);
        expect(expectedResult).toEqual(pilotProfileCollection);
      });
    });

    describe('comparePilotProfile', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePilotProfile(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 18599 };
        const entity2 = null;

        const compareResult1 = service.comparePilotProfile(entity1, entity2);
        const compareResult2 = service.comparePilotProfile(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 18599 };
        const entity2 = { id: 16800 };

        const compareResult1 = service.comparePilotProfile(entity1, entity2);
        const compareResult2 = service.comparePilotProfile(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 18599 };
        const entity2 = { id: 18599 };

        const compareResult1 = service.comparePilotProfile(entity1, entity2);
        const compareResult2 = service.comparePilotProfile(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
