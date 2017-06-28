package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.finance.domain.Finance;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaValue;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Base class Builder for building FinanceRow subclasses.  THis base class holds the build steps common to all
 * FinanceRow subclasses.
 */
public abstract class BaseFinanceRowBuilder<FinanceType extends Finance, FinanceRowType extends FinanceRow<FinanceType>, S extends BaseFinanceRowBuilder<FinanceType, FinanceRowType, S>> extends BaseBuilder<FinanceRowType, S> {

    protected BaseFinanceRowBuilder(List<BiConsumer<Integer, FinanceRowType>> newMultiActions) {
        super(newMultiActions);
    }

    public S withItem(String... value){
        return withArray((v, financeRow) -> financeRow.setItem(v), value);
    }

    public S withDescription(String... value){
        return withArray((v, financeRow) -> financeRow.setDescription(v), value);
    }

    public S withQuantity(Integer... value){
        return withArray((v, financeRow) -> financeRow.setQuantity(v), value);
    }

    public S withCost(BigDecimal... value){
        return withArray((v, financeRow) -> financeRow.setCost(v), value);
    }

    public S withName(String... value){
        return withArray((v, financeRow) -> financeRow.setName(v), value);
    }

    public S withFinanceRowMetadata(List<FinanceRowMetaValue>... value){
        return withArray((v, financeRow) -> financeRow.setFinanceRowMetadata(v), value);
    }

    public S withOwningFinance(final FinanceType... owningFinance) {
        return withArray((finance, financeRow) -> financeRow.setTarget(finance), owningFinance);
    }

    public S withQuestion(Question... value){
        return withArray((v, financeRow) -> financeRow.setQuestion(v), value);
    }

    @Override
    protected void postProcess(int index, FinanceRowType instance) {
        super.postProcess(index, instance);

        // set hibernate-style back-links for Finance Row id
        instance.getFinanceRowMetadata().forEach(metaValue -> metaValue.setFinanceRowId(instance.getId()));
    }
}
