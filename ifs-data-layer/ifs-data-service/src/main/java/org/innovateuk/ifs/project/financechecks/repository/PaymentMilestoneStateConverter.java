package org.innovateuk.ifs.project.financechecks.repository;

import org.innovateuk.ifs.commons.util.IdentifiableEnumConverter;
import org.innovateuk.ifs.project.finance.resource.PaymentMilestoneState;

import javax.persistence.Converter;

@Converter(autoApply = true)
@SuppressWarnings(value = "unused")
public class PaymentMilestoneStateConverter extends IdentifiableEnumConverter<PaymentMilestoneState> {

    public PaymentMilestoneStateConverter() {
        super(PaymentMilestoneState.class);
    }

    @Override
    public Long convertToDatabaseColumn(PaymentMilestoneState attribute) {
        return super.convertToDatabaseColumn(attribute);
    }

    @Override
    public PaymentMilestoneState convertToEntityAttribute(Long dbData) {
        return super.convertToEntityAttribute(dbData);
    }
}
