package hu.congressline.pcs.service.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@SuppressWarnings("checkstyle:HideUtilityClassConstructorCheck")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageUtil {

    @SuppressWarnings("MissingJavadocMethod")
    public static String parseDeleteConstraintViolationClassMessage(String message) {
        Pattern pattern = Pattern.compile("\\.`\\w+`,");
        Matcher matcher = pattern.matcher(message);
        String className = null;
        if (matcher.find()) {
            className = matcher.group();
        }
        if (className == null) {
            return null;
        }

        className = className.replace("`", "");
        className = className.replace(".", "");
        className = className.replace(",", "");

        return className;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public static String parseDeleteConstraintViolationReferenceMessage(String message) {
        Pattern pattern = Pattern.compile("FOREIGN\\sKEY\\s\\(`\\w+`\\)");
        Matcher matcher = pattern.matcher(message);
        String referenceName = null;
        if (matcher.find()) {
            referenceName = matcher.group();
        }

        if (referenceName == null) {
            return null;
        }

        referenceName = referenceName.substring(12);
        referenceName = referenceName.replace("`", "");
        referenceName = referenceName.replace("(", "");
        referenceName = referenceName.replace(")", "");
        return referenceName;
    }

}
