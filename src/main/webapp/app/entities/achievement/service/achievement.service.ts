import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IAchievement, NewAchievement } from '../achievement.model';

export type PartialUpdateAchievement = Partial<IAchievement> & Pick<IAchievement, 'id'>;

@Injectable()
export class AchievementsService {
  readonly achievementsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly achievementsResource = httpResource<IAchievement[]>(() => {
    const params = this.achievementsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of achievement that have been fetched. It is updated when the achievementsResource emits a new value.
   * In case of error while fetching the achievements, the signal is set to an empty array.
   */
  readonly achievements = computed(() => (this.achievementsResource.hasValue() ? this.achievementsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/achievements');
}

@Injectable({ providedIn: 'root' })
export class AchievementService extends AchievementsService {
  protected readonly http = inject(HttpClient);

  create(achievement: NewAchievement): Observable<IAchievement> {
    return this.http.post<IAchievement>(this.resourceUrl, achievement);
  }

  update(achievement: IAchievement): Observable<IAchievement> {
    return this.http.put<IAchievement>(
      `${this.resourceUrl}/${encodeURIComponent(this.getAchievementIdentifier(achievement))}`,
      achievement,
    );
  }

  partialUpdate(achievement: PartialUpdateAchievement): Observable<IAchievement> {
    return this.http.patch<IAchievement>(
      `${this.resourceUrl}/${encodeURIComponent(this.getAchievementIdentifier(achievement))}`,
      achievement,
    );
  }

  find(id: number): Observable<IAchievement> {
    return this.http.get<IAchievement>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IAchievement[]>> {
    const options = createRequestOption(req);
    return this.http.get<IAchievement[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getAchievementIdentifier(achievement: Pick<IAchievement, 'id'>): number {
    return achievement.id;
  }

  compareAchievement(o1: Pick<IAchievement, 'id'> | null, o2: Pick<IAchievement, 'id'> | null): boolean {
    return o1 && o2 ? this.getAchievementIdentifier(o1) === this.getAchievementIdentifier(o2) : o1 === o2;
  }

  addAchievementToCollectionIfMissing<Type extends Pick<IAchievement, 'id'>>(
    achievementCollection: Type[],
    ...achievementsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const achievements: Type[] = achievementsToCheck.filter(isPresent);
    if (achievements.length > 0) {
      const achievementCollectionIdentifiers = achievementCollection.map(achievementItem => this.getAchievementIdentifier(achievementItem));
      const achievementsToAdd = achievements.filter(achievementItem => {
        const achievementIdentifier = this.getAchievementIdentifier(achievementItem);
        if (achievementCollectionIdentifiers.includes(achievementIdentifier)) {
          return false;
        }
        achievementCollectionIdentifiers.push(achievementIdentifier);
        return true;
      });
      return [...achievementsToAdd, ...achievementCollection];
    }
    return achievementCollection;
  }
}
