import { Component, OnInit, inject } from '@angular/core';
import { NgClass, NgForOf, NgIf } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { IPilotProfile } from '../pilot-profile.model';
import { IAchievement } from '../../achievement/achievement.model';
import { PilotProfileService } from '../service/pilot-profile.service';
import { AchievementService } from '../../achievement/service/achievement.service';

@Component({
  selector: 'jhi-pilot-profile',
  templateUrl: './pilot-profile.html',
  imports: [NgIf, NgForOf, NgClass, RouterLink, FontAwesomeModule],
})
export class PilotProfile implements OnInit {
  private pilotProfileService = inject(PilotProfileService);
  private achievementService = inject(AchievementService);

  currentPilot: IPilotProfile | null = null;
  rankProgression: { nextRank: string; targetXp: number; progressPercent: number } | null = null;
  allAchievements: IAchievement[] = [];
  isLoading = true;

  ngOnInit(): void {
    this.pilotProfileService.getCurrentPilot().subscribe({
      next: profile => {
        this.currentPilot = profile;
        this.rankProgression = this.pilotProfileService.getNextRankTarget(profile.totalXp ?? 0);
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      },
    });

    this.achievementService.query({ size: 20 }).subscribe({
      next: res => {
        this.allAchievements = res.body ?? [];
      },
    });
  }

  isBadgeUnlocked(badgeId: number | undefined): boolean {
    if (!badgeId) {
      return false;
    }
    const badges = this.currentPilot?.achievements ?? this.currentPilot?.achievementses;
    return badges?.some(a => a.id === badgeId) ?? false;
  }
}
