package com.onclass.ms_bootcamp.domain.exception;

public enum ErrorCode {
    BOOTCAMP_ALREADY_EXISTS("SE_001", "Capability name already exists"),
    INVALID_CAPABILITIES_COUNT("SE_002", "A capability must have between 1 and 4 capabilities"),
    DUPLICATE_CAPABILITIES("SE_003", "Capabilities list cannot contain duplicate IDs"),
    ASSOCIATE_CAPABILITY_ERROR("SE004", "Error associating capabilities in capability service"),
    INTERNAL_SERVER_ERROR("SE005", "Unexpected internal server error"),
    VALIDATION_ERROR("SE_006", "Validation error"),
    CAPABILITY_SERVICE_UNAVAILABLE("SE007", "Capability service is currently unavailable"),
    INVALID_PAGINATION_PARAMS("SE008", "Page must be >= 0 and size must be > 0"),
    INVALID_SORT_PARAMS("SE009", "Invalid sortBy value(name, capabilityCount) or sortDirection(ASC, DESC)");


    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}