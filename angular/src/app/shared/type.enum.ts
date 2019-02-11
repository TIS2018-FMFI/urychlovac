export enum Type {
    COOL_WATER1,
    COOL_WATER2,
    TARGET_FLANGE,
    TARGET_DETECT1,
    TARGET_DETECT2,
    ION_SOURCE,
    TANDEM,
    ROOM
}

export const TypeTemperatureList = [
    {
        name: "Cooling Water 1",
        value: Type.COOL_WATER1
    },
    {
        name: "Cooling Water 2",
        value: Type.COOL_WATER2
    }
]

export const TypeHumidityList = [
    { 
        name: "Target flange",
        value: Type.TARGET_FLANGE
    },
    {
        name: "Target detector 1",
        value: Type.TARGET_DETECT1
    },
    {
        name: "Target detector 2",
        value: Type.TARGET_DETECT2
    },
    {
        name: "Acc. ion source",
        value: Type.ION_SOURCE
    },
    {
        name: "Acc. tandem",
        value: Type.TANDEM
    },
    {
        name: "Acc. room",
        value: Type.ROOM
    }
]