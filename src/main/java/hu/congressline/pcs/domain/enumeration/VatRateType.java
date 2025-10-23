package hu.congressline.pcs.domain.enumeration;

public enum VatRateType {
    REGULAR,
    // VATExemption case
    NAM, // Egyéb nemzetközi ügyletekhez kapcsolódó jogcímen megállapított adómentesség
    //vatOutOfScope case
    ATK, //Áfa tárgyi hatályán kívül
    EUFAD37, //Áfa tv. 37. §-a alapján másik tagállamban teljesített, fordítottan adózó ügylet - EU-ba nyújtott szolgáltatás
    EUFADE, //Másik tagállamban teljesített, nem az Áfa tv. 37. §-a alá tartozó, fordítottan adózó ügylet - EU-ba értékesített termék
    EUE, //Másik tagállamban teljesített, nem fordítottan adózó ügylet  a teljesítés és az ÁFA fizetés az EU-ban van
    HO, //Harmadik országban teljesített ügylet
    TAM //Tárgyi adómentes ill. a tevékenység közérdekű vagy speciális jellegére tekintettel adómentes

}
