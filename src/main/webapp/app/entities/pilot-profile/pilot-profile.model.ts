import { IAchievement } from 'app/entities/achievement/achievement.model';
import { RankTier } from 'app/entities/enumerations/rank-tier.model';
import { IUser } from 'app/entities/user/user.model';

export interface IPilotProfile {
  id: number;
  totalXp?: number | null;
  rankTier?: keyof typeof RankTier | null;
  totalFlightHours?: number | null;
  totalNightFlightHours?: number | null;
  totalIfrFlightHours?: number | null;
  flightsCompleted?: number | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
  achievementses?: Pick<IAchievement, 'id' | 'title'>[] | null;
}

export type NewPilotProfile = Omit<IPilotProfile, 'id'> & { id: null };
