package com.egnyte.fbplugins.deprecated3rdpartyrules;

import com.egnyte.fbplugins.deprecated3rdpartyrules.model.deprecation.ClassDeprecation;
import com.egnyte.fbplugins.deprecated3rdpartyrules.model.deprecation.MethodDeprecation;
import com.egnyte.fbplugins.deprecated3rdpartyrules.model.deprecation.PackageDeprecation;

public interface DeprecationVisitor {
    public void visit(MethodDeprecation methodDeprecation, Deprecated3rdPartyDetector detector, String refConstantOperand);

    public void visit(ClassDeprecation classDeprecation, Deprecated3rdPartyDetector detector, String refConstantOperand);

    public void visit(PackageDeprecation packageDeprecation, Deprecated3rdPartyDetector detector, String refConstantOperand);
}
