import { IPilotProfile } from 'app/entities/pilot-profile/pilot-profile.model';

export interface IAchievement {
  id: number;
  code?: string | null;
  title?: string | null;
  description?: string | null;
  badgeIcon?: string | null;
  xpReward?: number | null;
  pilotses?: Pick<IPilotProfile, 'id'>[] | null;
}

export type NewAchievement = Omit<IAchievement, 'id'> & { id: null };
