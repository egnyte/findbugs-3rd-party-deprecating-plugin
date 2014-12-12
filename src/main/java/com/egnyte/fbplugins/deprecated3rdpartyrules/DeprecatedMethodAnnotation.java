package com.egnyte.fbplugins.deprecated3rdpartyrules;

import edu.umd.cs.findbugs.ClassAnnotation;
import edu.umd.cs.findbugs.MethodAnnotation;

public class DeprecatedMethodAnnotation extends MethodAnnotation {
    private static final long serialVersionUID = 6039123154634247622L;
    private final String reason;

    public DeprecatedMethodAnnotation(String className, String methodName, String methodSig, String reason) {
        super(className, methodName, methodSig, false);
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
