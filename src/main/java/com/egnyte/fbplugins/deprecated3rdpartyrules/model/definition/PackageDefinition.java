package com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition;

import lombok.Value;

import org.apache.commons.lang.NotImplementedException;

@Value
public class PackageDefinition implements Definition<PackageDefinition> {
    String packageName;

    boolean withSubpackages;

    @Override
    public String toString() {
        String subPackages = withSubpackages ? ".*" : "";
        return packageName + subPackages;
    }

    @Override
    public boolean matchesTo(PackageDefinition definition) {
        throw new NotImplementedException();
    }
}