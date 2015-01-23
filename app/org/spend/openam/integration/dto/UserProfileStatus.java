package org.spend.openam.integration.dto;

public enum UserProfileStatus {
    ACTIVE, INACTIVE;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
