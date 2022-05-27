package org.innovateuk.ifs.application.forms.hecpcosts.form;

import java.math.BigInteger;


public class HorizonEuropeGuaranteeCostsForm {

    private BigInteger personnel;
    private BigInteger hecpIndirectCosts;
    private BigInteger equipment;
    private BigInteger otherGoods;
    private BigInteger subcontracting;
    private BigInteger travel;
    private BigInteger other;

    /* view logic. */
    public BigInteger getOrganisationFinanceTotal() {
        return personnel
                .add(hecpIndirectCosts)
                .add(equipment)
                .add(otherGoods)
                .add(subcontracting)
                .add(travel)
                .add(other);
    }

    public BigInteger getPersonnel() {
        return personnel;
    }

    public void setPersonnel(BigInteger personnel) {
        this.personnel = personnel;
    }

    public BigInteger getHecpIndirectCosts() {
        return hecpIndirectCosts;
    }

    public void setHecpIndirectCosts(BigInteger hecpIndirectCosts) {
        this.hecpIndirectCosts = hecpIndirectCosts;
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
}