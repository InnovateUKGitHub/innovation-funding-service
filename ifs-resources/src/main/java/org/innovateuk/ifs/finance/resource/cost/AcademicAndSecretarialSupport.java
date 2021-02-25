package org.innovateuk.ifs.finance.resource.cost;

import java.math.BigDecimal;
import java.math.BigInteger;


/**
 * {@code AcademicAndSecretarialSupport} implements {@link FinanceRowItem}
 */
public class AcademicAndSecretarialSupport extends AbstractFinanceRowItem {

    private Long id;

    private BigInteger cost;

    public AcademicAndSecretarialSupport() {
        this(null);
    }

    public AcademicAndSecretarialSupport(Long targetId) {
        super(targetId);
    }

    public AcademicAndSecretarialSupport(Long targetId, Long id, BigInteger cost) {
        super(targetId);
        this.id = id;
        this.cost = cost;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigInteger getCost() {
        return cost;
    }

    public void setCost(BigInteger cost) {
        this.cost = cost;
    }

    @Override
    public BigDecimal getTotal() {
        return bigDecimalOrNull(cost);
    }

    @Override
    public FinanceRowType getCostType() {
        return FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT;
    }

    @Override
    public String getName() {
        return "Academic and secretarial support";
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
