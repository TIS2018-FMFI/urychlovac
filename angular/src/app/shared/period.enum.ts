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
    },
    {
      name: '2 dni',
      value: Period.TWO_D,
    },
    {
      name: '5 dní',
      value: Period.FIVE_D,
    },
  ];

  export const PeriodListHours = [
    {
      name: '1 hodina',
      value: Period.ONE_H,
    },
    {
      name: '2 hodiny',
      value: Period.TWO_H,
    },
    {
      name: '6 hodín',
      value: Period.SIX_H,
    },
    {
      name: '12 hodín',
      value: Period.TWELVE_H,
    },
  ];

  export const PeriodListMinutes = [
    {
      name: '2 minúty',
      value: Period.TWO_M,
    },
    {
      name: '5 minút',
      value: Period.FIVE_M,
    },
    {
      name: '15 minút',
      value: Period.FIFTEEN_M,
    },
    {
      name: '30 minút',
      value: Period.THIRTY_M,
    },
  ];