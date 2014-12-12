package com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition;

import lombok.Value;

@Value
public class WildcardMethodParameter implements MethodParameters {
    @Override
    public boolean matchesTo(MethodParameters definition) {
        return true;
    }

    @Override
    public String toString() {
        return "...";
    }
}
