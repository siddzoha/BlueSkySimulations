import { IAchievement, NewAchievement } from './achievement.model';

export const sampleWithRequiredData: IAchievement = {
  id: 379,
  code: 'overload',
  title: 'accompany frequent fiddle',
  description: 'pasta fiercely',
  xpReward: 26697,
};

export const sampleWithPartialData: IAchievement = {
  id: 23663,
  code: 'bashfully gust',
  title: 'tough',
  description: 'wherever where',
  xpReward: 7585,
};

export const sampleWithFullData: IAchievement = {
  id: 28283,
  code: 'fledgling',
  title: 'on dreamily following',
  description: 'now sophisticated backburn',
  badgeIcon: 'searchingly break yahoo',
  xpReward: 10753,
};

export const sampleWithNewData: NewAchievement = {
  code: 'to slather interviewer',
  title: 'vivaciously after',
  description: 'so',
  xpReward: 11221,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
