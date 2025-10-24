package hu.congressline.pcs.service.util;

import org.apache.commons.text.RandomStringGenerator;

/**
 * Utility class for generating random Strings.
 */
public final class RandomUtil {

    private static final int DEF_COUNT = 20;
    private static final int DEF_PAYMENT_TRXID_COUNT = 10;

    private RandomUtil() {
    }

    /**
     * Generates a unique id.
     *
     * @return the generated unique id.
     */
    public static String generateUniqueId() {
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(Character::isLetterOrDigit)
                .get();

        return generator.generate(DEF_COUNT);
    }

    /**
     * Generates a password.
     *
     * @return the generated password
     */
    public static String generatePassword() {
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(Character::isLetterOrDigit)
                .get();

        return generator.generate(DEF_COUNT);
    }

    /**
     * Generates an activation key.
     *
     * @return the generated activation key
     */
    public static String generateActivationKey() {
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(Character::isLetterOrDigit)
                .get();

        return generator.generate(DEF_COUNT);
    }

    /**
    * Generates a reset key.
    *
    * @return the generated reset key
    */
    public static String generateResetKey() {
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(Character::isLetterOrDigit)
                .get();

        return generator.generate(DEF_COUNT);
    }

    /**
     * Generates an online payment trx id.
     *
     * @return the generated trx id
     */
    public static String generatePaymentTrxId() {
        RandomStringGenerator generator = new RandomStringGenerator.Builder().withinRange('0', '9').get();
        return generator.generate(DEF_PAYMENT_TRXID_COUNT);
    }

}
