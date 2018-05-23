package org.innovateuk.ifs.config;

import org.innovateuk.ifs.exception.IntegerNumberFormatException;
import org.innovateuk.ifs.util.NumberUtils;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeDefinition;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.IAttributeDefinitionsAware;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor;
import org.thymeleaf.standard.util.StandardProcessorUtils;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;
import org.unbescape.html.HtmlEscape;

import java.math.BigDecimal;

/**
 * this class is responsible for providing the value of the form input element.
 * But we don't whant value="0" on the input field, because we want to use the placeholder="0".
 * That is why the value 0 is never returned.
 *
 * Basically the same as org.thymeleaf.spring4.processor.attr.SpringValueAttrProcessor but the with replacing the zero.
 */
public final class NonZeroValueProcessor
        extends AbstractStandardExpressionAttributeTagProcessor
        implements IAttributeDefinitionsAware {

    // This is 1010 in order to make sure it is executed after "name" and "type"
    public static final int PRECEDENCE = 1010;
    public static final String ATTR_NAME = "nonZeroValue";
    public static final String TARGET_ATTR_NAME = "value";

    public NonZeroValueProcessor(final String dialectPrefix) {
        this(TemplateMode.HTML, dialectPrefix, ATTR_NAME, TARGET_ATTR_NAME, PRECEDENCE, false);
    }

    private final boolean removeIfEmpty;
    private final String targetAttrCompleteName;

    private AttributeDefinition targetAttributeDefinition;

    private NonZeroValueProcessor(
            final TemplateMode templateMode, final String dialectPrefix,
            final String attrName, final String targetAttrCompleteName,
            final int precedence, final boolean removeIfEmpty) {

        super(templateMode, dialectPrefix, attrName, precedence, false);

        Validate.notNull(targetAttrCompleteName, "Complete name of target attribute cannot be null");

        this.targetAttrCompleteName = targetAttrCompleteName;
        this.removeIfEmpty = removeIfEmpty;

    }

    public void setAttributeDefinitions(final AttributeDefinitions attributeDefinitions) {
        Validate.notNull(attributeDefinitions, "Attribute Definitions cannot be null");
        // We precompute the AttributeDefinition of the target attribute in order to being able to use much
        // faster methods for setting/replacing attributes on the ElementAttributes implementation
        this.targetAttributeDefinition = attributeDefinitions.forName(getTemplateMode(), this.targetAttrCompleteName);
    }

    @Override
    protected final void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final Object expressionResult,
            final IElementTagStructureHandler structureHandler) {

        final String newAttributeValue = HtmlEscape.escapeHtml4Xml(expressionResult == null ? null : expressionResult.toString());

        // These attributes might be "removable if empty", in which case we would simply remove the target attribute...
        if (this.removeIfEmpty && (newAttributeValue == null || newAttributeValue.length() == 0)) {

            // We are removing the equivalent attribute name, without the prefix...
            structureHandler.removeAttribute(this.targetAttributeDefinition.getAttributeName());
            structureHandler.removeAttribute(attributeName);

        } else {

            // We are setting the equivalent attribute name, without the prefix...
            StandardProcessorUtils.replaceAttribute(
                    structureHandler, attributeName, this.targetAttributeDefinition, this.targetAttrCompleteName, (newAttributeValue == null ? "" : emptyStringWhenZero(newAttributeValue)));

        }
    }

    private String emptyStringWhenZero(String newValue) {
        BigDecimal decimal = BigDecimal.ZERO;
        try{
            decimal = NumberUtils.getBigDecimalValue(newValue, new Double(0));
        }catch (IntegerNumberFormatException ignored ){
            // Ignore number format exceptions..
        }

        if (StringUtils.isEmpty(newValue) || decimal.equals(BigDecimal.ZERO)) {
            return "";
        } else {
            return newValue;
        }
    }
}
