package uk.co.robmarch.voxsmart;

import java.util.Map;
import java.util.Optional;

public class NumberParser {

    private static final String INTERNATIONAL_CALL_TOKEN = "+";

    private final Map<String, Integer> countryCodes;
    private final Map<String, String> nationalTrunkPrefixes;

    public NumberParser(Map<String, Integer> countryCodes, Map<String, String> nationalTrunkPrefixes) {
        this.countryCodes = countryCodes;
        this.nationalTrunkPrefixes = nationalTrunkPrefixes;
    }

    public String parse(String dialledNumber, String userNumber) {
        if (isInternationalNumber(dialledNumber)) {
            validateCountryCode(dialledNumber);
            return dialledNumber;
        }
        String countryKey = validateCountryCode(userNumber);
        return internationalise(dialledNumber, countryKey);
    }

    private boolean isInternationalNumber(String number) {
        return number.startsWith(INTERNATIONAL_CALL_TOKEN);
    }

    private String validateCountryCode(String number) {
        return getCountryKey(number)
                .orElseThrow(() -> new IllegalArgumentException("Unknown country code for number " + number));
    }

    private Optional<String> getCountryKey(String number) {
        return countryCodes.entrySet().stream()
                .filter(countryCodeEntry -> isCallToCountry(number, countryCodeEntry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst();
    }

    private boolean isCallToCountry(String number, Integer countryCode) {
        return number.startsWith(INTERNATIONAL_CALL_TOKEN + countryCode);
    }

    private String internationalise(String dialledNumber, String userCountry) {
        String userLocalTrunkPrefix = Optional.ofNullable(nationalTrunkPrefixes.get(userCountry))
                .orElseThrow(() -> new IllegalStateException("No trunk prefix found for country " + userCountry));
        if (!dialledNumber.startsWith(userLocalTrunkPrefix)) {
            throw new IllegalArgumentException("Dialled number with invalid trunk prefix");
        }
        return dialledNumber.replaceFirst(userLocalTrunkPrefix, INTERNATIONAL_CALL_TOKEN + countryCodes.get(userCountry));
    }
}
