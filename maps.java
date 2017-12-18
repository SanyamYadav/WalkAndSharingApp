package simpledemo;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.apache.commons.io.IOUtils;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;

public class maps extends JFrame {
	static JSONObject geocode;

	// static JSONArray geo;
	public static void main(String[] args) {

		String start_postcode = "AB101AA";
		String end_postcode = "AB101AF";
		// ICoordinate c = new Coordinate(1, 1);
		// // List<? extends ICoordinate> points
		//
		// List<? extends ICoordinate> points = new ArrayList<ICoordinate>();
		// ArrayList<ICoordinate> obj = new ArrayList<ICoordinate>();
		// obj.add(c);

		JPanel contentPane = null;

		JMapViewer mapViewer = new JMapViewer();
		mapViewer.setBounds(97, 11, 441, 559);
		// contentPane.add(mapViewer);

		List<ICoordinate> coordinates = new ArrayList<>();
		Coordinate c = new Coordinate(56.1, -2);
		coordinates.add(c);
		c = new Coordinate(56.7, -2.1);
		coordinates.add(c);
		c = new Coordinate(56.8, -2.2);
		coordinates.add(c);
		MapPolygonImpl m = new MapPolygonImpl(coordinates);
		mapViewer.addMapPolygon(m);

		String url = "https://maps.googleapis.com/maps/api/directions/json?origin=Disneyland&destination=Universal+Studios+Hollywood4&key=AIzaSyAMMNuVPAxuZURzMPdEl_fQXB4wyse8XAA";
		String url2 = "https://maps.googleapis.com/maps/api/geocode/json?address=EH7+4dg&key=AIzaSyAMMNuVPAxuZURzMPdEl_fQXB4wyse8XAA"
				+ "";
		// String
		// url3="https://maps.googleapis.com/maps/api/staticmap?center=Brooklyn+Bridge,New+York,NY&zoom=13&size=600x300&maptype=roadmap\n"
		// +
		// "&markers=color:blue%7Clabel:S%7C40.702147,-74.015794&markers=color:green%7Clabel:G%7C40.711614,-74.012318\n"
		// +
		// "&markers=color:red%7Clabel:C%7C40.718217,-73.998284\n" +
		// "&key=AIzaSyAMMNuVPAxuZURzMPdEl_fQXB4wyse8XAA";
		String url4 = String.format(
				"https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&key=AIzaSyAMMNuVPAxuZURzMPdEl_fQXB4wyse8XAA",
				start_postcode, end_postcode);
		try {
			String JSON = IOUtils.toString(new URL(url2));
			// geocode = (JSONObject) JSONValue.parseWithException(JSON);

			// JSONObject a= (JSONObject) geocode.get("results");
			// JSONObject b = (JSONObject) a.get(0);
			// JSONObject c= (JSONObject) b.get("geometry");
			// JSONObject d= (JSONObject) c.get("location");
			// Double e= (Double) d.get("lng");
			// Double f= (Double) d.get("lat");

			String JSON1 = IOUtils.toString(new URL(url4));
			geocode = (JSONObject) JSONValue.parseWithException(JSON1);

			JSONArray routes = (JSONArray) geocode.get("routes");
			JSONObject zero = (JSONObject) routes.get(0);
			JSONArray legs = (JSONArray) zero.get("legs");

			// System.out.println(legs.size());
			//
			for (int i = 0; i <= legs.size() - 1; i++) {
				JSONObject a2 = (JSONObject) legs.get(i);
				// System.out.println(i);
				// System.out.println(a2 + "\n");
				JSONArray steps = (JSONArray) a2.get("steps");
				// System.out.println(steps.size() + "\n");
				System.out.println("LEG " + i + "\n");
				for (int j = 0; j <= steps.size() - 1; j++) {
					JSONObject substeps = (JSONObject) steps.get(j);
					// System.out.println(substeps);
					if (j == 0) {
						JSONObject startP = (JSONObject) substeps.get("start_location");

						JSONObject endP = (JSONObject) substeps.get("end_location");
						System.out.println("Start pont is: " + startP);
						System.out.println(" End point is: " + endP);
						Double e = (Double) startP.get("lng");
						Double f = (Double) endP.get("lat");
						System.out.println(" start and End point is: " + endP + startP);

					} else {
						JSONObject endP = (JSONObject) substeps.get("end_location");
						System.out.println(" End point is: " + endP);
						Double f = (Double) endP.get("lat");
						System.out.println(" start and End point is: " + endP);

					}

				}

			}
			//

		} catch (IOException | ParseException e) {

		}

	}
}
