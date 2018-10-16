package Test;

import Model.Model;
import View.Content.MapContext.SwingView;
import javafx.embed.swing.JFXPanel;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.geom.Rectangle2D;

import static org.junit.Assert.*;

public class ModelTest {
    static SwingView sw;
    static Rectangle2D viewRect;

    @BeforeClass
    public static void setup() {
        JFXPanel fxPanel = new JFXPanel();
        Model.getInstance().fileLoad("data/cph.zip");
        Model m = Model.getInstance();
        double minLon = m.getMinLon();
        double maxLon = m.getMaxLon();
        double minLat = m.getMinLat();
        double maxLat = m.getMaxLat();
        viewRect = new Rectangle2D.Double(minLon,minLat,maxLon,maxLat);
        sw = new SwingView();
    }

    @Test
    public void testNearestAddress() {
        assertEquals("Bernstorffsgade 300 2610 Rødovre ",Model.getInstance().getNearestAddress(7.087757110595703,-55.67327117919922).toString());
    }

    /*@Test
    public void testNearestName() {
        sw.setViewRect(viewRect);
        assertEquals("IT-Universitet i København ", Model.getInstance().getNearestName(OSMWayType.BUILDING, new Point2D.Double(7.100558280944824,-55.66080093383789)));
    }*/
}