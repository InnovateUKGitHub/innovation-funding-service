package com.worth.ifs.config;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractUnescapedTextChildModifierAttrProcessor;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.standard.expression.StandardExpressions;

/**
 * this class id responsible for the escaping of <script> tags inside thymeleaf code. it does the same as a th:utext tag except for the fact that it escapes the <script> tags
 */

class EnhancedUtextProcessor extends AbstractUnescapedTextChildModifierAttrProcessor {

    EnhancedUtextProcessor() {
        super("utext");
    }

    @Override
    protected String getText(final Arguments arguments, final Element element, final String attributeName) {
        final String attributeValue = element.getAttributeValue(attributeName);

        final Configuration configuration = arguments.getConfiguration();
        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);

        final IStandardExpression expression = expressionParser.parseExpression(configuration, arguments, attributeValue);

        final Object result =
            expression.execute(configuration, arguments, StandardExpressionExecutionContext.UNESCAPED_EXPRESSION);

        return (result == null? "" : result.toString()).replaceAll("(?i)<(/?script[^>]*)>", "&lt;$1&gt;");
    }

    @Override public int getPrecedence() {
        return 1000;
    }
}
