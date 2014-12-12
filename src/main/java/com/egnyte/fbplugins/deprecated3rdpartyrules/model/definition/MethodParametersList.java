package com.egnyte.fbplugins.deprecated3rdpartyrules.model.definition;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Value;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

@Value
@AllArgsConstructor
public class MethodParametersList implements MethodParameters {
    List<String> parameters;

    public MethodParametersList() {
        this.parameters = Lists.newArrayList();
    }

    @Override
    public boolean matchesTo(MethodParameters definition) {
        if (definition instanceof MethodParametersList) {
            MethodParametersList methodParametersListDef = (MethodParametersList) definition;
            return parameters.equals(methodParametersListDef.parameters);
        }

        return false;
    }

    @Override
    public String toString() {
        return StringUtils.join(parameters, ", ");
    }
}
