package hu.congressline.pcs.service.util;

import java.util.Arrays;
import java.util.Optional;

public enum RegistrationUploadHeader {
    REG_UPLOAD_TITLE("title"),
    REG_UPLOAD_FAMILY_NAME("family_name"),
    REG_UPLOAD_FIRST_NAME("first_name"),
    REG_UPLOAD_POSITION("position"),
    REG_UPLOAD_OTHER_DATA("other_data"),
    REG_UPLOAD_DEPARTMENT("department"),
    REG_UPLOAD_COUNTRY("country"),
    REG_UPLOAD_ZIP_CODE("zip_code"),
    REG_UPLOAD_CITY("city"),
    REG_UPLOAD_STREET("street"),
    REG_UPLOAD_PHONE("phone"),
    REG_UPLOAD_EMAIL("email"),
    REG_UPLOAD_FAX("fax"),
    REG_UPLOAD_INVOICE_NAME("invoice_name"),
    REG_UPLOAD_INVOICE_COUNTRY("invoice_country"),
    REG_UPLOAD_INVOICE_ZIP_CODE("invoice_zip_code"),
    REG_UPLOAD_INVOICE_CITY("invoice_city"),
    REG_UPLOAD_INVOICE_ADDRESS("invoice_address"),
    REG_UPLOAD_INVOICE_TAX_NUMBER("invoice_tax_number"),
    
    REG_UPLOAD_WORKPLACE_NAME("workplace_name"),
    REG_UPLOAD_WORKPLACE_VAT_REG_NUMBER("workplace_vat_reg_number"),
    REG_UPLOAD_WORKPLACE_DEPARTMENT("workplace_department"),
    REG_UPLOAD_WORKPLACE_ZIP_CODE("workplace_zip_code"),
    REG_UPLOAD_WORKPLACE_CITY("workplace_city"),
    REG_UPLOAD_WORKPLACE_STREET("workplace_street"),
    REG_UPLOAD_WORKPLACE_PHONE("workplace_phone"),
    REG_UPLOAD_WORKPLACE_FAX("workplace_fax"),
    REG_UPLOAD_WORKPLACE_EMAIL("workplace_email"),
    REG_UPLOAD_WORKPLACE_COUNTRY("workplace_country");

    private final String headerName;

    RegistrationUploadHeader(String headerName) {
        this.headerName = headerName;
    }

    public String getHeaderName() {
        return headerName;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public static Optional<RegistrationUploadHeader> getHeaderByName(String headerName) {
        return Arrays.stream(values())
                .filter(header -> header.getHeaderName().equals(headerName))
                .findFirst();
    }
}
