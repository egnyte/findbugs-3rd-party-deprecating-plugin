package com.egnyte.fbplugins.deprecated3rdpartyrules.settings;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.egnyte.fbplugins.deprecated3rdpartyrules.model.deprecation.Deprecation;

@Getter
@AllArgsConstructor
public class DeprecationSettings {
    private final List<Deprecation> deprecations;

    private final List<DeprecatedListParseError> errors;
}
