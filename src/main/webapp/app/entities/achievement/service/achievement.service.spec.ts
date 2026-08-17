import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IAchievement } from '../achievement.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../achievement.test-samples';

import { AchievementService } from './achievement.service';

const requireRestSample: IAchievement = {
  ...sampleWithRequiredData,
};

describe('Achievement Service', () => {
  let service: AchievementService;
  let httpMock: HttpTestingController;
  let expectedResult: IAchievement | IAchievement[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(AchievementService);
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

    it('should create a Achievement', () => {
      const achievement = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(achievement).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Achievement', () => {
      const achievement = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(achievement).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Achievement', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Achievement', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Achievement', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addAchievementToCollectionIfMissing', () => {
      it('should add a Achievement to an empty array', () => {
        const achievement: IAchievement = sampleWithRequiredData;
        expectedResult = service.addAchievementToCollectionIfMissing([], achievement);
        expect(expectedResult).toEqual([achievement]);
      });

      it('should not add a Achievement to an array that contains it', () => {
        const achievement: IAchievement = sampleWithRequiredData;
        const achievementCollection: IAchievement[] = [
          {
            ...achievement,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addAchievementToCollectionIfMissing(achievementCollection, achievement);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Achievement to an array that doesn't contain it", () => {
        const achievement: IAchievement = sampleWithRequiredData;
        const achievementCollection: IAchievement[] = [sampleWithPartialData];
        expectedResult = service.addAchievementToCollectionIfMissing(achievementCollection, achievement);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(achievement);
      });

      it('should add only unique Achievement to an array', () => {
        const achievementArray: IAchievement[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const achievementCollection: IAchievement[] = [sampleWithRequiredData];
        expectedResult = service.addAchievementToCollectionIfMissing(achievementCollection, ...achievementArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const achievement: IAchievement = sampleWithRequiredData;
        const achievement2: IAchievement = sampleWithPartialData;
        expectedResult = service.addAchievementToCollectionIfMissing([], achievement, achievement2);
        expect(expectedResult).toEqual([achievement, achievement2]);
      });

      it('should accept null and undefined values', () => {
        const achievement: IAchievement = sampleWithRequiredData;
        expectedResult = service.addAchievementToCollectionIfMissing([], null, achievement, undefined);
        expect(expectedResult).toEqual([achievement]);
      });

      it('should return initial array if no Achievement is added', () => {
        const achievementCollection: IAchievement[] = [sampleWithRequiredData];
        expectedResult = service.addAchievementToCollectionIfMissing(achievementCollection, undefined, null);
        expect(expectedResult).toEqual(achievementCollection);
      });
    });

    describe('compareAchievement', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareAchievement(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 17559 };
        const entity2 = null;

        const compareResult1 = service.compareAchievement(entity1, entity2);
        const compareResult2 = service.compareAchievement(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 17559 };
        const entity2 = { id: 27064 };

        const compareResult1 = service.compareAchievement(entity1, entity2);
        const compareResult2 = service.compareAchievement(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 17559 };
        const entity2 = { id: 17559 };

        const compareResult1 = service.compareAchievement(entity1, entity2);
        const compareResult2 = service.compareAchievement(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
