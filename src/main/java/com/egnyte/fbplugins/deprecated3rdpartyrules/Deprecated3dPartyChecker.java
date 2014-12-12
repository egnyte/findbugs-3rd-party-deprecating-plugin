package com.egnyte.fbplugins.deprecated3rdpartyrules;

import org.apache.commons.lang.NotImplementedException;

import com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition.ClassDefinition;
import com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition.MethodDefinition;
import com.egnyte.fbplugins.deprecated3rdpartyrules.model.deprecation.ClassDeprecation;
import com.egnyte.fbplugins.deprecated3rdpartyrules.model.deprecation.MethodDeprecation;
import com.egnyte.fbplugins.deprecated3rdpartyrules.model.deprecation.PackageDeprecation;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.Priorities;

public class Deprecated3dPartyChecker implements DeprecationVisitor {
    private static final String DEPRECATED_3RD_PARTY_CLASS = "DEPRECATED_3RD_PARTY_CLASS";

    private static final String DEPRECATED_3RD_PARTY_METHOD = "DEPRECATED_3RD_PARTY_METHOD";

    private SignatureParser signatureParser;

    public Deprecated3dPartyChecker() {
        this.signatureParser = new SignatureParser();
    }

    @Override
    public void visit(MethodDeprecation methodDeprecation, Deprecated3rdPartyDetector detector, String refConstantOperand) {
        MethodDefinition methodDef = signatureParser.parseMethodSignature(refConstantOperand);
        if (methodDef != null && methodDeprecation.getMethodDefinition().matchesTo(methodDef)) {
            String className = methodDeprecation.getMethodDefinition().getMethodsBlockDefinition().toString();
            String methodName = methodDeprecation.getMethodDefinition().getMethodName();
            String methodArgsAndReturnValueSignature = signatureParser.getMethodArgumentAndReturnedValueSignature(refConstantOperand);
            detector.getBugReporter().reportBug(
                    new BugInstance(DEPRECATED_3RD_PARTY_METHOD, Priorities.HIGH_PRIORITY)
                            .addClass(detector)
                            .addMethod(detector)
                            .addSourceLine(detector)
                            .addString(refConstantOperand)
                            .add(new DeprecatedMethodAnnotation(className, methodName, methodArgsAndReturnValueSignature,
                                    methodDeprecation.getReason())));
        }
    }

    @Override
    public void visit(ClassDeprecation classDeprecation, Deprecated3rdPartyDetector detector, String refConstantOperand) {
        MethodDefinition methodDef = signatureParser.parseMethodSignature(refConstantOperand);
        if (methodDef != null) {
            ClassDefinition classDef = methodDef.getMethodsBlockDefinition();
            if (classDeprecation.getClassDefinition().matchesTo(classDef)) {
                detector.getBugReporter().reportBug(
                        new BugInstance(DEPRECATED_3RD_PARTY_CLASS, Priorities.HIGH_PRIORITY)
                                .addClass(detector)
                                .addMethod(detector)
                                .addSourceLine(detector)
                                .addString(refConstantOperand)
                                .add(new DeprecatedClassAnnotation(classDeprecation.getClassDefinition().toString(),
                                        classDeprecation.getReason())));
            }
        }
    }

    @Override
    public void visit(PackageDeprecation packageDeprecation, Deprecated3rdPartyDetector detector, String refConstantOperand) {
        throw new NotImplementedException();
    }

}
