package com.egnyte.fbplugins.settings;

import static com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition.MethodDefinition.newConstructorDefinition;
import static com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition.MethodDefinition.newMethodDefinition;
import static com.egnyte.fbplugins.deprecated3rdpartyrules.settings.DeprecationSettingsLoader.INCORRECT_CLASS_OR_METHOD_BLOCK_DEFINITION;
import static com.egnyte.fbplugins.deprecated3rdpartyrules.settings.DeprecationSettingsLoader.INCORRECT_METHOD_DEFINITION;
import static com.egnyte.fbplugins.deprecated3rdpartyrules.settings.DeprecationSettingsLoader.INCORRECT_PACKAGE_DEFINITION;
import static com.egnyte.fbplugins.deprecated3rdpartyrules.settings.DeprecationSettingsLoader.LACK_OF_THE_END_OF_METHOD_BLOCK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition.ClassDefinition;
import com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition.MethodParametersList;
import com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition.PackageDefinition;
import com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition.WildcardMethodParameter;
import com.egnyte.fbplugins.deprecated3rdpartyrules.model.deprecation.Deprecation;
import com.egnyte.fbplugins.deprecated3rdpartyrules.model.deprecation.MethodDeprecation;
import com.egnyte.fbplugins.deprecated3rdpartyrules.model.deprecation.PackageDeprecation;
import com.egnyte.fbplugins.deprecated3rdpartyrules.settings.DeprecatedListParseError;
import com.egnyte.fbplugins.deprecated3rdpartyrules.settings.DeprecationSettings;
import com.egnyte.fbplugins.deprecated3rdpartyrules.settings.DeprecationSettingsLoader;
import com.google.common.collect.Lists;

public class DeprecatedSettingsLoaderUnitTest {
    private static final String VALID_METHODS_WITH_PARAMS = "/valid-methods-with-params.txt";

    private static final String VALID_METHODS_FROM_MULT_CLASSES = "/valid-methods-from-mult-classes.txt";

    private static final String VALID_CONSTRUCTORS_WITH_PARAMS = "/valid-constructors-with-params.txt";

    private static final String VALID_CONSTRUCTORS_FROM_MULT_CLASSES = "/valid-constructors-from-mult-classes.txt";

    private static final String VALID_PACKAGES = "/valid-packages.txt";

    private static final String VALID_PACKAGES_WITH_SUBPACKAGES = "/valid-packages-with-subpackages.txt";

    private static final String INVALID_DEPRECATIONS_WITHOUT_COMMENT = "/invalid-deprecations-without-comment.txt";

    private static final String INVALID_METHODS_WITHOUT_BRACES = "/invalid-methods-without-braces.txt";

    private static final String VALID_EMPTY = "/valid-empty.txt";

    private static final String INVALID_METHODS_BLOCK_WITHOUT_END_BRACKET = "/invalid-methods-block-without-end-bracket.txt";

    private static final String VALID_METHODS_WITH_WILDCARD_PARAMS = "/valid-methods-with-wildcard-params.txt";

    private DeprecationSettingsLoader settingsLoader;

    @Before
    public void setUp() throws Exception {
        this.settingsLoader = new DeprecationSettingsLoader();
    }

    @Test
    public void should_parse_methods_with_params() throws Exception {
        final ClassDefinition methodsBlockDefinition = new ClassDefinition("java.util", "Class");

        MethodDeprecation expectedMethod1 = new MethodDeprecation(newMethodDefinition(methodsBlockDefinition,
                "method1", new MethodParametersList(Lists.newArrayList("java.util.Test"))), "method 1 reason");

        MethodDeprecation expectedMethod2 = new MethodDeprecation(newMethodDefinition(methodsBlockDefinition,
                "method2", new MethodParametersList(Lists.newArrayList("java.t1.Test1", "java.util.Test2"))), "method 2 reason");

        MethodDeprecation expectedMethod3 = new MethodDeprecation(newMethodDefinition(methodsBlockDefinition,
                "method3", new MethodParametersList(Lists.newArrayList("java.t1.Test1", "java.util.Test2"))), "method 3 reason");

        MethodDeprecation expectedMethod4 = new MethodDeprecation(newMethodDefinition(methodsBlockDefinition,
                "method4", new MethodParametersList(Lists.newArrayList("int", "java.util.Test2[][][]"))), "method 4 reason");

        DeprecationSettings settings = getDeprecatedSettings(VALID_METHODS_WITH_PARAMS);
        List<Deprecation> deprecationsList = settings.getDeprecations();
        List<DeprecatedListParseError> errorsList = settings.getErrors();

        assertTrue(errorsList.isEmpty());

        assertEquals(4, deprecationsList.size());
        assertEquals(expectedMethod1, deprecationsList.get(0));
        assertEquals(expectedMethod2, deprecationsList.get(1));
        assertEquals(expectedMethod3, deprecationsList.get(2));
        assertEquals(expectedMethod4, deprecationsList.get(3));
    }

    @Test
    public void should_parse_methods_with_wildcard_params() throws Exception {
        final ClassDefinition methodsBlockDefinition1 = new ClassDefinition("java.package1", "Class1");

        MethodDeprecation expectedMethod1 = new MethodDeprecation(newMethodDefinition(methodsBlockDefinition1,
                "method1", new WildcardMethodParameter()), "method reason");

        final ClassDefinition methodsBlockDefinition2 = new ClassDefinition("java.package2", "Class2");
        MethodDeprecation expectedMethod2 = new MethodDeprecation(newMethodDefinition(methodsBlockDefinition2,
                "method2", new WildcardMethodParameter()), "method reason");

        DeprecationSettings settings = getDeprecatedSettings(VALID_METHODS_WITH_WILDCARD_PARAMS);
        List<Deprecation> deprecationsList = settings.getDeprecations();
        List<DeprecatedListParseError> errorsList = settings.getErrors();

        assertTrue(errorsList.isEmpty());

        assertEquals(2, deprecationsList.size());
        assertEquals(expectedMethod1, deprecationsList.get(0));
        assertEquals(expectedMethod2, deprecationsList.get(1));
    }

    @Test
    public void should_parse_methods_from_mult_classes() throws Exception {
        final ClassDefinition methodsBlock1 = new ClassDefinition("java.util", "Class1");
        MethodDeprecation expectedMethod1 = new MethodDeprecation(newMethodDefinition(methodsBlock1, "method1",
                new MethodParametersList()), "test1");

        final ClassDefinition methodsBlock2 = new ClassDefinition("java.util", "Class2");
        MethodDeprecation expectedMethod2 = new MethodDeprecation(newMethodDefinition(methodsBlock2, "method2",
                new MethodParametersList(Lists.newArrayList("java.util.Test"))), "test2");

        DeprecationSettings settings = getDeprecatedSettings(VALID_METHODS_FROM_MULT_CLASSES);
        List<Deprecation> deprecationsList = settings.getDeprecations();
        List<DeprecatedListParseError> errorsList = settings.getErrors();

        assertTrue(errorsList.isEmpty());

        assertEquals(2, deprecationsList.size());
        assertEquals(expectedMethod1, deprecationsList.get(0));
        assertEquals(expectedMethod2, deprecationsList.get(1));
    }

    @Test
    public void should_parse_constructors_with_params() throws Exception {
        final ClassDefinition methodsBlockDefinition = new ClassDefinition("java.util", "Class");

        MethodDeprecation expectedConstructor1 = new MethodDeprecation(newConstructorDefinition(
                methodsBlockDefinition, new MethodParametersList(Lists.newArrayList("java.util.Test"))), "constructor 1 reason");

        MethodDeprecation expectedConstructor2 = new MethodDeprecation(newConstructorDefinition(
                methodsBlockDefinition, new MethodParametersList(Lists.newArrayList("java.t1.Test1", "java.util.Test2"))),
                "constructor 2 reason");

        MethodDeprecation expectedConstructor3 = new MethodDeprecation(newConstructorDefinition(
                methodsBlockDefinition, new MethodParametersList(Lists.newArrayList("java.t1.Test1", "java.util.Test2"))),
                "constructor 3 reason");

        MethodDeprecation expectedConstructor4 = new MethodDeprecation(newConstructorDefinition(
                methodsBlockDefinition, new MethodParametersList(Lists.newArrayList("int", "java.util.Test2[][][]"))),
                "constructor 4 reason");

        DeprecationSettings settings = getDeprecatedSettings(VALID_CONSTRUCTORS_WITH_PARAMS);
        List<Deprecation> deprecationsList = settings.getDeprecations();
        List<DeprecatedListParseError> errorsList = settings.getErrors();

        assertTrue(errorsList.isEmpty());

        assertEquals(4, deprecationsList.size());
        assertEquals(expectedConstructor1, deprecationsList.get(0));
        assertEquals(expectedConstructor2, deprecationsList.get(1));
        assertEquals(expectedConstructor3, deprecationsList.get(2));
        assertEquals(expectedConstructor4, deprecationsList.get(3));
    }

    @Test
    public void should_parse_constructors_from_mult_classes() throws Exception {
        final ClassDefinition methodsBlock1 = new ClassDefinition("java.util", "Class1");
        MethodDeprecation expectedConstructor1 = new MethodDeprecation(newConstructorDefinition(methodsBlock1,
                new MethodParametersList()), "test1");

        final ClassDefinition methodsBlock2 = new ClassDefinition("java.util", "Class2");
        MethodDeprecation expectedConstructor2 = new MethodDeprecation(newConstructorDefinition(methodsBlock2,
                new MethodParametersList(Lists.newArrayList("java.util.Test"))), "test2");

        DeprecationSettings settings = getDeprecatedSettings(VALID_CONSTRUCTORS_FROM_MULT_CLASSES);
        List<Deprecation> deprecationsList = settings.getDeprecations();
        List<DeprecatedListParseError> errorsList = settings.getErrors();

        assertTrue(errorsList.isEmpty());

        assertEquals(2, deprecationsList.size());
        assertEquals(expectedConstructor1, deprecationsList.get(0));
        assertEquals(expectedConstructor2, deprecationsList.get(1));
    }

    @Test
    public void should_parse_packages() throws Exception {
        PackageDeprecation expectedPackage1 = new PackageDeprecation(new PackageDefinition("java.test1", false), "test1");

        PackageDeprecation expectedPackage2 = new PackageDeprecation(new PackageDefinition("java.test1.test2", false),
                "test1.test2");

        DeprecationSettings settings = getDeprecatedSettings(VALID_PACKAGES);
        List<Deprecation> deprecationsList = settings.getDeprecations();
        List<DeprecatedListParseError> errorsList = settings.getErrors();

        assertTrue(errorsList.isEmpty());

        assertEquals(2, deprecationsList.size());
        assertEquals(expectedPackage1, deprecationsList.get(0));
        assertEquals(expectedPackage2, deprecationsList.get(1));
    }

    @Test
    public void should_parse_packages_with_subpackages() throws Exception {
        PackageDeprecation expectedPackage1 = new PackageDeprecation(new PackageDefinition("java.test1", true),
                "test1 with subpackages");

        PackageDeprecation expectedPackage2 = new PackageDeprecation(new PackageDefinition("java.test2", false),
                "test2 without subpackages");

        DeprecationSettings settings = getDeprecatedSettings(VALID_PACKAGES_WITH_SUBPACKAGES);
        List<Deprecation> deprecationsList = settings.getDeprecations();
        List<DeprecatedListParseError> errorsList = settings.getErrors();

        assertTrue(errorsList.isEmpty());

        assertEquals(2, deprecationsList.size());
        assertEquals(expectedPackage1, deprecationsList.get(0));
        assertEquals(expectedPackage2, deprecationsList.get(1));
    }

    @Test
    public void should_return_error_for_lines_without_comments() throws Exception {
        final DeprecatedListParseError lackOfCommentAndSlashes = new DeprecatedListParseError(1, "package java.test",
                INCORRECT_PACKAGE_DEFINITION);
        final DeprecatedListParseError lackOfSlashes = new DeprecatedListParseError(2,
                "class java.lang.Throwable example bug with class", INCORRECT_CLASS_OR_METHOD_BLOCK_DEFINITION);
        final DeprecatedListParseError lackOfComment = new DeprecatedListParseError(4, "	getDefault() // ",
                INCORRECT_METHOD_DEFINITION);

        DeprecationSettings settings = getDeprecatedSettings(INVALID_DEPRECATIONS_WITHOUT_COMMENT);
        List<Deprecation> deprecationsList = settings.getDeprecations();
        List<DeprecatedListParseError> errorsList = settings.getErrors();

        assertTrue(deprecationsList.isEmpty());

        assertEquals(3, errorsList.size());

        DeprecatedListParseError error1 = errorsList.get(0);
        DeprecatedListParseError error2 = errorsList.get(1);
        DeprecatedListParseError error3 = errorsList.get(2);

        assertNotNull(error1);
        assertEquals(lackOfCommentAndSlashes, error1);

        assertNotNull(error2);
        assertEquals(lackOfSlashes, error2);

        assertNotNull(error3);
        assertEquals(lackOfComment, error3);
    }

    @Test
    public void should_return_error_for_lines_without_braces() throws Exception {
        final DeprecatedListParseError lackOfBraces = new DeprecatedListParseError(2, "	getDefault // example bug with method",
                INCORRECT_METHOD_DEFINITION);

        DeprecationSettings settings = getDeprecatedSettings(INVALID_METHODS_WITHOUT_BRACES);
        List<Deprecation> deprecationsList = settings.getDeprecations();
        List<DeprecatedListParseError> errorsList = settings.getErrors();

        assertTrue(deprecationsList.isEmpty());

        assertEquals(1, errorsList.size());
        DeprecatedListParseError error = errorsList.get(0);

        assertNotNull(error);
        assertEquals(lackOfBraces, error);
    }

    @Test
    public void should_return_nothing_for_empty_file() throws Exception {
        DeprecationSettings settings = getDeprecatedSettings(VALID_EMPTY);
        List<Deprecation> deprecationsList = settings.getDeprecations();
        List<DeprecatedListParseError> errorsList = settings.getErrors();

        assertTrue(deprecationsList.isEmpty());
        assertTrue(errorsList.isEmpty());
    }

    @Test
    public void should_return_error_for_methods_block_without_end_bracket() throws Exception {
        final DeprecatedListParseError lackOfEndBracket = new DeprecatedListParseError(4,
                "	method3(   int,    java.util.Test2  ) // method3", LACK_OF_THE_END_OF_METHOD_BLOCK);

        DeprecationSettings settings = getDeprecatedSettings(INVALID_METHODS_BLOCK_WITHOUT_END_BRACKET);
        List<Deprecation> deprecationsList = settings.getDeprecations();
        List<DeprecatedListParseError> errorsList = settings.getErrors();

        assertFalse(deprecationsList.isEmpty());

        assertEquals(1, errorsList.size());
        DeprecatedListParseError error = errorsList.get(0);

        assertNotNull(error);
        assertEquals(lackOfEndBracket, error);
    }

    private DeprecationSettings getDeprecatedSettings(String resource) throws IOException {
        InputStream in = getClass().getResourceAsStream(resource);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        return settingsLoader.settingsFromBufferedReader(reader);
    }

}
