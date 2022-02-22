package org.innovateuk.ifs.application.forms.sections.hecpcosts.form;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class HorizonEuropeGuaranteeCostsForm {

    private BigInteger labour;
    private BigInteger overhead;
    private BigInteger material;
    private BigInteger capital;
    private BigInteger subcontracting;
    private BigInteger travel;
    private BigInteger other;

    /* view logic. */
    public BigInteger getOrganisationFinanceTotal() {
        return labour
                .add(overhead)
                .add(material)
                .add(capital)
                .add(subcontracting)
                .add(travel)
                .add(other);
    }
}