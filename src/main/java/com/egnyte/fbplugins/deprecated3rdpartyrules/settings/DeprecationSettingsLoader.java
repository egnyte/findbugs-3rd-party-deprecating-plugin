package com.egnyte.fbplugins.deprecated3rdpartyrules.settings;

import static com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition.MethodDefinition.newConstructorDefinition;
import static com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition.MethodDefinition.newMethodDefinition;
import static java.util.regex.Pattern.compile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition.ClassDefinition;
import com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition.MethodParameters;
import com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition.MethodParametersList;
import com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition.PackageDefinition;
import com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition.WildcardMethodParameter;
import com.egnyte.fbplugins.deprecated3rdpartyrules.model.deprecation.ClassDeprecation;
import com.egnyte.fbplugins.deprecated3rdpartyrules.model.deprecation.Deprecation;
import com.egnyte.fbplugins.deprecated3rdpartyrules.model.deprecation.MethodDeprecation;
import com.egnyte.fbplugins.deprecated3rdpartyrules.model.deprecation.PackageDeprecation;

/**
 * This class consists methods which load user deprecation settings from
 * deprecation settings file.
 * 
 * <p>
 * Definitions included in deprecation settings file are parsed using regular
 * expressions so far, because the use of other techniques in the current state
 * of development would be overprogramming.
 *
 */
public class DeprecationSettingsLoader {

    /**
     * The pattern matches deprecated packages definitions.
     * <p>
     * To deprecate package, the following format is supported:
     * 
     * <pre>
     * {@code
     * package [package.name] // reason of deprecation
     * }
     * </pre>
     * 
     * You can also deprecate package with subpackages:
     * 
     * <pre>
     * {@code
     * package [package.name].* // reason of deprecation
     * }
     * </pre>
     */
    private static final Pattern PACKAGE_PATTERN = compile("^package\\s+(([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*)(\\.\\*)?\\s*//\\s*(.+)$");

    /**
     * The pattern matches deprecated classes definitions.
     * <p>
     * To deprecate any usage of class, the following format is supported:
     * 
     * <pre>
     * {@code
     * class [package.name].[ClassName] // reason of deprecation
     * }
     * </pre>
     */
    private static final Pattern CLASS_PATTERN = compile("^class\\s+((([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*)\\.([a-zA-Z_$][a-zA-Z\\d_$]*))\\s*//\\s*(.+)$");

    /**
     * The pattern matches methods block definition, which contains list of
     * deprecated method definitions (see {@link #METHOD_PATTERN}).
     * <p>
     * To declare methods block, the following format is supported:
     * 
     * <pre>
     * {@code
     * class [package.name].[ClassName] {
     *     ...
     *     deprecated method definitions of [ClassName]
     *     ...
     * }
     * }
     * </pre>
     */
    private static final Pattern METHODS_BLOCK_PATTERN = compile("^class\\s+((([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*)\\.([a-zA-Z_$][a-zA-Z\\d_$]*))\\s*\\{\\s*$");

    /**
     * The pattern matches deprecated method or constructor definitions, which
     * should be contained in methods block (see {@link #METHODS_BLOCK_PATTERN}
     * ).
     * <p>
     * To deprecate method definition, the following format is supported:
     * 
     * <pre>
     * {@code
     * [methodName or ConstructorName]([parameters]) // reason of deprecation
     * }
     * </pre>
     * 
     * You can deprecate methods or constructors with specified parameters (for
     * instance "int, double, java.util.List" or "" as [parameters]), or with
     * wildcard parameter to match method with specified name with any
     * parameters ("..." as [parameters]).
     */
    private static final Pattern METHOD_PATTERN = compile("^([a-zA-Z_$][a-zA-Z\\d_$]*)\\s*\\(\\s*((\\.\\.\\.)|((([a-zA-Z_$][a-zA-Z\\d_$]*.)*[a-zA-Z_$][a-zA-Z\\d_$]*(\\s*\\[\\s*\\])*\\s*,\\s*)*([a-zA-Z_$][a-zA-Z\\d_$]*.)*[a-zA-Z_$][a-zA-Z\\d_$]*(\\s*\\[\\s*\\])*)?)\\s*\\)\\s*//\\s*(.+)$");

    private static final String PACKAGE_IDENTIFIER = "package";
    private static final String CLASS_IDENTIFIER = "class";
    private static final String WILDCARD_PARAMETERS_IDENTIFIER = "...";
    public static final String INCORRECT_PACKAGE_DEFINITION = "Incorrect package definition";

    public static final String INCORRECT_CLASS_OR_METHOD_BLOCK_DEFINITION = "Incorrect class or methods block definition";
    public static final String INCORRECT_METHOD_DEFINITION = "Incorrect method definition";
    public static final String LACK_OF_THE_END_OF_METHOD_BLOCK = "Lack of the end of methods block";

    public DeprecationSettings settingsFromResource(String filePath) throws Exception {
        InputStream in = DeprecationSettings.class.getResourceAsStream(filePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        return settingsFromBufferedReader(reader);
    }

    public DeprecationSettings settingsFromTxtFile(String filePath) throws Exception {
        File deprecatedListFile = new File(filePath);
        if (!deprecatedListFile.exists()) {
            throw new FileNotFoundException("Could not find settings file. Deprecated 3rd Party detector will not run.");
        }

        BufferedReader reader = new BufferedReader(new FileReader(deprecatedListFile));
        return settingsFromBufferedReader(reader);
    }

    public DeprecationSettings settingsFromBufferedReader(BufferedReader reader) throws IOException {
        List<String> lines = new ArrayList<String>();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return settingsFromTxtContent(lines);
    }

    public DeprecationSettings settingsFromTxtContent(List<String> lines) {
        List<Deprecation> deprecated = new ArrayList<Deprecation>();
        List<DeprecatedListParseError> errors = new ArrayList<DeprecatedListParseError>();

        ParseState state = ParseState.BASE;

        ClassDefinition methodsBlockDefinition = null;
        int lineNo = 0;
        for (String line : lines) {
            lineNo++;

            if (StringUtils.isBlank(line)) {
                continue;
            }

            String trimmedLine = line.trim();
            Deprecation deprecation = null;
            DeprecatedListParseError error = null;

            if (state == ParseState.BASE && trimmedLine.startsWith(PACKAGE_IDENTIFIER)) {
                deprecation = getPackageDeprecation(trimmedLine);
                if (deprecation == null) {
                    error = new DeprecatedListParseError(lineNo, line, INCORRECT_PACKAGE_DEFINITION);
                }
            } else if (state == ParseState.BASE && trimmedLine.startsWith(CLASS_IDENTIFIER)) {
                deprecation = getClassDeprecation(trimmedLine);
                if (deprecation == null) {
                    methodsBlockDefinition = getMethodsBlockDefinition(trimmedLine);
                    if (methodsBlockDefinition != null) {
                        state = ParseState.METHODS_BLOCK;
                    } else {
                        error = new DeprecatedListParseError(lineNo, line, INCORRECT_CLASS_OR_METHOD_BLOCK_DEFINITION);
                    }
                }
            } else if (state == ParseState.METHODS_BLOCK) {
                deprecation = getMethodDeprecation(methodsBlockDefinition, trimmedLine);
                if (deprecation == null) {
                    if (trimmedLine.equals("}")) {
                        methodsBlockDefinition = null;
                        state = ParseState.BASE;
                    } else {
                        error = new DeprecatedListParseError(lineNo, line, INCORRECT_METHOD_DEFINITION);
                    }
                }
            }

            if (error != null) {
                errors.add(error);
                error = null;
            } else if (deprecation != null) {
                deprecated.add(deprecation);
            }
        }

        if (state == ParseState.METHODS_BLOCK) {
            errors.add(new DeprecatedListParseError(lineNo, lines.get(lines.size() - 1), LACK_OF_THE_END_OF_METHOD_BLOCK));
        }

        return new DeprecationSettings(deprecated, errors);
    }

    private PackageDeprecation getPackageDeprecation(String trimmedLine) {
        Matcher m = PACKAGE_PATTERN.matcher(trimmedLine);
        if (m.matches()) {
            String packageName = m.group(1);
            boolean withSubpackages = m.group(3) != null;
            String reason = m.group(4);

            return new PackageDeprecation(new PackageDefinition(packageName, withSubpackages), reason);
        }

        return null;
    }

    private ClassDeprecation getClassDeprecation(String trimmedLine) {
        Matcher m = CLASS_PATTERN.matcher(trimmedLine);
        if (m.matches()) {
            String packageName = m.group(2);
            String className = m.group(4);
            String reason = m.group(5);

            return new ClassDeprecation(new ClassDefinition(packageName, className), reason);
        }

        return null;
    }

    private MethodDeprecation getMethodDeprecation(ClassDefinition methodsBlockDefinition, String trimmedLine) {
        Matcher m = METHOD_PATTERN.matcher(trimmedLine);
        if (m.matches()) {
            String methodName = m.group(1);
            String parametersStr = m.group(2);
            String reason = m.group(10);

            MethodParameters methodParameters = new MethodParametersList();
            if (parametersStr != null) {
                if (parametersStr.equals(WILDCARD_PARAMETERS_IDENTIFIER)) {
                    methodParameters = new WildcardMethodParameter();
                } else if (!parametersStr.isEmpty()) {
                    methodParameters = new MethodParametersList(getMethodParameters(parametersStr));
                }
            }

            if (methodsBlockDefinition.getClassName().equals(methodName)) {
                return new MethodDeprecation(newConstructorDefinition(methodsBlockDefinition, methodParameters),
                        reason);
            } else {
                return new MethodDeprecation(newMethodDefinition(methodsBlockDefinition, methodName,
                        methodParameters), reason);
            }
        }

        return null;
    }

    private List<String> getMethodParameters(String parametersStr) {
        String[] parameters = parametersStr.split("\\s*,\\s*");
        List<String> normalizedParams = new ArrayList<String>(parameters.length);
        for (String parameter : parameters) {
            normalizedParams.add(normalizeMethodParameter(parameter));
        }
        return normalizedParams;
    }

    private ClassDefinition getMethodsBlockDefinition(String trimmedLine) {
        Matcher m = METHODS_BLOCK_PATTERN.matcher(trimmedLine);
        if (m.matches()) {
            String packageName = m.group(2);
            String className = m.group(4);
            return new ClassDefinition(packageName, className);
        }

        return null;
    }

    private String normalizeMethodParameter(String methodParam) {
        return methodParam.replaceAll("\\s+", "");
    }

    private enum ParseState {
        BASE, METHODS_BLOCK
    }
}
