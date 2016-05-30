package com.worth.ifs.config;

import org.springframework.util.StringUtils;
import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractUnescapedTextChildModifierAttrProcessor;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.standard.expression.StandardExpressions;

/**
 * this class is responsible for getting the value of the form input element.
 * But we don't what value="0" on the input field, because we want to use the placeholder="0".
 * That is why the value 0 is never returned.
 */
class NonZeroValueProcessor extends AbstractUnescapedTextChildModifierAttrProcessor {

    NonZeroValueProcessor() {
        super("nonZeroValue");
    }

    @Override
    protected String getText(final Arguments arguments, final Element element, final String attributeName) {
        final String attributeValue = element.getAttributeValue(attributeName);

        final Configuration configuration = arguments.getConfiguration();
        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);

        final IStandardExpression expression = expressionParser.parseExpression(configuration, arguments, attributeValue);

        final Object result =
                expression.execute(configuration, arguments, StandardExpressionExecutionContext.UNESCAPED_EXPRESSION);
        if (result == null) {
            return "";
        }
        String textValue = result.toString();

        if (StringUtils.isEmpty(textValue) || Integer.parseInt(textValue) == 0) {
            return "";
        } else {
            return textValue;
        }
    }

    @Override
    public int getPrecedence() {
        return 1000;
    }
}
