package Model.OSM;

import java.io.Serializable;

/**
 * OSM representation of addresses
 */
public class OSMAddress extends OSMNode implements Serializable, Comparable {
    private String street, city, houseNumber;
    private String postcode;

    /**
     * Constructor for an OSMAddress
     * @param lon X coordinate
     * @param lat Y coordinate
     * @param street Street
     * @param city City
     * @param houseNumber HouseNumber
     * @param postcode PostCode
     */
    public OSMAddress(float lon, float lat,String street, String city, String houseNumber, String postcode){ //without lon and lat for now
        super(lon,lat);

        this.street=street.trim();
        this.city=city.trim();
        this.houseNumber=houseNumber;
        this.postcode=postcode;
    }

    /**
     * Returns the house number
     * @return HouseNumber
     */
    public String getHouseNumber() {return houseNumber;}

    /**
     * Returns the postcode
     * @return Postcode
     */
    public String getPostcode() {
        return postcode;
    }

    /**
     * Returns city
     * @return City
     */
    public String getCity() {
        return city;
    }

    /**
     * Returns street
     * @return Street
     */
    public String getStreet() {
        return street;
    }

    /**
     * String representation of OSMAddress
     * @return String representation of OSMAddress
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(getStreet()!=null) {
            sb.append(getStreet()).append(" ");
        }
        if(getHouseNumber()!=null) {
            sb.append(getHouseNumber()).append(" ");
        }
        if(getPostcode()!=null) {
            sb.append(getPostcode()).append(" ");
        }
        if(getCity()!=null) {
            sb.append(getCity()).append(" ");
        }
        return sb.toString();
    }

    /**
     * CompareTo method
     * @param o Other object
     * @return Int result
     */
    @Override
    public int compareTo(Object o) {
        OSMAddress address = (OSMAddress) o;
        return toString().compareTo(address.toString());
    }
}
