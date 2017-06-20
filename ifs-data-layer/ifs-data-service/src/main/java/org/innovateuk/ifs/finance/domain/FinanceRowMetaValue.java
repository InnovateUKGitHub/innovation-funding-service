package org.innovateuk.ifs.finance.domain;

import javax.persistence.*;

/**
 * FinanceRowMetaValue defines database relations and a model to use client side and server side.
 * Holds the reference between the extra cost fields and the original cost.
 * The value is stored and the type determines how it is processed.
 */
@Entity
public class FinanceRowMetaValue {
    private String value;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long financeRowId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="finance_row_meta_field_id")
    private FinanceRowMetaField financeRowMetaField;

    public FinanceRowMetaValue() {
    	// no-arg constructor
    }

    public FinanceRowMetaValue(FinanceRowMetaField financeRowMetaField, String value) {
        this.financeRowMetaField = financeRowMetaField;
        this.value = value;
    }

    public FinanceRowMetaValue(FinanceRow financeRow, FinanceRowMetaField financeRowMetaField, String value) {
        this(financeRow.getId(), financeRowMetaField, value);
    }

    public FinanceRowMetaValue(Long financeRowId, FinanceRowMetaField financeRowMetaField, String value) {
        this.financeRowId = financeRowId;
        this.financeRowMetaField = financeRowMetaField;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Long getFinanceRowId() {
        return financeRowId;
    }

    public FinanceRowMetaField getFinanceRowMetaField() {
        return financeRowMetaField;
    }


    public void setFinanceRowId(Long financeRowId) {
        this.financeRowId = financeRowId;
    }

    public void setFinanceRowMetaField(FinanceRowMetaField financeRowMetaField) {
        this.financeRowMetaField = financeRowMetaField;
    }

    public Long getId(){ return id;}

    public void setValue(String value) {
        this.value = value;
    }
}
