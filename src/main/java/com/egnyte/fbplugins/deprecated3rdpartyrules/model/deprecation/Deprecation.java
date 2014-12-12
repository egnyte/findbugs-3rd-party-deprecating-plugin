package com.egnyte.fbplugins.deprecated3rdpartyrules.model.deprecation;

import com.egnyte.fbplugins.deprecated3rdpartyrules.Deprecated3rdPartyDetector;
import com.egnyte.fbplugins.deprecated3rdpartyrules.DeprecationVisitor;

public interface Deprecation {
    public String getReason();

    public void accept(DeprecationVisitor visitor, Deprecated3rdPartyDetector detector, String refConstantOperand);
}
