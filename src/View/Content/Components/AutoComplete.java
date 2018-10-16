package View.Content.Components;

import Model.Model;
import Model.OSM.OSMAddress;
import View.Content.MapContext.MapView;
import View.Content.MapContext.SwingView;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import Model.PathType;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Set;

/**
 * Shows the addresses which matches the search.
 * Makes a point at the selected address.
 */
public class AutoComplete extends ComboBox {
    private final int SHOWN_ADDRESS_LIMIT = 50;
    private double x,y;
    private ArrayList<OSMAddress> shownAddresses;
    private static ArrayList<OSMAddress> addressList;
    private OSMAddress selectedAddress;
    private PathType markingType;

    /**
     * Gets the OSM addresses and finds the selected address on the map.
     * @param markingType Uses the markingType to mark on the map.
     */
    public AutoComplete(PathType markingType) {
        this.markingType = markingType;

        setEditable(true);
        shownAddresses = new ArrayList<>();
        addressList = Model.getInstance().getOSMAddresses();
        setPadding(new Insets(0,2,0,0));

        getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals("") && !oldValue.equals(newValue)) {
                autoSuggest(newValue);
            }
        });

        getSelectionModel().selectedItemProperty().addListener(( observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                if(newValue != null && !newValue.equals("") && newValue instanceof OSMAddress) {
                    selectedAddress = (OSMAddress) newValue;
                    String selectedString = selectedAddress.toString().trim();
                    if(selectedString.length()>0) setPromptText(selectedAddress.toString());
                    y = selectedAddress.getLat();
                    x = selectedAddress.getLon();
                    Shape point = new Ellipse2D.Double(x, y, 0,0);
                    SwingView.getMarkingMap().put(markingType, selectedAddress);
                    MapView.getSwingView().setAbsPosition(x, y);

                    Set<Shape> selectedAddresses = Model.getInstance().getSelectedAddresses();
                    if (selectedAddresses.size() == 0) {
                        selectedAddresses.add(point);
                    } else {
                        selectedAddresses.clear();
                        selectedAddresses.add(point);
                    }
                    selectedAddresses.add(point);
                    repaintMap();
                }
            });
        });
    }

    /**
     * Show the selected addresses.
     * @return The showSelectedAddress is shown.
     */
    public static boolean isShowSelectedAddress() {
        boolean showSelectedAddress = true;
        return showSelectedAddress;
    }

    /**
     * Looks through shownAddresses and adds them when query length is less than or equal to 3 and matches the searched.
     * @param query The input the user writes in the address field on the UI.
     */
    private void autoSuggest(String query) {
        Platform.runLater(()->{
            shownAddresses.clear();
            getItems().clear();

            if (!query.equals("") && query.length() >= 3) {
                String bQuery = query.toLowerCase().trim();
                for (OSMAddress addr : addressList) {
                    String city = addr.getCity().toLowerCase().trim();
                    String street = addr.getStreet().toLowerCase().trim();
                    String house = null;
                    if(addr.getHouseNumber()!=null) {
                        house= addr.getHouseNumber().toLowerCase().trim();
                    }
                    String postcode = null;
                    if(addr.getPostcode()!=null) postcode = addr.getPostcode().toLowerCase().trim();

                    if (
                            city.startsWith(bQuery) ||
                            street.startsWith(bQuery) ||
                            (house!=null && house.startsWith(bQuery)) ||
                            (postcode!=null && postcode.startsWith(bQuery)) ||
                            bQuery.contains(street + " " + house)
                        ) shownAddresses.add(addr);

                    if(shownAddresses.size() >= SHOWN_ADDRESS_LIMIT)break;
                }
            }
            if(shownAddresses!=null){
                for(OSMAddress a : shownAddresses){
                    getItems().add(a);
                }
            }

            hide();
            show();
            setVisibleRowCount(5);
        });
    }

    /**
     * Set address list.
     * @param addressList Uses addressList to set addressList.
     */
    public static void setAddressList(ArrayList<OSMAddress> addressList) {
        AutoComplete.addressList = addressList;
    }

    /**
     * Repaints the map.
     */
    private void repaintMap() {
        MapView.getSwingView().repaint();
    }

    /**
     * Get the selected address.
     * @return Gives the selested address.
     */
    public OSMAddress getSelectedAddress() {
        return selectedAddress;
    }

    /**
     * Set the selected address.
     * @param selectedAddress Uses OSMAddress to make into String, which can then be set.
     */
    public void setSelectedAddress(OSMAddress selectedAddress) {
        this.selectedAddress = selectedAddress;
        getEditor().setText(selectedAddress.toString());
    }

}