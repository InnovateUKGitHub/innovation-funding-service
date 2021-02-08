package org.innovateuk.ifs.finance.resource.cost;

import java.math.BigDecimal;
import java.math.BigInteger;


/**
 * {@code AcademicAndSecretarialSupport} implements {@link FinanceRowItem}
 */
public class AcademicAndSecretarialSupport extends AbstractFinanceRowItem {

    private Long id;

    private String role;

    private Integer duration;

    private BigInteger cost;

    private AcademicAndSecretarialSupport() {
        this(null);
    }

    public AcademicAndSecretarialSupport(Long targetId) {
        super(targetId);
    }

    public AcademicAndSecretarialSupport(Long targetId, Long id, String role, Integer duration, BigInteger cost) {
        super(targetId);
        this.id = id;
        this.role = role;
        this.duration = duration;
        this.cost = cost;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
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
