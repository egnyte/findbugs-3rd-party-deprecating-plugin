package com.egnyte.fbplugins.deprecated3rdpartyrules;

import static com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition.MethodDefinition.newConstructorDefinition;
import static com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition.MethodDefinition.newMethodDefinition;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition.ClassDefinition;
import com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition.MethodDefinition;
import com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition.MethodParametersList;
import com.google.common.collect.Lists;

public class SignatureParserUnitTest {

    private SignatureParser signatureParser;

    @Before
    public void setUp() {
        this.signatureParser = new SignatureParser();
    }

    @Test
    public void should_parse_class_signature() {
        final String classSignature = "java.lang.Throwable";

        ClassDefinition expectedClassDef = new ClassDefinition("java.lang", "Throwable");

        assertThat(signatureParser.parseClassSignature(classSignature), equalTo(expectedClassDef));
    }

    @Test
    public void should_parse_method_signature_with_no_parameter() {
        final String methodSignature = "java.lang.Throwable.printStackTrace : ()V";

        MethodDefinition expectedMethodDef = newMethodDefinition(new ClassDefinition("java.lang", "Throwable"),
                "printStackTrace", new MethodParametersList());

        assertThat(signatureParser.parseMethodSignature(methodSignature), equalTo(expectedMethodDef));
    }

    @Test
    public void should_parse_method_signature_with_parameters() {
        final String methodSignature = "java.util.TimeZone.getTimeZone : (Ljava.lang.String;I[[Ljava.util.TimeZone;B)Ljava.util.TimeZone;";

        MethodDefinition expectedMethodDef = newMethodDefinition(new ClassDefinition("java.util", "TimeZone"), "getTimeZone",
                new MethodParametersList(Lists.newArrayList("java.lang.String", "int", "java.util.TimeZone[][]", "byte")));

        assertThat(signatureParser.parseMethodSignature(methodSignature), equalTo(expectedMethodDef));
    }

    @Test
    public void should_parse_constructor_signature_with_parameters() {
        final String constructorSignature = "java.math.BigDecimal.<init> : (I)V";

        MethodDefinition expectedConstructorDef = newConstructorDefinition(new ClassDefinition("java.math",
                "BigDecimal"), new MethodParametersList(Lists.newArrayList("int")));

        assertThat(signatureParser.parseMethodSignature(constructorSignature), equalTo(expectedConstructorDef));
    }

    @Test
    public void should_return_method_argument_and_returned_value_signature() {
        final String methodSignature = "java.util.TimeZone.getTimeZone : (Ljava.lang.String;I[[Ljava.util.TimeZone;B)Ljava.util.TimeZone;";

        final String expectedSignature = "(Ljava.lang.String;I[[Ljava.util.TimeZone;B)Ljava.util.TimeZone;";
        assertThat(signatureParser.getMethodArgumentAndReturnedValueSignature(methodSignature), equalTo(expectedSignature));
    }

    @Test
    public void should_parse_byte_signature() {
        assertThat(signatureParser.parseParameterSignature("B"), equalTo("byte"));
    }

    @Test
    public void should_parse_char_signature() {
        assertThat(signatureParser.parseParameterSignature("C"), equalTo("char"));
    }

    @Test
    public void should_parse_double_signature() {
        assertThat(signatureParser.parseParameterSignature("D"), equalTo("double"));
    }

    @Test
    public void should_parse_float_signature() {
        assertThat(signatureParser.parseParameterSignature("F"), equalTo("float"));
    }

    @Test
    public void should_parse_int_signature() {
        assertThat(signatureParser.parseParameterSignature("I"), equalTo("int"));
    }

    @Test
    public void should_parse_long_signature() {
        assertThat(signatureParser.parseParameterSignature("J"), equalTo("long"));
    }

    @Test
    public void should_parse_short_signature() {
        assertThat(signatureParser.parseParameterSignature("S"), equalTo("short"));
    }

    @Test
    public void should_parse_boolean_signature() {
        assertThat(signatureParser.parseParameterSignature("Z"), equalTo("boolean"));
    }

    public void should_parse_string_class_signature() {
        assertThat(signatureParser.parseParameterSignature("Ljava.lang.String;"), equalTo("java.lang.String"));
    }

    @Test
    public void should_parse_byte_array_signature() {
        assertThat(signatureParser.parseParameterSignature("[B"), equalTo("byte[]"));
    }

    @Test
    public void should_parse_object_class_array_signature() {
        assertThat(signatureParser.parseParameterSignature("[Ljava.lang.Object;"), equalTo("java.lang.Object[]"));
    }

    @Test
    public void should_parse_two_dimensional_char_array_signature() {
        assertThat(signatureParser.parseParameterSignature("[[C"), equalTo("char[][]"));
    }

    @Test
    public void should_parse_three_dimensional_list_object_array_signature() {
        assertThat(signatureParser.parseParameterSignature("[[[Ljava.util.List;"), equalTo("java.util.List[][][]"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_not_parse_null_string() {
        signatureParser.parseParameterSignature(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_not_parse_empty_string() {
        signatureParser.parseParameterSignature("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_not_parse_incorrect_signature() {
        signatureParser.parseParameterSignature("Q");
    }
}
