import { ChangeDetectionStrategy, Component, OnInit, effect, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { Subscription, combineLatest, filter, tap } from 'rxjs';

import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { PilotProfileDeleteDialog } from '../delete/pilot-profile-delete-dialog';
import { IPilotProfile } from '../pilot-profile.model';
import { PilotProfileService } from '../service/pilot-profile.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-pilot-profile',
  templateUrl: './pilot-profile.html',
  imports: [RouterLink, FormsModule, FontAwesomeModule, AlertError, Alert, SortDirective, SortByDirective],
})
export class PilotProfile implements OnInit {
  subscription: Subscription | null = null;
  readonly pilotProfiles = signal<IPilotProfile[]>([]);

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly pilotProfileService = inject(PilotProfileService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.pilotProfileService.pilotProfilesResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.pilotProfiles.set(this.fillComponentAttributesFromResponseBody([...this.pilotProfileService.pilotProfiles()]));
    });
  }

  trackId = (item: IPilotProfile): number => this.pilotProfileService.getPilotProfileIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (this.pilotProfiles().length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  delete(pilotProfile: IPilotProfile): void {
    const modalRef = this.modalService.open(PilotProfileDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.pilotProfile = pilotProfile;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  load(): void {
    this.queryBackend();
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(event);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
  }

  protected refineData(data: IPilotProfile[]): IPilotProfile[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IPilotProfile[]): IPilotProfile[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      eagerload: true,
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.pilotProfileService.pilotProfilesParams.set(queryObject);
  }

  protected handleNavigation(sortState: SortState): void {
    const queryParamsObj = {
      sort: this.sortService.buildSortParam(sortState),
    };

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }
}
