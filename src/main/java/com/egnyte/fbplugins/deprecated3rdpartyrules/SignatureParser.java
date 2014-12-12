package com.egnyte.fbplugins.deprecated3rdpartyrules;

import static com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition.MethodDefinition.newMethodDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition.ClassDefinition;
import com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition.MethodDefinition;
import com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition.MethodParametersList;

/**
 * This class consists methods which parse virtual machine code signatures and
 * return specified DTO definition.
 * 
 * <p>
 * Signatures are parsed using regular expressions so far, because the use of
 * other techniques in the current state of development would be
 * overprogramming.
 *
 */
public class SignatureParser {

    /**
     * It matches class signatures at the level of the virtual machine code. For
     * instance, following class signature will be matched by this pattern:
     * 
     * <pre>
     * {@code
     * java.util.TimeZone
     * }
     * </pre>
     */
    private final static Pattern CLASS_PATTERN = Pattern
            .compile("^(([a-zA-Z_$][a-zA-Z\\\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\\\d_$]*)\\.([a-zA-Z_$][a-zA-Z\\\\d_$]*)");

    /**
     * It matches method signatures at the level of the virtual machine code. It
     * does not validate method parameters. For instance, following method
     * signature will be matched by this pattern:
     * 
     * <pre>
     * {@code
     * java.util.TimeZone.getTimeZone : (Ljava.lang.String;)Ljava.util.TimeZone;
     * }
     * </pre>
     */
    private final static Pattern METHOD_PATTERN = Pattern
            .compile("^(((([a-zA-Z_$][a-zA-Z\\\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\\\d_$]*)\\.([a-zA-Z_$][a-zA-Z\\\\d_$]*))\\.(<?[a-zA-Z_$][a-zA-Z\\\\d_$]*>?))\\s*:\\s*(\\((.*)\\).+)");

    /**
     * It matches parameters signatures at the level of the virtual machine
     * code. For instance, following parameters signature will be matched by
     * this pattern:
     * 
     * <pre>
     * {@code
     * Ljava.lang.String;I[[Ljava.util.TimeZone;B
     * }
     * </pre>
     */
    private final static Pattern ARGUMENT_PATTERN = Pattern
            .compile("(\\[*)([BCDFIJSZ]|(L(([a-zA-Z_$][a-zA-Z\\\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\\\d_$]*);))");

    public ClassDefinition parseClassSignature(String classSignature) {
        Matcher classMatcher = CLASS_PATTERN.matcher(classSignature);
        if (classMatcher.matches()) {
            String packageName = classMatcher.group(1);
            String className = classMatcher.group(3);

            return new ClassDefinition(packageName, className);
        }

        return null;
    }

    public MethodDefinition parseMethodSignature(String methodSignature) {
        String trimmedSignature = methodSignature.trim();
        Matcher methodMatcher = METHOD_PATTERN.matcher(trimmedSignature);
        if (methodMatcher.matches()) {
            String classDefinition = methodMatcher.group(2);
            String methodName = methodMatcher.group(6);
            String argumentsStr = methodMatcher.group(8);

            List<String> arguments = new ArrayList<String>();
            if (!StringUtils.isBlank(argumentsStr)) {
                Matcher parameterMatcher = ARGUMENT_PATTERN.matcher(argumentsStr);
                while (parameterMatcher.find()) {
                    arguments.add(parseParameterSignature(parameterMatcher.group(0)));
                }
            }

            return newMethodDefinition(parseClassSignature(classDefinition), methodName,
                    new MethodParametersList(arguments));
        }

        return null;
    }

    public String getMethodArgumentAndReturnedValueSignature(String methodSignature) {
        String trimmedSignature = methodSignature.trim();
        Matcher methodMatcher = METHOD_PATTERN.matcher(trimmedSignature);
        if (methodMatcher.matches()) {
            return methodMatcher.group(7);
        }

        return null;
    }

    String parseParameterSignature(String paramSignature) {
        if (StringUtils.isBlank(paramSignature)) {
            throw new IllegalArgumentException("Parameter signature cannot be empty");
        }

        int paramCurrIdx = 0;

        String arraySigns = "";
        for (; paramCurrIdx < paramSignature.length(); paramCurrIdx++) {
            if (paramSignature.charAt(paramCurrIdx) != '[') {
                break;
            }
            arraySigns += "[]";
        }

        if (paramCurrIdx >= paramSignature.length()) {
            throw new IllegalArgumentException("Parameter type is required in parameter signature");
        }
        String paramType = "";
        switch (paramSignature.charAt(paramCurrIdx)) {
        case 'B':
            paramType = "byte";
            break;
        case 'C':
            paramType = "char";
            break;
        case 'D':
            paramType = "double";
            break;
        case 'F':
            paramType = "float";
            break;
        case 'I':
            paramType = "int";
            break;
        case 'J':
            paramType = "long";
            break;
        case 'S':
            paramType = "short";
            break;
        case 'Z':
            paramType = "boolean";
            break;
        case 'L':
            paramType = paramSignature.substring(paramCurrIdx + 1, paramSignature.length() - 1);
            break;
        default:
            throw new IllegalArgumentException("Incorrect parameter type");
        }

        return paramType + arraySigns;
    }

}
