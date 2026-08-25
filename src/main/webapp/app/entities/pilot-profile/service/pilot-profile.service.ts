import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IPilotProfile, NewPilotProfile } from '../pilot-profile.model';

export type PartialUpdatePilotProfile = Partial<IPilotProfile> & Pick<IPilotProfile, 'id'>;

@Injectable()
export class PilotProfilesService {
  readonly pilotProfilesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly pilotProfilesResource = httpResource<IPilotProfile[]>(() => {
    const params = this.pilotProfilesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of pilotProfile that have been fetched. It is updated when the pilotProfilesResource emits a new value.
   * In case of error while fetching the pilotProfiles, the signal is set to an empty array.
   */
  readonly pilotProfiles = computed(() => (this.pilotProfilesResource.hasValue() ? this.pilotProfilesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/pilot-profiles');
}

@Injectable({ providedIn: 'root' })
export class PilotProfileService extends PilotProfilesService {
  protected readonly http = inject(HttpClient);

  create(pilotProfile: NewPilotProfile): Observable<IPilotProfile> {
    return this.http.post<IPilotProfile>(this.resourceUrl, pilotProfile);
  }

  update(pilotProfile: IPilotProfile): Observable<IPilotProfile> {
    return this.http.put<IPilotProfile>(
      `${this.resourceUrl}/${encodeURIComponent(this.getPilotProfileIdentifier(pilotProfile))}`,
      pilotProfile,
    );
  }

  partialUpdate(pilotProfile: PartialUpdatePilotProfile): Observable<IPilotProfile> {
    return this.http.patch<IPilotProfile>(
      `${this.resourceUrl}/${encodeURIComponent(this.getPilotProfileIdentifier(pilotProfile))}`,
      pilotProfile,
    );
  }

  find(id: number): Observable<IPilotProfile> {
    return this.http.get<IPilotProfile>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getCurrentPilot(): Observable<IPilotProfile> {
    return this.http.get<IPilotProfile>('/api/navigation/current-pilot');
  }

  query(req?: any): Observable<HttpResponse<IPilotProfile[]>> {
    const options = createRequestOption(req);
    return this.http.get<IPilotProfile[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getPilotProfileIdentifier(pilotProfile: Pick<IPilotProfile, 'id'>): number {
    return pilotProfile.id;
  }

  comparePilotProfile(o1: Pick<IPilotProfile, 'id'> | null, o2: Pick<IPilotProfile, 'id'> | null): boolean {
    return o1 && o2 ? this.getPilotProfileIdentifier(o1) === this.getPilotProfileIdentifier(o2) : o1 === o2;
  }

  addPilotProfileToCollectionIfMissing<Type extends Pick<IPilotProfile, 'id'>>(
    pilotProfileCollection: Type[],
    ...pilotProfilesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const pilotProfiles: Type[] = pilotProfilesToCheck.filter(isPresent);
    if (pilotProfiles.length > 0) {
      const pilotProfileCollectionIdentifiers = pilotProfileCollection.map(pilotProfileItem =>
        this.getPilotProfileIdentifier(pilotProfileItem),
      );
      const pilotProfilesToAdd = pilotProfiles.filter(pilotProfileItem => {
        const pilotProfileIdentifier = this.getPilotProfileIdentifier(pilotProfileItem);
        if (pilotProfileCollectionIdentifiers.includes(pilotProfileIdentifier)) {
          return false;
        }
        pilotProfileCollectionIdentifiers.push(pilotProfileIdentifier);
        return true;
      });
      return [...pilotProfilesToAdd, ...pilotProfileCollection];
    }
    return pilotProfileCollection;
  }

  getNextRankTarget(xp: number): { nextRank: string; targetXp: number; progressPercent: number } {
    if (xp >= 15000) {
      return { nextRank: 'MAX RANK', targetXp: 15000, progressPercent: 100 };
    } else if (xp >= 8000) {
      const progress = ((xp - 8000) / (15000 - 8000)) * 100;
      return { nextRank: 'FLEET CHIEF', targetXp: 15000, progressPercent: Math.round(progress) };
    } else if (xp >= 3000) {
      const progress = ((xp - 3000) / (8000 - 3000)) * 100;
      return { nextRank: 'CAPTAIN', targetXp: 8000, progressPercent: Math.round(progress) };
    } else if (xp >= 1000) {
      const progress = ((xp - 1000) / (3000 - 1000)) * 100;
      return { nextRank: 'SENIOR FIRST OFFICER', targetXp: 3000, progressPercent: Math.round(progress) };
    } else {
      const progress = (xp / 1000) * 100;
      return { nextRank: 'FIRST OFFICER', targetXp: 1000, progressPercent: Math.round(progress) };
    }
  }
}
