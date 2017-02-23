package org.innovateuk.ifs.config;

import org.innovateuk.ifs.util.ThymeleafUtil;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.expression.IExpressionObjectFactory;

import java.util.Set;

import static java.util.Collections.singleton;

/**
 * Factory for creating the expression objects that are made available in Thymeleaf expressions e.g. #ifsUtil
 */
public class IfsThymeleafExpressionObjectFactory implements IExpressionObjectFactory {

    public static final ThymeleafUtil THYMELEAF_UTIL = new ThymeleafUtil();

    @Override
    public Set<String> getAllExpressionObjectNames() {
        return singleton("ifsUtil");
    }

    @Override
    public Object buildObject(IExpressionContext context, String expressionObjectName) {
        switch (expressionObjectName) {
            case "ifsUtil": return THYMELEAF_UTIL;
            default: throw new IllegalArgumentException("Unable to build an expression object of name " + expressionObjectName);
        }
    }

    @Override
    public boolean isCacheable(String expressionObjectName) {
        return true;
    }
}
