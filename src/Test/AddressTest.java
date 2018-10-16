package Test;

import Model.Address;
import org.junit.Test;

import static org.junit.Assert.*;

public class AddressTest {

    @Test
    public void testStreetCityInput() {
        String input = "Rued Langgaards Vej 7, 2300 København S";
        Address a = Address.parse(input);
        assertEquals("Rued Langgaards Vej", a.street());
        assertEquals("7", a.house());
        assertEquals("2300", a.zipcode());
        assertEquals("København S", a.city());
    }

    @Test
    public void testStreetCityMalformedInput() {
        String input = "Rued Langgaards Vej   7,, 2300 København S";
        Address a = Address.parse(input);
        assertEquals("Rued Langgaards Vej", a.street());
        assertEquals("7", a.house());
        assertEquals("2300", a.zipcode());
        assertEquals("København S", a.city());
    }

    @Test
    public void testStreetInput() {
        String input = "Valby Langgade 39";
        Address a = Address.parse(input);
        assertEquals("Valby Langgade", a.street());
        assertEquals("39", a.house());
    }

    @Test
    public void testCityInput() {
        String input = "2500 Valby";
        Address a = Address.parse(input);
        assertEquals("2500", a.zipcode());
        assertEquals("Valby", a.city());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidInput1() {
        String input = "39 Gade 39 Gade";
        Address.parse(input);

    }
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidInput2() {
        String input = "Test 12, 123 City";
        Address.parse(input);
    }

    @Test
    public void testAddressFloorToTheLeft() {
        String input = "Nærum Hovedgade 34, 2 tv 2850 Nærum";
        Address a = Address.parse(input);
        assertEquals("Nærum Hovedgade", a.street());
        assertEquals("34", a.house());
        assertEquals("2", a.floor());
        assertEquals("tv", a.side());
        assertEquals("2850", a.zipcode());
        assertEquals("Nærum", a.city());
    }

    @Test
    public void test() {
        String input = "Hans Olriks Vej 18.1.TV, 2450 København SV";
        Address address = Address.parse(input);
        assertEquals("Hans Olriks Vej",address.street());
        assertEquals("18.1.TV",address.house());
        assertEquals("2450",address.zipcode());
        assertEquals("København SV",address.city());
    }

    @Test
    public void testValidStreetCityInput2() {
        String input = "Chr. Bergs Vej 2710A, 3400 Hillerød"; //Special character in street-name + one letter in house-number
        Address a = Address.parse(input);
        assertEquals("Chr. Bergs Vej", a.street());
        assertEquals("2710A", a.house());
        assertEquals("3400", a.zipcode());
        assertEquals("Hillerød", a.city());
    }

    @Test
    public void testValidStreetCityInput3() {
        String input = "Frederiksberg Allé 7A, 2000 Frederiksberg"; //Special character in street-name
        Address a = Address.parse(input);
        assertEquals("Frederiksberg Allé", a.street());
        assertEquals("7A", a.house());
        assertEquals("2000", a.zipcode());
        assertEquals("Frederiksberg", a.city());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testInvalidStreetCityInput1() {
        String input = "Rued Langgaards Vej, 2300 København S"; //Missing house-number
        Address.parse(input);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testInvalidStreetCityInput2() {
        String input = "23 Rued Langgaards Vej, 2300 København S"; //Street and house in wrong order
        Address.parse(input);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testInvalidStreetCityInput3() {
        String input = "Rued Langgaards Vej 23, København S 2300"; //City and zipcode in wrong order
        Address.parse(input);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testInvalidStreetCityInput4() {
        String input = "Rued Langgaards Vej 23, København S"; //Missing zipcode
        Address.parse(input);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testInvalidStreetCityInput5() {
        String input = "Rued Langgaards Vej 23, 232123123 København S"; //Zipcode is too long
        Address.parse(input);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testInvalidStreetCityInput6() {
        String input = "Rued Langgaards Vej 23, 232 København S"; //zipcode is too short
        Address.parse(input);
    }

    @Test
    public void testAbnormalButValidStreetCityInput() {
        String input = "     Nordre Fasanvej        57A,,,,,,,,,,,,,,, 2000          Frederiksberg            ";
        Address a = Address.parse(input);
        assertEquals("Nordre Fasanvej", a.street());
        assertEquals("57A", a.house());
        assertEquals("2000", a.zipcode());
        assertEquals("Frederiksberg", a.city());
    }


    @Test
    public void testValidApartmentInput1() {
        String input = "Morsøvej 25, 1. MF., 2720 Vanløse"; //Dot after floor and side
        Address a = Address.parse(input);
        assertEquals("Morsøvej", a.street());
        assertEquals("25", a.house());
        assertEquals("1", a.floor());
        assertEquals("MF", a.side());
        assertEquals("2720", a.zipcode());
        assertEquals("Vanløse", a.city());
    }

    @Test
    public void testValidApartmentInput2() {
        String input = "Morsøvej 25, 1 MF, 2720 Vanløse"; //No dot after floor and side
        Address a = Address.parse(input);
        assertEquals("Morsøvej", a.street());
        assertEquals("25", a.house());
        assertEquals("1", a.floor());
        assertEquals("MF", a.side());
        assertEquals("2720", a.zipcode());
        assertEquals("Vanløse", a.city());
    }

    @Test
    public void testValidApartmentInput3() {
        String input = "Morsøvej 25, 1, 2720 Vanløse"; //No side for apartment given (whole apartment is one floor) + no dot after floor
        Address a = Address.parse(input);
        assertEquals("Morsøvej", a.street());
        assertEquals("25", a.house());
        assertEquals("1", a.floor());
        assertEquals("2720", a.zipcode());
        assertEquals("Vanløse", a.city());
    }

    @Test
    public void testValidApartmentInput4() {
        String input = "Morsøvej 25, 1., 2720 Vanløse"; //No side for apartment given (whole apartment is one floor) + dot after floor
        Address a = Address.parse(input);
        assertEquals("Morsøvej", a.street());
        assertEquals("25", a.house());
        assertEquals("1", a.floor());
        assertEquals("2720", a.zipcode());
        assertEquals("Vanløse", a.city());
    }

    @Test
    public void testValidApartmentInput5() {
        String input = "Morsøvej 25A, st. tv., 2720 Vanløse"; //Floor is not number but text (ground floor)
        Address a = Address.parse(input);
        assertEquals("Morsøvej", a.street());
        assertEquals("25A", a.house());
        assertEquals("st", a.floor());
        assertEquals("tv", a.side());
        assertEquals("2720", a.zipcode());
        assertEquals("Vanløse", a.city());
    }

    @Test
    public void testValidApartmentInput6() {
        String input = "Amu-Centervej 25A 25 TV. 2300 København S"; //Special characters in street-name + no separating commas between groups
        Address a = Address.parse(input);
        assertEquals("Amu-Centervej", a.street());
        assertEquals("25A", a.house());
        assertEquals("25", a.floor());
        assertEquals("TV", a.side());
        assertEquals("2300", a.zipcode());
        assertEquals("København S", a.city());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testInvalidApartmentInput() {
        String input = "Amu-Centervej 25A fifth left 2000 Frederiksberg"; //Invalid text for floor and side
        Address.parse(input);
    }

    @Test
    public void testCityMissingInput() {
        String input = "2500";
        Address a = Address.parse(input);
        assertEquals("2500", a.zipcode());
        assertEquals("Valby", a.city());
    }

    @Test
    public void testLargeInput() {
        String input = "Rued Langgaards Vej 7, 1 tv 2300 København S";
        Address a = Address.parse(input);
        assertEquals("Rued Langgaards Vej", a.street());
        assertEquals("1", a.floor());
        assertEquals("tv", a.side());
        assertEquals("7", a.house());
        assertEquals("2300", a.zipcode());
        assertEquals("København S", a.city());
    }

    @Test
    public void testWithoutSideInput() {
        String input = "Valby Langgade 39, 1. 2500";
        Address a = Address.parse(input);
        assertEquals("2500", a.zipcode());
        assertEquals("Valby", a.city());
        assertEquals("Valby Langgade", a.street());
        assertEquals("39", a.house());
        assertEquals("1.", a.floor());
    }

    @Test
    public void testWithoutFloorInput() {
        String input = "Valby Langgade 39, tv 2500";
        Address a = Address.parse(input);
        assertEquals("2500", a.zipcode());
        assertEquals("Valby", a.city());
        assertEquals("Valby Langgade", a.street());
        assertEquals("39", a.house());
        assertEquals("tv", a.side());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidInputNoString() {
        String input = "";
        Address.parse(input);
    }

    @Test
    public void testHouseNumberWithLetterInput() {
        String input = "Nørrebrogade 25G , 2500 Valby";
        Address a = Address.parse(input);
        assertEquals("Nørrebrogade", a.street());
        assertEquals("25G",a.house());
        assertEquals("2500", a.zipcode());
        assertEquals("Valby", a.city());
    }

    @Test
    public void testFloorSideInput() {
        String input = "Nørrebrogade 25 2. th., 2500 Valby";
        Address a = Address.parse(input);
        assertEquals("Nørrebrogade", a.street());
        assertEquals("25",a.house());
        assertEquals("2.", a.floor());
        assertEquals("th.", a.side());
        assertEquals("2500", a.zipcode());
        assertEquals("Valby", a.city());
    }

    @Test
    public void testDetailedAddress() {
        String input = "Lilletoften 24, 3. th, 2740 Skovlunde"; //test with comma's between (street house), (floor side), (zipcode city)
        Address a = Address.parse(input);
        assertEquals("Lilletoften", a.street());
        assertEquals("24", a.house());
        assertEquals("3.", a.floor());
        assertEquals("th", a.side());
        assertEquals("2740", a.zipcode());
        assertEquals("Skovlunde", a.city());
    }

    @Test
    public void testAnnoyingAddress1() {
        String input = "Haveforeningen af 1918 1, 8000 Aarhus C"; //Test with a streetname that ends in a number, followed by house
        Address a = Address.parse(input);
        assertEquals("haveforeningen af 1918", a.street());
        assertEquals("1", a.house());
        assertEquals("", a.floor());
        assertEquals("", a.side());
        assertEquals("8000", a.zipcode());
        assertEquals("Aarhus C", a.city());
    }

    @Test
    public void testAnnoyingAddress2() {
        String input = "Christian 4 vej 4, 6000 Kolding"; //Test with a streetname with number in the middel
        Address a = Address.parse(input);
        assertEquals("Christian 4 vej", a.street());
        assertEquals("4", a.house());
        assertEquals("", a.floor());
        assertEquals("", a.side());
        assertEquals("6000", a.zipcode());
        assertEquals("Kolding", a.city());
    }

    @Test
    public void testAnnoyingAddress3() {
        String input = "c-vej 3, 2300 København S"; //Test with a streetname with special character
        Address a = Address.parse(input);
        assertEquals("c-vej", a.street());
        assertEquals("3", a.house());
        assertEquals("", a.floor());
        assertEquals("", a.side());
        assertEquals("2300", a.zipcode());
        assertEquals("København S", a.city());
    }

    @Test
    public void testFloorsAndSides() {
        String input = "Bakkegade 28, 2. tv, 3400 Hillerød";
        Address addr = Address.parse(input);
        assertEquals("street of " + input, "Bakkegade", addr.street());
        assertEquals("house of " + input, "28", addr.house());
        assertEquals("floor of " + input, "2", addr.floor());
        assertEquals("side of " + input, "tv", addr.side());
        assertEquals("zipcode of " + input, "3400", addr.zipcode());
        assertEquals("city of " + input, "Hillerød", addr.city());
    }
    @Test
    public void testFloorsAndSidesWithLetters() {
        String input = "Bakkegade 28b, 2. tv, 3400 Hillerød";
        Address addr = Address.parse(input);
        assertEquals("street of " + input, "Bakkegade", addr.street());
        assertEquals("house of " + input, "28", addr.house());
        assertEquals("floor of " + input, "2", addr.floor());
        assertEquals("side of " + input, "tv", addr.side());
        assertEquals("zipcode of " + input, "3400", addr.zipcode());
        assertEquals("city of " + input, "Hillerød", addr.city());
    }
    @Test
    public void testFloorsAndSidesWithoutDot() {
        String input = "Bakkegade 28, 2 tv, 3400 Hillerød";
        Address addr = Address.parse(input);
        assertEquals("street of " + input, "Bakkegade", addr.street());
        assertEquals("house of " + input, "28", addr.house());
        assertEquals("floor of " + input, "2", addr.floor());
        assertEquals("side of " + input, "tv", addr.side());
        assertEquals("zipcode of " + input, "3400", addr.zipcode());
        assertEquals("city of " + input, "Hillerød", addr.city());
    }
    @Test
    public void testFloorsAndSidesWithoutCharacters() {
        String input = "Bakkegade 28 2 tv 3400 Hillerød";
        Address addr = Address.parse(input);
        assertEquals("street of " + input, "Bakkegade", addr.street());
        assertEquals("house of " + input, "28", addr.house());
        assertEquals("floor of " + input, "2", addr.floor());
        assertEquals("side of " + input, "tv", addr.side());
        assertEquals("zipcode of " + input, "3400", addr.zipcode());
        assertEquals("city of " + input, "Hillerød", addr.city());
    }
    @Test
    public void testzipcodeAndCityFirst() {
        String input = "3400 Hillerød, Bakkegade 28 2 tv";
        Address addr = Address.parse(input);
        assertEquals("street of " + input, "Bakkegade", addr.street());
        assertEquals("house of " + input, "28", addr.house());
        assertEquals("floor of " + input, "2", addr.floor());
        assertEquals("side of " + input, "tv", addr.side());
        assertEquals("zipcode of " + input, "3400", addr.zipcode());
        assertEquals("city of " + input, "Hillerød", addr.city());
    }

    @Test
    // Valid according to danmarksadresser.dk/adresser-regler-vejledning
    public void testAccentedStreet() {
        String input = "Alléparken 20, 2018 København S";
        Address addr = Address.parse(input);
        assertEquals("street of " + input, "Alléparken", addr.street());
        assertEquals("street number of " + input, "20", addr.house());
        assertEquals("zipcode of " + input, "2018", addr.zipcode());
        assertEquals("city of " + input, "København S", addr.city());
    }

    @Test
    public void whiteSpaceTest() {
        String input = "   Grønjordskollegiet 3 ,  2300 København  S   ";
        Address address = Address.parse(input);
        assertEquals("Grønjordskollegiet",address.street());
        assertEquals("3",address.house());
        assertEquals("2300",address.zipcode());
        assertEquals("København S",address.city());
    }

    @Test
    public void testStreetAndCity_NBJE()
    {
        String input = "Vangedevej 10 Dyssegård";
        Address addr = Address.parse(input);

        assertEquals("Vangedevej",addr.street());
        assertEquals("Dyssegård",addr.city());
    }

    @Test
    public void testStreetAndCityComma()
    {
        String input = "Vangedevej 10, Dyssegård";
        Address addr = Address.parse(input);
        assertEquals("Vangedevej",addr.street());
        assertEquals("Dyssegård",addr.city());
    }


    @Test
    public void testDanishChar() {
        String input = "Vråvej 1 4171 Glumsø";
        Address address = Address.parse(input);
        assertEquals("Vråvej", address.street());
        assertEquals("1",address.house());
        assertEquals("4171",address.zipcode());
        assertEquals("Glumsø",address.city());
    }

    @Test
    public void testTownWithOneSpace() {
        String input = "Storskovvej 16 8260 Viby J";
        Address address = Address.parse(input);
        assertEquals("Storskovvej",address.street());
        assertEquals("16",address.house());
        assertEquals("8260",address.zipcode());
        assertEquals("Viby J",address.city());
    }

    @Test
    public void testHouseWithSingleLetter() {
        String input = "Storskovvej 14C 8260 Viby J";
        Address address = Address.parse(input);
        assertEquals("Storskovvej",address.street());
        assertEquals("14C",address.house());
        assertEquals("8260",address.zipcode());
        assertEquals("Viby J",address.city());
    }

    @Test
    public void testStreetWithUmlaut() {
        String input = "Blüchers Alle 4 6800 Varde";
        Address address = Address.parse(input);
        assertEquals("Blüchers Alle",address.street());
        assertEquals("4",address.house());
        assertEquals("6800",address.zipcode());
        assertEquals("Varde",address.city());
    }

    @Test
    public void testStreetWithDot() {
        String input = "B. Nielsens Vej 1 7990 Øster Assels";
        Address address = Address.parse(input);
        assertEquals("B. Nielsens Vej",address.street());
        assertEquals("1",address.house());
        assertEquals("7990",address.zipcode());
        assertEquals("Øster Assels",address.city());
    }

    @Test
    public void testStreetWithDash() {
        String input = "Bobøl-Foldingbrovej 12 6650 Brørup";
        Address address = Address.parse(input);
        assertEquals("Bobøl-Foldingbrovej",address.street());
        assertEquals("12",address.house());
        assertEquals("6650",address.zipcode());
        assertEquals("Brørup",address.city());
    }

    @Test
    public void testzipcode() {
        String input = "Skagensgade 1, 0000 Skagen";
        Address addr = Address.parse(input);
        assertEquals("street of " + input, "Skagensgade", addr.street());
        assertEquals("zipcode of " + input, "0000", addr.zipcode());
        assertEquals("city of " + input, "Skagen", addr.city());
    }

    @Test
    public void testFloorAndSide() {
        String input = "Odensevej 5 3. th, 2100 Koebenhavn";
        Address addr = Address.parse(input);
        assertEquals("street of " + input, "Odensevej", addr.street());
        assertEquals("floor of " + input, "3.", addr.floor());
        assertEquals("side of " + input, "th", addr.side());
        assertEquals("zipcode of " + input, "2100", addr.zipcode());
        assertEquals("city of " + input, "Koebenhavn", addr.city());
    }

    @Test
    public void testStreetWithHyphen() {
        String input = "Chr-Skagensgade 1, 0000 Skagen";
        Address addr = Address.parse(input);
        assertEquals("street of " + input, "Chr-Skagensgade", addr.street());
        assertEquals("zipcode of " + input, "0000", addr.zipcode());
        assertEquals("city of " + input, "Skagen", addr.city());
    }

    @Test
    public void testStreetWithStartingNumber() {
        String input = "10. Majsgade 1, 0000 Skagen";
        Address addr = Address.parse(input);
        assertEquals("street of " + input, "10. Majsgade", addr.street());
        assertEquals("zipcode of " + input, "0000", addr.zipcode());
        assertEquals("city of " + input, "Skagen", addr.city());
    }


    @Test
    public void testStrangezipcode() {
        String input = "Emil Holms Kanal 20, 0999 København C";
        Address addr = Address.parse(input);
        assertEquals("street of " + input, "Emil Holms Kanal", addr.street());
        assertEquals("street number of " + input, "20", addr.house());
        assertEquals("zipcode of " + input, "0999", addr.zipcode());
        assertEquals("city of " + input, "København C", addr.city());
    }

    @Test
    public void testStreetWithPeriod() {
        String input = "Chr. X Vej 7, 3720 Aakirkeby";
        Address addr = Address.parse(input);
        assertEquals("street of " + input, "Chr. X Vej", addr.street());
        assertEquals("street number of " + input, "7", addr.house());
        assertEquals("zipcode of " + input, "3720", addr.zipcode());
        assertEquals("city of " + input, "Aakirkeby", addr.city());
    }

    @Test
    public void testStreetWithDigit() {
        String input = "3. Tværvej 1, 2400 København NV";
        Address addr = Address.parse(input);
        assertEquals("street of " + input, "3. Tværvej", addr.street());
        assertEquals("street number of " + input, "1", addr.house());
        assertEquals("zipcode of " + input, "2400", addr.zipcode());
        assertEquals("city of " + input, "København NV", addr.city());
    }

    @Test
    public void testFloorAndSide2() {
        String input = "Holsteinsgade 28 st. mf. tv., 2100 København Ø";
        Address a = Address.parse(input);
        assertEquals("street of " + input, "Holsteinsgade", a.street());
        assertEquals("street number of " + input,"28", a.house());
        assertEquals("floor of " + input,"st.", a.floor());
        assertEquals("side of " + input, "mf. tv.", a.side());
        assertEquals("zipcode of " + input, "2100", a.zipcode());
        assertEquals("city of " + input, "København Ø", a.city());
    }

    @Test
    public void testHouseWithLetterInput() {
        String input = "Bistrupvej 75a 3460 Birkerød";
        Address a = Address.parse(input);
        assertEquals("Bistrupvej", a.street());
        assertEquals("75a", a.house());
        assertEquals("3460", a.zipcode());
        assertEquals("Birkerød", a.city());
    }

    @Test
    public void testNoKommasOrDotsInput(){
        String input = "Rued langgårds vejá 7b 1 45454545454545454545454545 2920 Charlottenlund";
        Address a = Address.parse(input);
        assertEquals("Rued langgårds vejá", a.street());
        assertEquals("7b", a.house());
        assertEquals("1", a.floor());
        assertEquals("45454545454545454545454545", a.side());
        assertEquals("2920", a.zipcode());
        assertEquals("Charlottenlund", a.city());
    }

    @Test
    public void testStreetWithNumbersInput() {
        String input = "10. Februar Vej 3";
        Address a = Address.parse(input);
        assertEquals("10. Februar Vej", a.street());
        assertEquals("3", a.house());
    }

    @Test
    public void testStoriesInput(){
        String input = "Ejgårdsvej 24, 1. TV 2920 Charlottenlund";
        Address a = Address.parse(input);
        assertEquals("Ejgårdsvej", a.street());
        assertEquals("24", a.house());
        assertEquals("1", a.floor());
        assertEquals("TV", a.side());
        assertEquals("2920", a.zipcode());
        assertEquals("Charlottenlund", a.city());
    }

    @Test
    public void testStoriesWithNumbersInput(){
        String input = "Grønjordskollegiet 3, 2 3323 2300 København S";
        Address a = Address.parse(input);
        assertEquals("Grønjordskollegiet", a.street());
        assertEquals("3", a.house());
        assertEquals("2", a.floor());
        assertEquals("3323", a.side());
        assertEquals("2300", a.zipcode());
        assertEquals("København S", a.city());
    }
}