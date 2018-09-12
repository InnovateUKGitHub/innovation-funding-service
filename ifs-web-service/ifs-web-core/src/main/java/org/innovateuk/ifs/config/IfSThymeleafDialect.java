package org.innovateuk.ifs.config;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.HashSet;
import java.util.Set;

/**
 * Thymeleaf Dialect that registers new objects that can be used in OGNL or SpringEL expression evaluations like <tt>${#ifsUtil.doThis(obj)}</tt>
 */
public class IfSThymeleafDialect extends AbstractDialect implements IExpressionObjectDialect, IProcessorDialect {

    public IfSThymeleafDialect() {
        super("ifs");
    }

    @Override
    public IExpressionObjectFactory getExpressionObjectFactory() {
        return new IfsThymeleafExpressionObjectFactory();
    }

    @Override
    public String getPrefix() {
        return "ifs";
    }

    @Override
    public int getDialectProcessorPrecedence() {
        return 0;
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        Set<IProcessor> processors = new HashSet<>();
        processors.add(new EnhancedUtextProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new NonZeroValueProcessor(dialectPrefix));
        processors.add(new FieldsToGlobalProcessor(TemplateMode.HTML, dialectPrefix));
        return processors;
    }
}
