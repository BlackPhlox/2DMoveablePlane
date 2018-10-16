package Model;

import java.util.regex.*;

/**
 * This class handles addresses and can parse
 * strings into a format that the program can recognize.
 */
public class Address {
    private final String street, house, floor, side, zipcode, city;

    /**
     * Creates an address object.
     * @param _street Street of address.
     * @param _house House of address.
     * @param _floor Floor of address.
     * @param _side Side of address.
     * @param _zipcode Zip code of address.
     * @param _city City of address.
     */
    private Address(String _street, String _house, String _floor, String _side, String _zipcode, String _city) {
        street = _street;
        house = _house;
        floor = _floor;
        side = _side;
        zipcode = _zipcode;
        city = _city;
    }

    /**
     * String representation of an address.
     * @return String representation of address.
     */
    public String toString() {
        return (street != null ? street + " " : "") +
                (house != null ? house + " " : "") +
                (floor != null ? floor + " " : "") +
                (side != null ? side + " " : "") +
                (zipcode != null ? zipcode + " " : "") +
                (city != null ? city + " " : "");
    }

    /**
     * Class for handling the building of an address.
     */
    static class Builder {
        String street = "Unknown", house, floor, side, zipcode, city;
        Builder street(String _street) { street = _street; return this; }
        Builder house(String _house)   { house = _house;   return this; }
        Builder floor(String _floor)   { floor = _floor;   return this; }
        Builder side(String _side)     { side = _side;     return this; }
        Builder zipcode(String _zipcode) { zipcode = _zipcode; return this; }
        Builder city(String _city)     { city = _city;     return this; }
        Address build() {
            return new Address(street, house, floor, side, zipcode, city);
        }
    }

    /**
     * Get street.
     * @return Street.
     */
    public String street()   { return street; }

    /**
     * Get house.
     * @return House.
     */
    public String house()    { return house; }

    /**
     * Get floor.
     * @return Floor.
     */
    public String floor()    { return floor; }

    /**
     * Get side.
     * @return Side.
     */
    public String side()     { return side; }

    /**
     * Get Zip code.
     * @return Zip code.
     */
    public String zipcode() { return zipcode; }

    /**
     * Get city.
     * @return City.
     */
    public String city()     { return city; }

    /**
     * parses addresses for use in model.
     * @param s Address.
     * @return Address.
     */
    public static Address parse(String s) {
        final String regex = "\\s*(?<streetHouseFloorSide>(?<street>[a-zA-ZåæøÅÆØäÄüÜéÉ\\s\\d\\.\\-]+[a-zA-ZæøåÆØÅäÄüÜéÉ\\.\\-])\\s+(?<house>\\d+\\.*\\d*[a-zA-Z]?)[\\s\\.\\,]*(?<floor>[\\d]+\\.\\s+?[a-zA-Z\\.]{2,6})?\\s*(?<side>[a-zA-Z]{2})?)?\\s*[,]*\\s*(?<zipcodeCity>(?<zipcode>\\d{4})\\s+(?<city>[a-zA-ZåæøÅÆØ]+[\\s\\.]*?[a-zA-ZåæøÅÆØ]*))?\\s*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        Builder b = new Builder();
        if (matcher.matches() && (matcher.group("streetHouseFloorSide")!=null || matcher.group("zipcodeCity")!=null)) {
            return b.city(matcher.group("city")).
                    house(matcher.group("house")).
                    floor(matcher.group("floor")).
                    side(matcher.group("side")).
                    zipcode(matcher.group("zipcode")).
                    street(matcher.group("street")).build();
        }
        throw new IllegalArgumentException("Invalid address \"" + s + "\"");
    }
}