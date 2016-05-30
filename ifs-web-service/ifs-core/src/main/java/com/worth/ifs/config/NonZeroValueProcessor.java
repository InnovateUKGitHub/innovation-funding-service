package com.worth.ifs.config;

import com.worth.ifs.application.finance.view.item.NumberUtils;
import com.worth.ifs.exception.IntegerNumberFormatException;
import org.springframework.util.StringUtils;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.Element;
import org.thymeleaf.spring4.processor.attr.AbstractSpringFieldAttrProcessor;
import org.thymeleaf.spring4.requestdata.RequestDataValueProcessorUtils;
import org.thymeleaf.standard.processor.attr.AbstractStandardSingleAttributeModifierAttrProcessor;

import java.math.BigDecimal;

/**
 * this class is responsible for providing the value of the form input element.
 * But we don't whant value="0" on the input field, because we want to use the placeholder="0".
 * That is why the value 0 is never returned.
 *
 * Basically the same as {@link org.thymeleaf.spring4.processor.attr.SpringValueAttrProcessor} but the with replacing the zero.
 */
public final class NonZeroValueProcessor
        extends AbstractStandardSingleAttributeModifierAttrProcessor {

    // This is 1010 in order to make sure it is executed after "name" and "type"
    public static final int ATTR_PRECEDENCE = 1010;
    public static final String ATTR_NAME = "nonZeroValue";
    public static final String TARGET_ATTR_NAME = "value";

    public NonZeroValueProcessor() {
        super(ATTR_NAME);
    }

    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }

    @Override
    protected String getTargetAttributeName(
            final Arguments arguments, final Element element, final String attributeName) {
        return TARGET_ATTR_NAME;
    }

    @Override
    protected String getTargetAttributeValue(
            final Arguments arguments, final Element element, final String attributeName) {

        final String attributeValue = super.getTargetAttributeValue(arguments, element, attributeName);
        if (element.hasNormalizedAttribute(Attribute.getPrefixFromAttributeName(attributeName), AbstractSpringFieldAttrProcessor.ATTR_NAME)) {
            // There still is a th:field to be executed, so better not process the value ourselves (let's let th:field do it)
            return attributeValue;
        }

        final String name = element.getAttributeValueFromNormalizedName("name");
        final String type = element.getAttributeValueFromNormalizedName("type");
        String newValue = RequestDataValueProcessorUtils.processFormFieldValue(
                arguments.getConfiguration(), arguments, name, attributeValue, type);

        return emptyStringWhenZero(newValue);
    }

    private String emptyStringWhenZero(String newValue) {
        BigDecimal decimal = BigDecimal.ZERO;
        try{
            decimal = NumberUtils.getBigDecimalValue(newValue, new Double(0));
        }catch (IntegerNumberFormatException e ){
            // Ignore number format exceptions..
        }

        if (StringUtils.isEmpty(newValue) || decimal.equals(BigDecimal.ZERO)) {
            return "";
        } else {
            return newValue;
        }
    }


    @Override
    protected ModificationType getModificationType(
            final Arguments arguments, final Element element, final String attributeName, final String newAttributeName) {
        return ModificationType.SUBSTITUTION;
    }



    @Override
    protected boolean removeAttributeIfEmpty(
            final Arguments arguments, final Element element, final String attributeName, final String newAttributeName) {
        return false;
    }
}