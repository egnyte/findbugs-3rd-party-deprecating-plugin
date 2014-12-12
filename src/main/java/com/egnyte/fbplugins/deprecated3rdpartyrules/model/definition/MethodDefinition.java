package com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition;

import static lombok.AccessLevel.PRIVATE;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = PRIVATE)
public class MethodDefinition implements Definition<MethodDefinition> {
    private final static String BYTE_CODE_CONSTRUCTOR_SIGNATURE = "<init>";

    ClassDefinition methodsBlockDefinition;

    String methodName;

    MethodParameters parameters;

    public static MethodDefinition newMethodDefinition(ClassDefinition methodsBlockDefinition, String methodName,
            MethodParameters parameters) {
        return new MethodDefinition(methodsBlockDefinition, methodName, parameters);
    }

    public static MethodDefinition newConstructorDefinition(ClassDefinition methodsBlockDefinition,
            MethodParameters parameters) {
        return newMethodDefinition(methodsBlockDefinition, BYTE_CODE_CONSTRUCTOR_SIGNATURE, parameters);
    }

    @Override
    public boolean matchesTo(MethodDefinition definition) {
        return methodsBlockDefinition.matchesTo(definition.getMethodsBlockDefinition())
                && methodName.equals(definition.getMethodName())
                && parameters.matchesTo(definition.getParameters());
    }

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(methodsBlockDefinition.getPackageName());
        strBuilder.append(".");
        strBuilder.append(methodsBlockDefinition.getClassName());
        strBuilder.append(".");
        strBuilder.append(methodName);
        strBuilder.append("(");
        strBuilder.append(parameters.toString());
        strBuilder.append(")");

        return strBuilder.toString();
    }
}