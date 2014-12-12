package com.egnyte.fbplugins.deprecated3rdpartyrules.settings;

import lombok.Data;

@Data
public class DeprecatedListParseError {
    private final int lineNo;

    private final String sourceCode;

    private final String message;
}
