import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IFlightLog, NewFlightLog } from '../flight-log.model';

export type PartialUpdateFlightLog = Partial<IFlightLog> & Pick<IFlightLog, 'id'>;

type RestOf<T extends IFlightLog | NewFlightLog> = Omit<T, 'departureTime' | 'arrivalTime'> & {
  departureTime?: string | null;
  arrivalTime?: string | null;
};

export type RestFlightLog = RestOf<IFlightLog>;

export type NewRestFlightLog = RestOf<NewFlightLog>;

export type PartialUpdateRestFlightLog = RestOf<PartialUpdateFlightLog>;

@Injectable()
export class FlightLogsService {
  readonly flightLogsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly flightLogsResource = httpResource<RestFlightLog[]>(() => {
    const params = this.flightLogsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of flightLog that have been fetched. It is updated when the flightLogsResource emits a new value.
   * In case of error while fetching the flightLogs, the signal is set to an empty array.
   */
  readonly flightLogs = computed(() =>
    (this.flightLogsResource.hasValue() ? this.flightLogsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/flight-logs');

  protected convertValueFromServer(restFlightLog: RestFlightLog): IFlightLog {
    return {
      ...restFlightLog,
      departureTime: restFlightLog.departureTime ? dayjs(restFlightLog.departureTime) : undefined,
      arrivalTime: restFlightLog.arrivalTime ? dayjs(restFlightLog.arrivalTime) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class FlightLogService extends FlightLogsService {
  protected readonly http = inject(HttpClient);

  create(flightLog: NewFlightLog): Observable<IFlightLog> {
    const copy = this.convertValueFromClient(flightLog);
    return this.http.post<RestFlightLog>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(flightLog: IFlightLog): Observable<IFlightLog> {
    const copy = this.convertValueFromClient(flightLog);
    return this.http
      .put<RestFlightLog>(`${this.resourceUrl}/${encodeURIComponent(this.getFlightLogIdentifier(flightLog))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(flightLog: PartialUpdateFlightLog): Observable<IFlightLog> {
    const copy = this.convertValueFromClient(flightLog);
    return this.http
      .patch<RestFlightLog>(`${this.resourceUrl}/${encodeURIComponent(this.getFlightLogIdentifier(flightLog))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IFlightLog> {
    return this.http
      .get<RestFlightLog>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IFlightLog[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestFlightLog[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getFlightLogIdentifier(flightLog: Pick<IFlightLog, 'id'>): number {
    return flightLog.id;
  }

  compareFlightLog(o1: Pick<IFlightLog, 'id'> | null, o2: Pick<IFlightLog, 'id'> | null): boolean {
    return o1 && o2 ? this.getFlightLogIdentifier(o1) === this.getFlightLogIdentifier(o2) : o1 === o2;
  }

  addFlightLogToCollectionIfMissing<Type extends Pick<IFlightLog, 'id'>>(
    flightLogCollection: Type[],
    ...flightLogsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const flightLogs: Type[] = flightLogsToCheck.filter(isPresent);
    if (flightLogs.length > 0) {
      const flightLogCollectionIdentifiers = flightLogCollection.map(flightLogItem => this.getFlightLogIdentifier(flightLogItem));
      const flightLogsToAdd = flightLogs.filter(flightLogItem => {
        const flightLogIdentifier = this.getFlightLogIdentifier(flightLogItem);
        if (flightLogCollectionIdentifiers.includes(flightLogIdentifier)) {
          return false;
        }
        flightLogCollectionIdentifiers.push(flightLogIdentifier);
        return true;
      });
      return [...flightLogsToAdd, ...flightLogCollection];
    }
    return flightLogCollection;
  }

  protected convertValueFromClient<T extends IFlightLog | NewFlightLog | PartialUpdateFlightLog>(flightLog: T): RestOf<T> {
    return {
      ...flightLog,
      departureTime: flightLog.departureTime?.toJSON() ?? null,
      arrivalTime: flightLog.arrivalTime?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestFlightLog): IFlightLog {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestFlightLog[]): IFlightLog[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
