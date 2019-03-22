package org.innovateuk.ifs.application.forms.sections.h2020costs.form;

import java.math.BigDecimal;

public class Horizon2020CostsForm {

    private BigDecimal labour;
    private BigDecimal overhead;
    private BigDecimal material;
    private BigDecimal capital;
    private BigDecimal subcontracting;
    private BigDecimal travel;
    private BigDecimal other;

    public BigDecimal getLabour() {
        return labour;
    }

    public void setLabour(BigDecimal labour) {
        this.labour = labour;
    }

    public BigDecimal getOverhead() {
        return overhead;
    }

    public void setOverhead(BigDecimal overhead) {
        this.overhead = overhead;
    }

    public BigDecimal getMaterial() {
        return material;
    }

    public void setMaterial(BigDecimal material) {
        this.material = material;
    }

    public BigDecimal getCapital() {
        return capital;
    }

    public void setCapital(BigDecimal capital) {
        this.capital = capital;
    }

    public BigDecimal getSubcontracting() {
        return subcontracting;
    }

    public void setSubcontracting(BigDecimal subcontracting) {
        this.subcontracting = subcontracting;
    }

    public BigDecimal getTravel() {
        return travel;
    }

    public void setTravel(BigDecimal travel) {
        this.travel = travel;
    }

    public BigDecimal getOther() {
        return other;
    }

    public void setOther(BigDecimal other) {
        this.other = other;
    }

    /* view logic. */
    public BigDecimal getOrganisationFinanceTotal() {
        return labour
                .add(overhead)
                .add(material)
                .add(capital)
                .add(subcontracting)
                .add(travel)
                .add(other);
    }
}
