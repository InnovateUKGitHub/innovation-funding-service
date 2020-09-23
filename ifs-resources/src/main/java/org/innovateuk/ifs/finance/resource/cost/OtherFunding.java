package org.innovateuk.ifs.finance.resource.cost;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.Digits;
import java.math.BigDecimal;

public class OtherFunding extends BaseOtherFunding {

    public OtherFunding() {
        this(null);
    }

    public OtherFunding(Long targetId) {
        super(targetId);
    }

    public OtherFunding(Long id, String otherPublicFunding, String fundingSource, String securedDate, BigDecimal fundingAmount, Long targetId) {
        super(id, otherPublicFunding, fundingSource, securedDate, fundingAmount, targetId);
    }

    @Override
    public FinanceRowType getCostType() {
        return FinanceRowType.OTHER_FUNDING;
    }
}
