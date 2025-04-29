package com.example.proteinManager;

public class TestConfig {
    // Static boolean to control test mode manually
    private static boolean isTestMode = false;

    // Getter for test mode status
    public static boolean isTestMode() {
        return isTestMode;
    }

    // Setter to change test mode manually
    public static void setTestMode(boolean testMode) {
        isTestMode = testMode;
    }
}
