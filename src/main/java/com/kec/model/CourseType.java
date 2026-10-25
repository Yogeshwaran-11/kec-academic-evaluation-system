package com.kec.model;

/**
 * Course Types under Kongu Engineering College (Autonomous) - Regulation 2024.
 * Defines evaluation weightages and ESE minimum passing thresholds.
 */
public enum CourseType {
    THEORY("Theory", 0.40, 0.60, 45.0, 50.0),
    PRACTICAL("Practical", 0.60, 0.40, 50.0, 50.0),
    EMBEDDED("Embedded", 0.50, 0.50, 45.0, 50.0);

    private final String displayName;
    private final double ciaWeightage; // e.g., 0.40 for 40%
    private final double eseWeightage; // e.g., 0.60 for 60%
    private final double eseMinThresholdRaw; // Minimum raw ESE mark out of 100 required (45% for Theory/Embedded, 50% for Practical)
    private final double overallPassMin; // 50 marks out of 100 total

    CourseType(String displayName, double ciaWeightage, double eseWeightage, 
               double eseMinThresholdRaw, double overallPassMin) {
        this.displayName = displayName;
        this.ciaWeightage = ciaWeightage;
        this.eseWeightage = eseWeightage;
        this.eseMinThresholdRaw = eseMinThresholdRaw;
        this.overallPassMin = overallPassMin;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getCiaWeightage() {
        return ciaWeightage;
    }

    public double getEseWeightage() {
        return eseWeightage;
    }

    public double getEseMinThresholdRaw() {
        return eseMinThresholdRaw;
    }

    public double getEseMinThresholdScaled() {
        return eseMinThresholdRaw * eseWeightage;
    }

    public double getOverallPassMin() {
        return overallPassMin;
    }

    public static CourseType fromString(String text) {
        if (text == null) return THEORY;
        String normalized = text.trim().toUpperCase();
        for (CourseType ct : CourseType.values()) {
            if (ct.name().equals(normalized)) return ct;
        }
        throw new IllegalArgumentException("Unknown Course Type: " + text + ". Expected THEORY, PRACTICAL, or EMBEDDED.");
    }
}
