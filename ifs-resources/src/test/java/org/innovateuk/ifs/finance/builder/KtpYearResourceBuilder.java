package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.resource.KtpYearResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.createDefault;

public class KtpYearResourceBuilder extends BaseBuilder<KtpYearResource, KtpYearResourceBuilder> {

    protected KtpYearResourceBuilder() {
        super();
    }

    protected KtpYearResourceBuilder(List<BiConsumer<Integer, KtpYearResource>> newActions) {
        super(newActions);
    }

    public static KtpYearResourceBuilder newKtpYearResource() {
        return new KtpYearResourceBuilder();
    }
    
    @Override
    protected KtpYearResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer,
            KtpYearResource>> actions) {
        return new KtpYearResourceBuilder(actions);
    }

    @Override
    protected KtpYearResource createInitial() {
        return createDefault(KtpYearResource.class);
    }

    public KtpYearResourceBuilder withYear(Integer... array) {
        return withArray((item, resource) -> resource.setYear(item), array);
    }
    public KtpYearResourceBuilder withTurnover(BigDecimal... array) {
        return withArray((item, resource) -> resource.setTurnover(item), array);
    }
    public KtpYearResourceBuilder withPreTaxProfit(BigDecimal... array) {
        return withArray((item, resource) -> resource.setPreTaxProfit(item), array);
    }
    public KtpYearResourceBuilder withCurrentAssets(BigDecimal... array) {
        return withArray((item, resource) -> resource.setCurrentAssets(item), array);
    }
    public KtpYearResourceBuilder withLiabilities(BigDecimal... array) {
        return withArray((item, resource) -> resource.setLiabilities(item), array);
    }
    public KtpYearResourceBuilder withShareholderValue(BigDecimal... array) {
        return withArray((item, resource) -> resource.setShareholderValue(item), array);
    }
    public KtpYearResourceBuilder withLoans(BigDecimal... array) {
        return withArray((item, resource) -> resource.setLoans(item), array);
    }
    public KtpYearResourceBuilder withEmployees(Long... array) {
        return withArray((item, resource) -> resource.setEmployees(item), array);
    }

}
