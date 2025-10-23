package hu.congressline.pcs.domain.enumeration;

import java.util.Arrays;

public enum Language {
    HU, EN, E, PT;

    @SuppressWarnings("MissingJavadocMethod")
    public static Language parse(String language) {
        if (Arrays.stream(Language.values()).map(Enum::toString).map(String::toLowerCase).noneMatch(value -> value.equals(language))) {
            throw new IllegalArgumentException("Language has to be HU, EN, ES, or PT!");
        } else {
            return Language.valueOf(language.toUpperCase());
        }
    }

}
