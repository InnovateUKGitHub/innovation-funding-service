package org.innovateuk.ifs.config;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import static org.springframework.util.ReflectionUtils.doWithFields;

/**
 * A processor to take the fields from an object and add them to the local variable scope.
 *
 * This processor can be used to pass a view model into a fragment which is expecting values to be added to the spring
 * model instead of within a view model class. This will allow us to refactor common fragments from one usage at a time.
 */
public final class FieldsToGlobalProcessor
        extends AbstractStandardExpressionAttributeTagProcessor {
    private static final Log LOG = LogFactory.getLog(FieldsToGlobalProcessor.class);

    public static final int PRECEDENCE = 600;
    public static final String ATTR_NAME = "global";

    public FieldsToGlobalProcessor(final TemplateMode templateMode, final String dialectPrefix) {
        super(templateMode, dialectPrefix, ATTR_NAME, PRECEDENCE, true);
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, Object expressionResult, IElementTagStructureHandler structureHandler) {
        if (expressionResult == null) {
            throw new TemplateProcessingException(
                    String.format("Could not parse expression result from %s", attributeValue));
        }

        doWithFields(expressionResult.getClass(), field -> {
            String name = field.getName();
            Object value = getValue(expressionResult, name);
            structureHandler.setLocalVariable(name, value);
        });
    }

    private Object getValue(Object expressionResult, String name) {
        try {
            return PropertyUtils.getProperty(expressionResult, name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}