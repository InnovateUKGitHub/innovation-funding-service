package com.worth.ifs.competition.resource;

/**
 * This enum defines the threestate type for the competition
 */
public enum ThreeStateType {
    TRUE(Boolean.TRUE, "yes"),
    FALSE(Boolean.FALSE, "no"),
    UNSET(null, "");

    private Boolean booleanValue;
    private String booleanName;

    ThreeStateType(Boolean booleanValue, String booleanName) {
        this.booleanValue = booleanValue;
        this.booleanName = booleanName;
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public String getBooleanName() { return booleanName; }

    public static ThreeStateType fromBoolean(Boolean type) {
        for(ThreeStateType threeStateType : ThreeStateType.values()) {
            if(type.equals(threeStateType.getBooleanValue())) {
                return threeStateType;
            }
        }
        throw new IllegalArgumentException("Not a valid ThreeStateType : " + type);
    }

    public static ThreeStateType fromBooleanName(String booleanName) {
        for(ThreeStateType threeStateType : ThreeStateType.values()) {
            if(booleanName.equals(threeStateType.getBooleanName())) {
                return threeStateType;
            }
        }
        throw new IllegalArgumentException("Not a valid ThreeStateType : " + booleanName);
    }
}

