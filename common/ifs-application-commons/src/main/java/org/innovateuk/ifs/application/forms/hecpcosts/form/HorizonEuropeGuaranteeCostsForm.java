package org.innovateuk.ifs.application.forms.hecpcosts.form;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class HorizonEuropeGuaranteeCostsForm {

    private BigInteger labour;
    private BigInteger overhead;
    private BigInteger equipment;
    private BigInteger otherGoods;
    private BigInteger subcontracting;
    private BigInteger travel;
    private BigInteger other;

    public BigInteger getLabour() {
        return labour;
    }

    public void setLabour(BigInteger labour) {
        this.labour = labour;
    }

    public BigInteger getOverhead() {
        return overhead;
    }

    public void setOverhead(BigInteger overhead) {
        this.overhead = overhead;
    }

    public BigInteger getEquipment() {
        return equipment;
    }

    public void setEquipment(BigInteger equipment) {
        this.equipment = equipment;
    }

    public BigInteger getOtherGoods() {
        return otherGoods;
    }

    public void setOtherGoods(BigInteger otherGoods) {
        this.otherGoods = otherGoods;
    }

    public BigInteger getSubcontracting() {
        return subcontracting;
    }

    public void setSubcontracting(BigInteger subcontracting) {
        this.subcontracting = subcontracting;
    }

    public BigInteger getTravel() {
        return travel;
    }

    public void setTravel(BigInteger travel) {
        this.travel = travel;
    }

    public BigInteger getOther() {
        return other;
    }

    public void setOther(BigInteger other) {
        this.other = other;
    }

    /* view logic. */
    public BigInteger getOrganisationFinanceTotal() {
        return labour
                .add(overhead)
                .add(equipment)
                .add(otherGoods)
                .add(subcontracting)
                .add(travel)
                .add(other);
    }
}