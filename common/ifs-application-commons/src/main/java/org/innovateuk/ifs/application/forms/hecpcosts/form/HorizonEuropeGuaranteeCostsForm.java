package org.innovateuk.ifs.application.forms.hecpcosts.form;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;


@Setter
@Getter
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

}