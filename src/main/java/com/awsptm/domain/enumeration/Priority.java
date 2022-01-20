package com.awsptm.domain.enumeration;

/**
 * The Priority enumeration.
 */
public enum Priority {
    P1_HIGH("High"),
    P2_MEDIUM("Medium"),
    P3_LOW("Low");

    private final String value;

    Priority(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
