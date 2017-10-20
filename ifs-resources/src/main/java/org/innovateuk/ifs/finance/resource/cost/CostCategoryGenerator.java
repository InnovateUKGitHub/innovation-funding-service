package org.innovateuk.ifs.finance.resource.cost;


public interface CostCategoryGenerator<T> extends Comparable<T> {

    boolean isIncludedInSpendProfile();

    String getName();

    String getLabel();
}
