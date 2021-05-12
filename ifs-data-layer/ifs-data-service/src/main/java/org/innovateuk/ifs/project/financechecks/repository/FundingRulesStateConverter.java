package org.innovateuk.ifs.project.financechecks.repository;

import org.innovateuk.ifs.commons.util.IdentifiableEnumConverter;
import org.innovateuk.ifs.project.finance.resource.FundingRulesState;

import javax.persistence.Converter;

@Converter(autoApply = true)
@SuppressWarnings(value = "unused")
public class FundingRulesStateConverter extends IdentifiableEnumConverter<FundingRulesState> {

    public FundingRulesStateConverter() {
        super(FundingRulesState.class);
    }

    @Override
    public Long convertToDatabaseColumn(FundingRulesState attribute) {
        return super.convertToDatabaseColumn(attribute);
    }

    @Override
    public FundingRulesState convertToEntityAttribute(Long dbData) {
        return super.convertToEntityAttribute(dbData);
    }
}
