package org.innovateuk.ifs.finance.resource.cost;

import java.math.BigDecimal;
import java.math.BigInteger;


/**
 * {@code KnowledgeBaseCost} implements {@link FinanceRowItem}
 */
public class KnowledgeBaseCost extends AbstractFinanceRowItem {

    private Long id;

    private String description;

    private BigInteger cost;

    private KnowledgeBaseCost() {
        this(null);
    }

    public KnowledgeBaseCost(Long targetId) {
        super(targetId);
    }

    public KnowledgeBaseCost(Long targetId, Long id, String description, BigInteger cost) {
        super(targetId);
        this.id = id;
        this.description = description;
        this.cost = cost;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return FinanceRowType.KNOWLEDGE_BASE;
    }

    @Override
    public String getName() {
        return "Knowledge Base Cost";
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
