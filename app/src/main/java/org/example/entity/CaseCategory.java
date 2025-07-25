package org.example.entity;

public enum CaseCategory {
    THEFT("Theft"),
    FRAUD("Fraud"),
    CYBERCRIME("Cybercrime"),
    ASSAULT("Assault"),
    ROBBERY("Robbery"),
    MURDER("Murder"),
    DRUG_OFFENSE("Drug Offense"),
    BURGLARY("Burglary"),
    VANDALISM("Vandalism"),
    KIDNAPPING("Kidnapping"),
    DOMESTIC_VIOLENCE("Domestic Violence"),
    WHITE_COLLAR_CRIME("White Collar Crime"),
    IDENTITY_THEFT("Identity Theft"),
    MONEY_LAUNDERING("Money Laundering"),
    HUMAN_TRAFFICKING("Human Trafficking"),
    TERRORISM("Terrorism"),
    CORRUPTION("Corruption"),
    TAX_EVASION("Tax Evasion"),
    COUNTERFEITING("Counterfeiting"),
    INTELLECTUAL_PROPERTY("Intellectual Property"),
    ENVIRONMENTAL_CRIME("Environmental Crime"),
    ORGANIZED_CRIME("Organized Crime"),
    SEXUAL_OFFENSE("Sexual Offense"),
    CHILD_ABUSE("Child Abuse"),
    ELDER_ABUSE("Elder Abuse"),
    MISSING_PERSON("Missing Person"),
    COLD_CASE("Cold Case"),
    DIGITAL_FORENSICS("Digital Forensics"),
    FINANCIAL_INVESTIGATION("Financial Investigation"),
    FORENSIC_ACCOUNTING("Forensic Accounting");

    private final String displayName;

    CaseCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
