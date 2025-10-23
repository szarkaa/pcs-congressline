package hu.congressline.pcs.domain.enumeration;

public enum NavVatCategory {
    DOMESTIC_NORMAL_VAT_TAX_NUMBER, //magyar cég, áfaalany, adószám kell
    PRIVATE_PERSON, // magánszemély (belföldi/külföldi) adószám nincs
    EU_VAT_EU_TAX_NUMBER, // EUs cég, Áfás, adószám nem kötelező
    OTHER_COUNTRY, // EU-n kívüli országbeli cég, adószám nem kötelező (Áfás, vagy nem Áfás)
    EU_NO_VAT_EU_TAX_NUMBER, // EUs cég, Áfamentes (RV), EUs adószám
    OTHER_CUSTOMER, // egyéb adóalany – pl. testület, szervezet
    DOMESTIC_GROUP_VAT_TAX_NUMBER // magyar áfaalany, csoportos adószám
}
