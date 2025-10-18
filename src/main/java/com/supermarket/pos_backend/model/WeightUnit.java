package com.supermarket.pos_backend.model;

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

    public String getLabel() {
        return label;
    }
}
