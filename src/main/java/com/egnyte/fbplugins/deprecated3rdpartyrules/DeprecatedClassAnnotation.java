package com.egnyte.fbplugins.deprecated3rdpartyrules;

import edu.umd.cs.findbugs.ClassAnnotation;

public class DeprecatedClassAnnotation extends ClassAnnotation {
    private static final long serialVersionUID = 8919000888209573191L;
    private final String reason;

    public DeprecatedClassAnnotation(String className, String reason) {
        super(className);
        this.reason = reason;
    }

    @Override
    protected String formatPackageMember(String key, ClassAnnotation primaryClass) {
        if (key.equals("reason")) {
            return reason;
        }
        return super.formatPackageMember(key, primaryClass);
    }

    @Override
    public boolean isSignificant() {
        return false;
    }
}
