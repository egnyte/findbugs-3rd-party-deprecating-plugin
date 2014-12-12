package com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition;

import lombok.Value;

@Value
public class ClassDefinition implements Definition<ClassDefinition> {
    String packageName;

    String className;

    @Override
    public String toString() {
        return new StringBuilder().append(packageName).append(".").append(className).toString();
    }

    @Override
    public boolean matchesTo(ClassDefinition definition) {
        return equals(definition);
    }

}