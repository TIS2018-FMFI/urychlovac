export enum Period {
    ONE_D,
    TWO_D,
    FIVE_D,

    ONE_H,
    TWO_H,
    SIX_H,
    TWELVE_H,

    TWO_M,
    FIVE_M,
    FIFTEEN_M,
    THIRTY_M
  }
  
  export const PeriodListDays = [
    {
      name: '1 deň',
      value: Period.ONE_D,
      beValue: '1_DAY'
    },
    {
      name: '2 dni',
      value: Period.TWO_D,
      beValue: '2_DAY'
    },
    {
      name: '5 dní',
      value: Period.FIVE_D,
      beValue: '5_DAY'
    },
  ];

  export const PeriodListHours = [
    {
      name: '1 hodina',
      value: Period.ONE_H,
      beValue: '1_HOUR'
    },
    {
      name: '2 hodiny',
      value: Period.TWO_H,
      beValue: '2_HOUR'
    },
    {
      name: '6 hodín',
      value: Period.SIX_H,
      beValue: '6_HOUR'
    },
    {
      name: '12 hodín',
      value: Period.TWELVE_H,
      beValue: '12_HOUR'
    },
  ];

  export const PeriodListMinutes = [
    {
      name: '2 minúty',
      value: Period.TWO_M,
      beValue: '2_MIN'
    },
    {
      name: '5 minút',
      value: Period.FIVE_M,
      beValue: '5_MIN'
    },
    {
      name: '15 minút',
      value: Period.FIFTEEN_M,
      beValue: '15_MIN'
    },
    {
      name: '30 minút',
      value: Period.THIRTY_M,
      beValue: '30_MIN'
    },
  ];