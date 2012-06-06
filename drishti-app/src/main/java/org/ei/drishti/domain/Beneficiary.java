package org.ei.drishti.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Beneficiary {
    private final String caseId;
    private final String ecCaseId;
    private final String thaayiCardNumber;
    private final ChildStatus status;

    public Beneficiary(String caseId, String ecCaseId, String thaayiCardNumber, ChildStatus status) {
        this.caseId = caseId;
        this.ecCaseId = ecCaseId;
        this.thaayiCardNumber = thaayiCardNumber;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
