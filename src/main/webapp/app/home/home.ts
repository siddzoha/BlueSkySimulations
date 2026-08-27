import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { AccountService } from 'app/core/auth/account.service';
import { PilotProfileService } from '../entities/pilot-profile/service/pilot-profile.service';
import { FlightLogService } from '../entities/flight-log/service/flight-log.service';
import { IPilotProfile } from '../entities/pilot-profile/pilot-profile.model';
import { IFlightLog } from '../entities/flight-log/flight-log.model';

@Component({
  selector: 'jhi-home',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './home.html',
  styleUrl: './home.scss',
  imports: [RouterLink],
})
export default class Home implements OnInit {
  public readonly account = inject(AccountService).account;
  private readonly pilotProfileService = inject(PilotProfileService);
  private readonly flightLogService = inject(FlightLogService);

  private readonly router = inject(Router);

  currentPilot: IPilotProfile | null = null;
  rankProgression: { nextRank: string; targetXp: number; progressPercent: number } | null = null;
  recentFlights: IFlightLog[] = [];

  login(): void {
    this.router.navigate(['/login']);
  }

  ngOnInit() {
    if (this.account()) {
      this.pilotProfileService.getCurrentPilot().subscribe({
        next: profile => {
          this.currentPilot = profile;
          this.rankProgression = this.pilotProfileService.getNextRankTarget(profile.totalXp ?? 0);
        },
      });

      this.flightLogService.query({ page: 0, size: 3, sort: ['departureTime,desc'] }).subscribe({
        next: res => {
          this.recentFlights = res.body ?? [];
        },
      });
    }
  }
}
