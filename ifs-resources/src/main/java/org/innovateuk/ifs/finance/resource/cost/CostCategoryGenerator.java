package org.innovateuk.ifs.finance.resource.cost;


public interface CostCategoryGenerator<T> extends Comparable<T> {

    boolean isIncludedInGeneratedSpendProfile();

    String getName();

    String getLabel();
}
