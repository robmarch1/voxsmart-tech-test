package uk.co.robmarch.voxsmart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NumberParserTest {

    private static final String UNITED_KINGDOM = "GB";
    private static final String FRANCE = "FR";
    private static final String UNITED_STATES = "US";
    private static final String HONG_KONG = "HK";

    private NumberParser underTest;

    @BeforeEach
    void setup() {
        underTest = new NumberParser(buildCountryCodesMap(), buildNationalTrunkPrefixesMap());
    }

    @Test
    void shouldParseCallFromUkToUk() {
        // Given
        String dialledNumber = "07277822334";
        String userNumber = "+447866866886";

        // When
        String parsedNumber = underTest.parse(dialledNumber, userNumber);

        // Then
        assertThat(parsedNumber, is("+447277822334"));
    }

    @Test
    void shouldParseCallFromUsToUs() {
        // Given
        String dialledNumber = "1312233244";
        String userNumber = "+1212233200";

        // When
        String parsedNumber = underTest.parse(dialledNumber, userNumber);

        // Then
        assertThat(parsedNumber, is("+1312233244"));
    }

    @Test
    void shouldParseCallFromUkToUs() {
        // Given
        String dialledNumber = "+1312233244";
        String userNumber = "+447866866886";

        // When
        String parsedNumber = underTest.parse(dialledNumber, userNumber);

        // Then
        assertThat(parsedNumber, is("+1312233244"));
    }

    @Test
    void shouldParseCallFromHkToFr() {
        // Given
        String dialledNumber = "+33149527154";
        String userNumber = "+85225218121";

        // When
        String parsedNumber = underTest.parse(dialledNumber, userNumber);

        // Then
        assertThat(parsedNumber, is("+33149527154"));
    }

    @Test
    void shouldParseCallFromHkToHk() {
        // Given
        String dialledNumber = "5218122";
        String userNumber = "+85225218121";

        // When
        String parsedNumber = underTest.parse(dialledNumber, userNumber);

        // Then
        assertThat(parsedNumber, is("+8525218122"));
    }

    @Test
    void shouldErrorOnInvalidDialledCountryCodeForInternationalCalls() {
        // Given
        String dialledNumber = "+9876543210";
        String userNumber = "+5432109876";

        // When/Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> underTest.parse(dialledNumber, userNumber));
        assertThat(thrown.getMessage(), is("Unknown country code for number +9876543210"));
    }

    @Test
    void shouldErrorOnInvalidUserCountryCodeForLocalCalls() {
        // Given
        String dialledNumber = "9876543210";
        String userNumber = "+5432109876";

        // When/Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> underTest.parse(dialledNumber, userNumber));
        assertThat(thrown.getMessage(), is("Unknown country code for number +5432109876"));
    }

    @Test
    void shouldErrorOnMissingNationalTrunkPrefixForLocalCalls() {
        // Given
        Map<String, Integer> countryCodeMap = Map.of("GB", 44);
        Map<String, String> nationalTrunkPrefixMap = new HashMap<>();
        underTest = new NumberParser(countryCodeMap, nationalTrunkPrefixMap);

        String dialledNumber = "07277822334";
        String userNumber = "+447866866886";

        // When/Then
        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> underTest.parse(dialledNumber, userNumber));
        assertThat(thrown.getMessage(), is("No trunk prefix found for country GB"));
    }

    @Test
    void shouldErrorOnMissingDialledTrunkPrefixForLocalCalls() {
        // Given
        String dialledNumber = "7277822334";
        String userNumber = "+447866866886";

        // When/Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> underTest.parse(dialledNumber, userNumber));
        assertThat(thrown.getMessage(), is("Dialled number with invalid trunk prefix"));
    }

    private Map<String, Integer> buildCountryCodesMap() {
        Map<String, Integer> countryCodes = new HashMap<>();
        countryCodes.put(UNITED_KINGDOM, 44);
        countryCodes.put(FRANCE, 33);
        countryCodes.put(UNITED_STATES, 1);
        countryCodes.put(HONG_KONG, 852);
        return countryCodes;
    }

    private Map<String, String> buildNationalTrunkPrefixesMap() {
        Map<String, String> nationalTrunkPrefixes = new HashMap<>();
        nationalTrunkPrefixes.put(UNITED_KINGDOM, "0");
        nationalTrunkPrefixes.put(FRANCE, "0");
        nationalTrunkPrefixes.put(UNITED_STATES, "1");
        nationalTrunkPrefixes.put(HONG_KONG, "");
        return nationalTrunkPrefixes;
    }

}