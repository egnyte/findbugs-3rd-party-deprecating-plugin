package com.egnyte.fbplugins.deprecated3rdpartyrules.model.deprecation;

import lombok.Value;
import lombok.experimental.NonFinal;

import com.egnyte.fbplugins.deprecated3rdpartyrules.Deprecated3rdPartyDetector;
import com.egnyte.fbplugins.deprecated3rdpartyrules.DeprecationVisitor;
import com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition.MethodDefinition;

@Value
@NonFinal
public class MethodDeprecation implements Deprecation {
    MethodDefinition methodDefinition;

    String reason;

    @Override
    public void accept(DeprecationVisitor visitor, Deprecated3rdPartyDetector detector, String refConstantOperand) {
        visitor.visit(this, detector, refConstantOperand);
    }
}
