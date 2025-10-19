package com.supermarket.pos_backend.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum WeightUnit {
    ML("ml"),
    L("L"),
    G("g"),
    KG("kg"),
    PACKET("packet"),
    PIECE("piece");

    private final String label;

    WeightUnit(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static WeightUnit fromValue(String value) {
        for (WeightUnit unit : values()) {
            if (unit.label.equalsIgnoreCase(value)) { // 👈 case-insensitive match
                return unit;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
