package org.ei.drishti.view.contract;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.ei.drishti.domain.FPMethod;
import org.ei.drishti.util.StringUtil;
import org.joda.time.LocalDate;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ECClient implements SmartRegisterClient {
    public static final String OUT_OF_AREA = "out_of_area";
    public static final String IN_AREA = "in_area";
    private String entityId;
    private String entityIdToSavePhoto;
    private String name;
    private String husbandName;
    private String dateOfBirth;
    private Integer ecNumber;
    private String village;
    private String fpMethod;
    private String numPregnancies;
    private String parity;
    private String numLivingChildren;
    private String numStillbirths;
    private String numAbortions;
    private boolean isHighPriority;
    private String familyPlanningMethodChangeDate;
    private String photo_path;
    private String caste;
    private String economicStatus;
    private String iudPlace;
    private String iudPerson;
    private String numberOfCondomsSupplied;
    private String numberOfCentchromanPillsDelivered;
    private String numberOfOCPDelivered;
    private String highPriorityReason;
    private String locationStatus;
    private List<ECChildClient> children;
    private Map<String, String> status;

    public ECClient(String entityId, String name, String husbandName, String village, Integer ecNumber) {
        this.entityId = entityId;
        this.entityIdToSavePhoto = entityId;
        this.name = name;
        this.husbandName = husbandName;
        this.village = village;
        this.ecNumber = ecNumber;
        this.children = new ArrayList<ECChildClient>();
    }

    public String wifeName() {
        return name;
    }

    @Override
    public String village() {
        return StringUtil.humanize(village);
    }

    public String name() {
        return StringUtil.humanize(name);
    }

    public String husbandName() {
        return StringUtil.humanize(husbandName);
    }

    //#TODO: Write unit test
    public int age() {
        LocalDate dob = LocalDate.parse(dateOfBirth);
        LocalDate now = LocalDate.now();
        return new Period(dob, now).getYears();
    }

    public Integer ecNumber() {
        return ecNumber;
    }

    public String locationStatus() {
        return locationStatus;
    }

    public boolean isSC() {
        return caste != null && caste.equalsIgnoreCase("SC");
    }

    public boolean isST() {
        return caste != null && caste.equalsIgnoreCase("ST");
    }

    public boolean isHighPriority() {
        return isHighPriority;
    }

    public boolean isBPL() {
        return economicStatus != null && economicStatus.equalsIgnoreCase("BPL");
    }

    public FPMethod fpMethod() {
        return FPMethod.tryParse(this.fpMethod, FPMethod.NONE);
    }

    public List<ECChildClient> children() {
        return children;
    }

    public Map<String, String> status() {
        return status;
    }

    public String entityId() {
        return entityId;
    }

    public String numberOfPregnancies() {
        return numPregnancies;
    }

    public String parity() {
        return parity;
    }

    public String numberOfLivingChildren() {
        return numLivingChildren;
    }

    public String numberOfStillbirths() {
        return numStillbirths;
    }

    public String numberOfAbortions() {
        return numAbortions;
    }

    public String familyPlanningMethodChangeDate() {
        return familyPlanningMethodChangeDate;
    }

    public String numberOfOCPDelivered() {
        return numberOfOCPDelivered;
    }

    public String numberOfCondomsSupplied() {
        return numberOfCondomsSupplied;
    }

    public String iudPerson() {
        return iudPerson;
    }

    public String iudPlace() {
        return iudPlace;
    }

    public ECClient withDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public ECClient withIsOutOfArea(boolean outOfArea) {
        this.locationStatus = outOfArea ? OUT_OF_AREA : IN_AREA;
        return this;
    }


    public ECClient withFPMethod(String fp_method) {
        this.fpMethod = fp_method;
        return this;
    }

    public ECClient withNumberOfPregnancies(String num_pregnancies) {
        this.numPregnancies = num_pregnancies;
        return this;
    }

    public ECClient withParity(String parity) {
        this.parity = parity;
        return this;
    }

    public ECClient withNumberOfLivingChildren(String num_living_children) {
        this.numLivingChildren = num_living_children;
        return this;
    }

    public ECClient withNumberOfStillBirths(String num_stillbirths) {
        this.numStillbirths = num_stillbirths;
        return this;
    }

    public ECClient withNumberOfAbortions(String num_abortions) {
        this.numAbortions = num_abortions;
        return this;
    }

    public ECClient withIsHighPriority(boolean highPriority) {
        isHighPriority = highPriority;
        return this;
    }

    public ECClient withFamilyPlanningMethodChangeDate(String family_planning_method_change_date) {
        this.familyPlanningMethodChangeDate = family_planning_method_change_date;
        return this;
    }

    public ECClient withPhotoPath(String photo_path) {
        this.photo_path = photo_path;
        return this;
    }

    public ECClient withCaste(String caste) {
        this.caste = caste;
        return this;
    }

    public ECClient withEconomicStatus(String economicStatus) {
        this.economicStatus = economicStatus;
        return this;
    }

    public ECClient withIUDPlace(String iudPlace) {
        this.iudPlace = iudPlace;
        return this;
    }

    public ECClient withIUDPerson(String iudPerson) {
        this.iudPerson = iudPerson;
        return this;
    }

    public ECClient withNumberOfCondomsSupplied(String numberOfCondomsSupplied) {
        this.numberOfCondomsSupplied = numberOfCondomsSupplied;
        return this;
    }

    public ECClient withNumberOfCentchromanPillsDelivered(String numberOfCentchromanPillsDelivered) {
        this.numberOfCentchromanPillsDelivered = numberOfCentchromanPillsDelivered;
        return this;
    }

    public ECClient withNumberOfOCPDelivered(String numberOfOCPDelivered) {
        this.numberOfOCPDelivered = numberOfOCPDelivered;
        return this;
    }

    public ECClient withHighPriorityReason(String highPriorityReason) {
        this.highPriorityReason = highPriorityReason;
        return this;
    }

    public ECClient withChildren(List<ECChildClient> children) {
        this.children = children;
        return this;
    }

    public ECClient addChild(ECChildClient childClient) {
        children.add(childClient);
        return this;
    }

    public ECClient withStatus(Map<String, String> status) {
        this.status = status;
        return this;
    }

    public boolean satisfiesFilter(String filter) {
        return name.toLowerCase().startsWith(filter)
                || String.valueOf(ecNumber).startsWith(filter)
                || village.toLowerCase().startsWith(filter);
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
