package simpledemo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.io.Serializable;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;

public class MAIN_ extends JFrame {
	/**
	 * Launch the application.
	 */
	int count = 0;
	int resultsize;
	static JMapViewer mapViewer = new JMapViewer();
	static Point aPoint2;
	static Point aPoint1;

	// TO CALCULATE THE DISTANCE BETWEEN TWO POINTS,LATER THE DISTANCE
	// WAS CALCULATED USING WALKING JOURNEY ROUTES (TAKING DISTANCE FROM API )AS IT
	// IS MORE ACCURATE

	private double distance(double lat1, double lon1, double lat2, double lon2) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		dist = dist * 1.609344;

		return (dist);
	}

	// Haversins Formula

	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	private double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}

	// system.println(distance(32.9697, -96.80322, 29.46786, -98.53506, "M") + "
	// Miles\n");
	// system.println(distance(32.9697, -96.80322, 29.46786, -98.53506, "K") + "
	// Kilometers\n");
	// system.println(distance(32.9697, -96.80322, 29.46786, -98.53506, "N") + "
	// Nautical Miles\n");


	private static final double DEFAULT_PRECISION = 1E5;
	private static JTextField textField;
	private static JTextField textField_1;
	private static JTextField textField_2;
	private static JTextField textField_3;
	private static JTextField textField_4;

	static Connection c = null;
	Statement stmt = null;
	private static JTextField textField_5;
	private static JTextField textField_6;
	// private JTextField textField_4;

	public List<Point> decode(String encoded) {
		return decode(encoded, DEFAULT_PRECISION);
	}

	/**
	 * Precision should be something like 1E5 or 1E6. For OSRM routes found
	 * precision was 1E6, not the original default 1E5.
	 *
	 * @param encoded
	 * @param precision
	 * @return
	 */

	// DECODING THE POLYLINE METHOD
	public static List<Point> decode(String encoded, double precision) {
		List<Point> track = new ArrayList<Point>();
		int index = 0;
		int lat = 0, lng = 0;

		while (index < encoded.length()) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			Point p = new Point((double) lat / precision, (double) lng / precision);
			track.add(p);
		}
		return track;
	}


	// THE FOLLOWING FUNCTION IMPLEMENTS THE OVERLAPPING ROUTES MATCHING ALGORITHM
	private void third(String start_address, String end_address) {

		List<ICoordinate> c = new ArrayList<>();
		List<ICoordinate> d = new ArrayList<>();
		String start_postcode = textField_1.getText();
		String end_postcode = textField_2.getText();

		c = get_coordinates(start_address, end_address);
		d = get_coordinates(start_postcode, end_postcode);
		// System.out.println("COORDINATES LIST 1" + c);
		// System.out.println("COORDINATES LIST 2" + d);

		partial(c, d);

		if ((overlapping_paths == true) && (time_common_meeting(start_postcode,
				end_postcode) == time_common_meeting(start_address, end_address))) {

			draw_route(start_address, end_address, "red");
			draw_route(start_postcode, end_postcode, "blue");

		}

	}

	// THE FOLLOWING FUNCTUON RETURNS THE SET OF CORRDINATES BETWEEN TWO POINTS
	private List<ICoordinate> get_coordinates(String start_address, String end_address) {
		List<ICoordinate> coordinates = new ArrayList<>();
		try {

			String url = String.format(
					"https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&mode=walking&key=AIzaSyAMMNuVPAxuZURzMPdEl_fQXB4wyse8XAA",
					start_address, end_address);

			String MapData = IOUtils.toString(new URL(url));
			JSONObject MapData_JSON = (JSONObject) JSONValue.parseWithException(MapData);

			JSONArray Routes = (JSONArray) MapData_JSON.get("routes");
			JSONObject Zero = (JSONObject) Routes.get(0);
			JSONObject Polyline = (JSONObject) Zero.get("overview_polyline");
			String ActualPolyline = (String) Polyline.get("points");
			// System.out.println("This is the polyline" + ActualPolyline);

			JSONArray Legs = (JSONArray) Zero.get("legs");

			for (int i = 0; i <= Legs.size() - 1; i++) {

				JSONObject CurrentLeg = (JSONObject) Legs.get(i);

				JSONArray steps = (JSONArray) CurrentLeg.get("steps");

				for (int j = 0; j <= steps.size() - 1; j++) {
					JSONObject substeps = (JSONObject) steps.get(j);

					if (j == 0) {

						System.out.println("DECODE" + decode(ActualPolyline, 1));

						for (int k = 0; k <= decode(ActualPolyline, 1).size() - 1; k++) {

							Point point = decode(ActualPolyline, 1).get(k);
							double _latt = point.getLat();
							double _long = point.getLng();
							// System.out.println("Yoooooooo __ : " + _latt / 100000.0);
							/// System.out.println("Loooooooo __ : " + _long / 100000.0);
							Coordinate c = new Coordinate(_latt / 100000.0, _long / 100000.0);
							coordinates.add(c);

						}

					} else {
						JSONObject substep_start = (JSONObject) substeps.get("start_location");
						JSONObject substep_end = (JSONObject) substeps.get("end_location");
						System.out.println(" End point is: " + substep_end);
						Double e1 = (Double) substep_start.get("lng");
						Double f = (Double) substep_end.get("lat");
						// System.out.println("this is e1 " + e1);
						// System.out.println("this is f" + f);

						// System.out.println("DECODE else" + decode(ActualPolyline, 1));

						for (int k = 0; k <= decode(ActualPolyline, 1).size() - 1; k++) {

							Point point = decode(ActualPolyline, 1).get(k);
							double _latt = point.getLat();
							double _long = point.getLng();
							// System.out.println("Yoooooooo __ : " + _latt / 100000.0);
							// System.out.println("Loooooooo __ : " + _long / 100000.0);
							Coordinate c = new Coordinate(_latt / 100000.0, _long / 100000.0);
							coordinates.add(c);

						}

					}

				}

			}
			// return coordinates;
		} catch (IOException e) {

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return coordinates;

		// TODO Auto-generated method stub

	}

	static boolean overlapping_paths;

	static Coordinate common_start;
	static Coordinate common_end;


	// THE FOLLOWING FUNCTION IMPLEMENTS THE PARTIAL ROUTE MATCHING

	public static List<ICoordinate> partial(List<ICoordinate> a, List<ICoordinate> b) {
		List<ICoordinate> fin = new ArrayList<>();

		for (int i = 0; i < a.size(); i++) {
			// System.out.println("A: " + a.get(i));
			for (int j = 0; j < b.size(); j++) {
				// System.out.println("B: " + b.get(j));
				if (a.get(i).equals(b.get(j))) {
					fin.add(a.get(i));
					common_start = (Coordinate) fin.get(0);
					common_end = (Coordinate) fin.get(fin.size() - 1);
					overlapping_paths = true;
				}
				// System.out.println(initial);
			}
		}
		// System.out.println("common_start" + common_start);
		// System.out.println("common_end" + common_end);

		return fin;

	}


	// THE FOLLOWING FUNCTION RETURNS THE TIME OF COMMON MEETING OF TWO USERS

	public static int time_common_meeting(String a, String b) {

		PreparedStatement stat;

		String t1 = textField_5.getText();
		String t2 = textField_6.getText();

		int t = Integer.parseInt(t1);
		int t_ = Integer.parseInt(t2);

		// System.out.println("String 1" + t);
		// System.out.println("String 2" + t_);
		try {

			stat = c.prepareStatement("SELECT * from main");

			ResultSet results;

			results = stat.executeQuery();

			// String JSON = IOUtils.toString(new URL(url2));
			while (results.next()) {
				String db_postcode_start = results.getString("start_point");
				String db_postcode_end = results.getString("end_point");

				a = db_postcode_start;
				b = common_start.toString();

				// b = "EH22DG";
				b = b.replaceAll(" ", "");
				b = b.replaceAll("\\[", "");
				b = b.replaceAll("\\]", "");
				b = b.replaceAll("Coordinate", "");
				// System.out.println("String 2" + b);
				String url = String.format(
						"https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&mode=walking&key=AIzaSyAMMNuVPAxuZURzMPdEl_fQXB4wyse8XAA",
						db_postcode_start, b);

				String MapData_;

				try {

					MapData_ = IOUtils.toString(new URL(url));

					// MapData_JSON_;
					JSONObject MapData_JSON_ = (JSONObject) JSONValue.parseWithException(MapData_);

					JSONArray Routes_ = (JSONArray) MapData_JSON_.get("routes");

					JSONObject Zero_ = (JSONObject) Routes_.get(0);
					// System.out.println(" 3 WBHE FUIKH KEFUH KJEFHU K");

					JSONArray Legs_ = (JSONArray) Zero_.get("legs");
					JSONObject CurrentLeg_ = (JSONObject) Legs_.get(0);
					JSONObject dis = (JSONObject) CurrentLeg_.get("duration");

					String textFromDuration = (String) dis.get("text");
					String[] any = textFromDuration.split(" ");

					double convertedTime = Double.parseDouble(any[0]);
					// System.out.println(convertedTime + " convertedTime in minutes");
					t_ = (int) (t_ + convertedTime);
					if (t_ >= 60) {
						t = t + 1;
						t = 0;// AND RESET TO ZERO
					}
					// System.out.println(t + "hours");// TIME THAT HE REACHED THE COMMON POINTS
					// System.out.println(t_ + "minutes");// TIME THAT HE REACHED THE COMMON POINT

				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return t;

	}

	// 

	// THE FOLLOWINF FUNCTION RETURNS TRUW IF THE TIME MATCHED FOR THE TWO USERS,
	// ELSE FALSE
	private static boolean time_match(String hours, String minutes) {

		hours = textField_5.getText();
		minutes = textField_6.getText();

		int hour = Integer.parseInt(hours);
		int min = Integer.parseInt(minutes);

		PreparedStatement stat;
		try {
			stat = c.prepareStatement("SELECT * from main");

			ResultSet results;

			results = stat.executeQuery();

			while (results.next()) {
				int h = results.getInt("hours");
				int m = results.getInt("minutes");

				if (h == hour && m == min) {

					return true;
				} else {
					return false;
				}

			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return false;

	}

	// 

	// THE FOLLOWING FUNCTION RETURNS THE CONVERTED DISTANCE IN KILOMETERS BETWEEN
	// TWO POINTS

	private boolean Converted_Distance(String s, String s2) {
		// https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&mode=walking&key=AIzaSyB8WeCbtRGQGKG7NdQ0jcbpmFY0OOgWPI8
		String url_ = String.format(
				"https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&mode=walking&key=AIzaSyAMMNuVPAxuZURzMPdEl_fQXB4wyse8XAA",
				s, s2);

		// String MapData_;
		try {
			String MapData_ = IOUtils.toString(new URL(url_));

			JSONObject MapData_JSON_ = (JSONObject) JSONValue.parseWithException(MapData_);
			JSONArray Routes_ = (JSONArray) MapData_JSON_.get("routes");
			JSONObject Zero_ = (JSONObject) Routes_.get(0);
			JSONArray Legs_ = (JSONArray) Zero_.get("legs");
			JSONObject CurrentLeg_ = (JSONObject) Legs_.get(0);
			JSONObject dis = (JSONObject) CurrentLeg_.get("distance");

			String textFromDuration = (String) dis.get("text");
			String[] any = textFromDuration.split(" ");
			// System.out.println(any[0] + " numerical value hopefully of distance");

			double convertedDistance = Double.parseDouble(any[0]);
			// System.out.println("Full distance converted" + convertedDistance);

			if (convertedDistance <= 5) {
				return true;
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

	// THE FOLLOWING FUNCTION DRAWS THE ROUTE BETWEEN TWO POINTS

	private static void draw_route(String start_address, String end_address, String Colour) {

		MapMarkerDot marker = new MapMarkerDot(52, -6);
		marker.setName("FIRST");

		marker.setColor(Color.RED);

		MapMarkerDot markerr = new MapMarkerDot(52, -6);
		marker.setColor(Color.YELLOW);

		MapMarkerDot marker3 = new MapMarkerDot(52, -6);
		marker3.setColor(Color.BLUE);

		try {

			List<ICoordinate> coordinates = new ArrayList<>();

			String url = String.format(
					"https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&mode=walking&key=AIzaSyAMMNuVPAxuZURzMPdEl_fQXB4wyse8XAA",
					start_address, end_address);

			String MapData = IOUtils.toString(new URL(url));
			JSONObject MapData_JSON = (JSONObject) JSONValue.parseWithException(MapData);

			JSONArray Routes = (JSONArray) MapData_JSON.get("routes");
			JSONObject Zero = (JSONObject) Routes.get(0);
			JSONObject Polyline = (JSONObject) Zero.get("overview_polyline");
			// System.out.println("This is polyline" + Polyline);
			String ActualPolyline = (String) Polyline.get("points");
			// System.out.println("This is the polyline" + ActualPolyline);

			JSONArray Legs = (JSONArray) Zero.get("legs");

			// System.out.println("legs size" + legs__.size());
			for (int i = 0; i <= Legs.size() - 1; i++) {

				JSONObject CurrentLeg = (JSONObject) Legs.get(i);
				JSONObject start_leg = (JSONObject) CurrentLeg.get("start_location");
				JSONObject end_leg = (JSONObject) CurrentLeg.get("end_location");

				Double start_lat = (Double) start_leg.get("lat");
				Double start_long = (Double) start_leg.get("lng");

				Double end_lat = (Double) end_leg.get("lat");
				Double end_long = (Double) end_leg.get("lng");

				marker3.setLat(start_lat);
				marker3.setLon(start_long);
				mapViewer.addMapMarker(marker3);
				System.out.println("red");

				JSONArray steps = (JSONArray) CurrentLeg.get("steps");

				for (int j = 0; j <= steps.size() - 1; j++) {
					JSONObject substeps = (JSONObject) steps.get(j);

					if (j == 0) {
						JSONObject substep_start = (JSONObject) substeps.get("start_location");
						JSONObject substep_end = (JSONObject) substeps.get("end_location");
						// System.out.println(" End point is: " + endP);
						Double substep_startLng = (Double) substep_start.get("lng");
						Double substep_startLat = (Double) substep_start.get("lat");

						Double substep_endLng = (Double) substep_end.get("lat");
						Double substep_endLat = (Double) substep_end.get("lng");

						// System.out.println("DECODE" + decode(ActualPolyline, 1));

						for (int k = 0; k <= decode(ActualPolyline, 1).size() - 1; k++) {

							Point point = decode(ActualPolyline, 1).get(k);
							double _latt = point.getLat();
							double _long = point.getLng();
							// System.out.println("Yoooooooo __ : " + _latt / 100000.0);
							// System.out.println("Loooooooo __ : " + _long / 100000.0);
							Coordinate c = new Coordinate(_latt / 100000.0, _long / 100000.0);
							coordinates.add(c);

							marker.setLat(substep_startLng);
							marker.setLon(substep_startLat);
							mapViewer.addMapMarker(marker);
							// System.out.println("red");

						}

					} else {
						JSONObject substep_start = (JSONObject) substeps.get("start_location");
						JSONObject substep_end = (JSONObject) substeps.get("end_location");
						// System.out.println(" End point is: " + substep_end);
						Double e1 = (Double) substep_start.get("lng");
						Double f = (Double) substep_end.get("lat");
						// System.out.println("this is e1 " + e1);
						// System.out.println("this is f" + f);

						// System.out.println("DECODE else" + decode(ActualPolyline, 1));

						for (int k = 0; k <= decode(ActualPolyline, 1).size() - 1; k++) {

							Point point = decode(ActualPolyline, 1).get(k);
							double _latt = point.getLat();
							double _long = point.getLng();
							// System.out.println("Yoooooooo __ : " + _latt / 100000.0);
							// System.out.println("Loooooooo __ : " + _long / 100000.0);
							Coordinate c = new Coordinate(_latt / 100000.0, _long / 100000.0);
							coordinates.add(c);

							markerr.setLat(f);
							markerr.setLon(e1);
							mapViewer.addMapMarker(markerr);
							// System.out.println("yellow");

						}
					}
				}
			}

			// System.out.println(coordinates);
			List<ICoordinate> final_ = new ArrayList<>();

			int l = coordinates.size();
			// System.out.println(l + "l");

			for (int s = 0; s < l; s++) {
				Coordinate z = (Coordinate) coordinates.get(s);
				final_.add(z);
				// System.out.println("ZZZZZ" + z);
			}
			for (int t = l - 1; t > -1; t--) {
				Coordinate y = (Coordinate) coordinates.get(t);
				final_.add(y);
				// System.out.println("YYYYYY" + y);
			}

			MapPolygonImpl x = new MapPolygonImpl(final_);
			mapViewer.addMapPolygon(x);
			mapViewer.setDisplayToFitMapPolygons();
			if (Colour == "red") {
				x.setColor(Color.RED);
			}
			if (Colour == "blue") {
				x.setColor(Color.BLUE);
			}
			if (Colour == "yellow") {
				x.setColor(Color.YELLOW);
			}
			if (Colour == "black") {
				x.setColor(Color.BLACK);

			}

		} catch (IOException e) {

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO Auto-generated method stub

	}

	// 

	// THE FOLLOWING FUNCTION DRAWS THE SHARED ROUTE WHICH THEY WILL BE TAKING IF
	// THEY SHARE THE JOURNEY.
	// IT DRIVES THE MID POINTS FOR THEM TO MEET AND SHARE THE ROUTE AND MID POINT
	// WHERE THE SHARED ROUTE ENDS.
	// IT ALSO DRAWS THE SHARED ROUTE

	private static void showSharedRoute(String a, String b, String c, String d) {

		JSONObject geocode__3;
		JSONObject geocode22;
		JSONObject geocode3;
		String user = textField.getText();
		String start_postcode = textField_1.getText();
		String end_postcode = textField_2.getText();
		String dateAndTime = textField_3.getText();
		String hours = textField_5.getText();
		String minutes = textField_6.getText();

		// int hour = Integer.parseInt(hours);
		// int min = Integer.parseInt(minutes);

		String var_distance = textField_4.getText();
		double var_dist = Double.parseDouble(var_distance);

		PreparedStatement stat = null;

		try {
			// Connection c1 = (Connection)
			// DriverManager.getConnection("jdbc:mysql://localhost:3306/db?useSSL=false",
			// "root", "Fatehjiom.!7");
			// stat = c1.prepareStatement("Select * from main");
			//
			// ResultSet results = stat.executeQuery();
			// // String JSON = IOUtils.toString(new URL(url2));
			//
			// while (results.next()) {
			// String db_postcode_start = results.getString("start_point");
			// String db_postcode_end = results.getString("end_point");

			String url1 = String.format(
					"https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&mode=walking&key=AIzaSyAMMNuVPAxuZURzMPdEl_fQXB4wyse8XAA",
					a, b);
			String url2 = String.format(
					"https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&mode=walking&key=AIzaSyAMMNuVPAxuZURzMPdEl_fQXB4wyse8XAA",
					c, d);

			String JSON22 = IOUtils.toString(new URL(url2));
			geocode22 = (JSONObject) JSONValue.parseWithException(JSON22);

			JSONArray routes22 = (JSONArray) geocode22.get("routes");

			System.out.println(routes22 + "ROUTES");

			JSONObject zero22 = (JSONObject) routes22.get(0);

			System.out.println("ZERO" + zero22);

			JSONObject polyline22 = (JSONObject) zero22.get("overview_polyline");
			// System.out.println("This is polyline" + polyline22);
			String a22 = (String) polyline22.get("points");
			// System.out.println("This is a" + a22);

			JSONArray legs22 = (JSONArray) zero22.get("legs");
			JSONObject NewZero22 = (JSONObject) legs22.get(0);

			JSONObject d22 = (JSONObject) NewZero22.get("distance");
			// FOR PARTIAL MATCH POINT 2
			JSONObject durationPoint2 = (JSONObject) NewZero22.get("duration");

			String textFromDuration = (String) durationPoint2.get("text");
			String[] any = textFromDuration.split(" ");
			// System.out.println(any[0] + " numerical value hopefully");

			double convertTime = Double.parseDouble(any[0]);
			// System.out.println("Full_Time converted" + convertTime);

			double half_Time = convertTime / 2.0;
			// System.out.println("Half_Time" + half_Time);
			JSONArray newStepsPoint2 = (JSONArray) NewZero22.get("steps");

			double timePoint2 = 0.0;

			for (int y = 0; y <= newStepsPoint2.size(); y++) {
				System.out.println(newStepsPoint2 + "newStepsPoint2 & its size " + newStepsPoint2.size());
				if (timePoint2 <= half_Time) {

					JSONObject _i_Point2 = (JSONObject) newStepsPoint2.get(y);
					// System.out.println("THIS" + _i_Point2 + "THIS IS POINT 1");

					JSONObject duration_again = (JSONObject) _i_Point2.get("duration");
					String textPoint2 = (String) duration_again.get("text");
					String[] splitAgain = textPoint2.split(" ");
					double hj = Double.parseDouble(splitAgain[0]);
					// System.out.println(splitAgain[0] + " the numerical value hopefully");

					timePoint2 = timePoint2 + hj;
					// System.out.println("IT IS TIME " + timePoint2);
					// JSONObject startFromStepsPoint2 = (JSONObject)
					// NewZero22.get("start_location");

					JSONObject endFromStepsPoint2 = (JSONObject) _i_Point2.get("end_location");

					Double fetching_Elng = (Double) endFromStepsPoint2.get("lng");
					Double fetching_Elat = (Double) endFromStepsPoint2.get("lat");
					// System.out.println("This is fetching_Elng" + fetching_Elng);
					// System.out.println("This is fetching_Elat" + fetching_Elat);

					aPoint2 = new Point(fetching_Elat, fetching_Elng);

					// System.out.println("This is aPoint 2" + aPoint2);
				} else {

					break;
				}

			}

			String t22 = (String) d22.get("text");
			String[] splitted22 = t22.split(" ");

			double ConvertedDistance22 = Double.parseDouble(splitted22[0]);

			// System.out.println(ConvertedDistance22 + " this is teh converted distance
			// hopefully");
			// System.out.println("This is d1" + d22);
			// System.out.println("This is the actual distance" + t22);
			String JSON3 = IOUtils.toString(new URL(url1));
			geocode3 = (JSONObject) JSONValue.parseWithException(JSON3);

			JSONArray routes3 = (JSONArray) geocode3.get("routes");

			System.out.println(routes3 + "ROUTES");

			JSONObject zero3 = (JSONObject) routes3.get(0);

			// System.out.println("ZERO" + zero3);

			JSONObject polyline3 = (JSONObject) zero3.get("overview_polyline");
			// System.out.println("This is polyline" + polyline3);
			String a3 = (String) polyline3.get("points");
			// System.out.println("This is a" + a3);

			JSONArray legs3 = (JSONArray) zero3.get("legs");
			JSONObject NewZero3 = (JSONObject) legs3.get(0);
			JSONObject d13 = (JSONObject) NewZero3.get("distance");

			String t13 = (String) d13.get("text");
			String[] splitted3 = t13.split(" ");

			// System.out.println(splitted3[0] + " the numerical value hopefully");

			double ConvertedDistance3 = Double.parseDouble(splitted3[0]);

			// System.out.println(ConvertedDistance3 + " this is teh converted distance
			// hopefully");

			for (int i3 = 0; i3 <= legs3.size() - 1; i3++) {

				JSONObject a23 = (JSONObject) legs3.get(i3);

				if ((ConvertedDistance3 <= 5) && (ConvertedDistance22 <= 5)) {

					String JSON1__3 = IOUtils.toString(new URL(url1));
					geocode__3 = (JSONObject) JSONValue.parseWithException(JSON1__3);

					JSONArray routes__3 = (JSONArray) geocode__3.get("routes");
					JSONObject zero__3 = (JSONObject) routes__3.get(0);

					JSONObject polyline__3 = (JSONObject) zero__3.get("overview_polyline");
					// System.out.println("This is polyline" + polyline__3);
					String a__3 = (String) polyline__3.get("points");
					// System.out.println("This is a" + a__3);

					JSONArray legs__3 = (JSONArray) zero__3.get("legs");
					JSONObject NewZero__3 = (JSONObject) legs__3.get(0);
					JSONObject durationLG = (JSONObject) NewZero__3.get("duration");
					String t__3 = (String) durationLG.get("text");
					String[] splitted__3 = t__3.split(" ");
					// System.out.println(splitted__3[0] + " the numerical value hopefully");

					double convertedTime = Double.parseDouble(splitted__3[0]);

					// System.out.println(convertedTime + " this is the converted time hopefully");
					double halfTime = convertedTime / 2.0;

					JSONArray newSteps = (JSONArray) NewZero__3.get("steps");

					double timeLG = 0.0;

					for (int y2 = 0; y2 < newSteps.size(); y2++) {
						if (timeLG < halfTime) {
							JSONObject _i_ = (JSONObject) newSteps.get(y2);

							JSONObject duration_ = (JSONObject) _i_.get("duration");
							String text = (String) duration_.get("text");
							String[] split = text.split(" ");
							double h = Double.parseDouble(split[0]);
							// System.out.println(split[0] + " the numerical value hopefully");
							timeLG = timeLG + h;
							// System.out.println("IT IS TIME " + timeLG);
							JSONObject startFromSteps = (JSONObject) _i_.get("start_location");
							// JSONObject endFromSteps = (JSONObject) _i_.get("end_location");

							Double fetching_Slng = (Double) startFromSteps.get("lng");
							Double fetching_Slat = (Double) startFromSteps.get("lat");
							// Double fetching_Elng = (Double) startFromSteps.get("lng");
							// Double fetching_Elat = (Double) startFromSteps.get("lat");

							aPoint1 = new Point(fetching_Slat, fetching_Slng);
							// System.out.println("This is aPoint 1" + aPoint1);

						} else {

							break;
						}

					}
					String one = aPoint1.toString();

					one = one.replaceAll(" ", "");
					one = one.replaceAll("\\(", "");
					one = one.replaceAll("\\)", "");

					String two = aPoint2.toString();
					two = two.replaceAll(" ", "");
					two = two.replaceAll("\\(", "");
					two = two.replaceAll("\\)", "");

					// System.out.println("--------" + one);
					// System.out.println("========" + two);

					hours = textField_5.getText();
					minutes = textField_6.getText();
					if (time_match(hours, minutes) == true) {

						// draw_route(start_postcode, end_postcode, "red");
						// draw_route(db_postcode_start, db_postcode_end, "blue");

						draw_route(one, two, "yellow");
					}
				}
			}

		}

		catch (IOException | ParseException e21) {

		}

	}

	// THIS IS WHERE THE WHOLE FILE IS CONNECTED TO USER INTERFACE

	MAIN_() {

		JPanel contentPane;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 564, 619);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		mapViewer.setBounds(151, 6, 441, 559);
		contentPane.add(mapViewer);
		// INPUT BUTTON IMPLEMENTATION

		JButton btnNewButton = new JButton("INPUT");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				PreparedStatement stat = null;

				try {
					Connection c1 = (Connection) DriverManager
							.getConnection("jdbc:mysql://localhost:3306/db?useSSL=false", "root", "Fatehjiom.!7");
					stat = c1.prepareStatement("Select * from main");

					ResultSet results = stat.executeQuery();
					// String JSON = IOUtils.toString(new URL(url2));

					while (results.next()) {
						String db_postcode_start = results.getString("start_point");
						String db_postcode_end = results.getString("end_point");
						String start_postcode = textField_1.getText();
						String end_postcode = textField_2.getText();
						showSharedRoute(start_postcode, db_postcode_start, end_postcode, db_postcode_end);

					}
				} catch (Exception e9) {
					e9.printStackTrace();
				}

				java.util.Date date = null;
				Date sqlStartDate = null;
				Date sqlStartTime = null;

				String user = textField.getText();
				String start_postcode = textField_1.getText();
				String end_postcode = textField_2.getText();
				String dateAndTime = textField_3.getText();
				String hours = textField_5.getText();
				String minutes = textField_6.getText();

				int hour = Integer.parseInt(hours);
				int min = Integer.parseInt(minutes);

				// String s = "03/24/2013 21:54";
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy ");
				try {

					date = simpleDateFormat.parse(dateAndTime);
					sqlStartDate = new java.sql.Date(date.getTime());
					// sqlStartTime = new java.sql.Time(date.getTime());

					// System.out.println("date : " + simpleDateFormat.format(date));

				} catch (java.text.ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				System.out.println(user);
				System.out.println(start_postcode);
				System.out.println(end_postcode);
				System.out.println(sqlStartDate);
				System.out.println(hour + "hours");
				System.out.println(min + "minutes");

				String a = "INSERT into main (user, start_point,end_point, date, hours,minutes) VALUES (?, ?, ?,?, ?, ?);";
				PreparedStatement ps;
				try {
					ps = c.prepareStatement(a);

					ps.setString(1, user);
					ps.setString(2, start_postcode);
					ps.setString(3, end_postcode);
					ps.setDate(4, sqlStartDate);
					ps.setInt(5, hour);
					ps.setInt(6, min);
					ps.execute();

				} catch (SQLException ez) {
					// TODO Auto-generated catch block
					ez.printStackTrace();

				}
			}
		});

		btnNewButton.setBounds(-3, 432, 150, 23);
		contentPane.add(btnNewButton);

		textField = new JTextField();
		textField.setBounds(17, 88, 130, 26);
		contentPane.add(textField);
		textField.setColumns(10);

		textField_1 = new JTextField();
		textField_1.setBounds(17, 145, 130, 26);
		contentPane.add(textField_1);
		textField_1.setColumns(10);

		textField_2 = new JTextField();
		textField_2.setBounds(17, 200, 130, 26);
		contentPane.add(textField_2);
		textField_2.setColumns(10);

		textField_3 = new JTextField();
		textField_3.setBounds(17, 252, 130, 26);
		contentPane.add(textField_3);
		textField_3.setColumns(10);

		textField_4 = new JTextField();
		textField_4.setBounds(17, 310, 130, 26);
		contentPane.add(textField_4);
		textField_4.setColumns(10);

		JLabel lblName = new JLabel("Name");
		lblName.setBounds(50, 60, 61, 16);
		contentPane.add(lblName);

		JLabel lblStartpoint = new JLabel("Start Point");
		lblStartpoint.setBounds(32, 126, 79, 16);
		contentPane.add(lblStartpoint);

		JLabel lblEndPoint = new JLabel("End Point");
		lblEndPoint.setBounds(32, 172, 79, 16);
		contentPane.add(lblEndPoint);

		JLabel lblDateandtime = new JLabel("Date_And_Time");
		lblDateandtime.setBounds(32, 226, 107, 16);
		contentPane.add(lblDateandtime);

		JLabel lblVariableDistance = new JLabel("Your var distance");
		lblVariableDistance.setBounds(25, 290, 114, 16);
		contentPane.add(lblVariableDistance);

		// *********************************************************************************************************
		// BUTTON IMPLEMENTATION

		JButton btnShowSharedRoute = new JButton("MatchedRoutes");
		btnShowSharedRoute.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String user = textField.getText();
				String start_postcode = textField_1.getText();
				String end_postcode = textField_2.getText();
				String dateAndTime = textField_3.getText();
				String hours = textField_5.getText();
				String minutes = textField_6.getText();

				int hour = Integer.parseInt(hours);
				int min = Integer.parseInt(minutes);

				// String var_distance = textField_4.getText();

				PreparedStatement stat = null;

				try {
					stat = c.prepareStatement("SELECT * from main");
				} catch (SQLException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}

				ResultSet results = null;

				try {
					results = stat.executeQuery();
				}

				catch (SQLException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}

				// String JSON = IOUtils.toString(new URL(url2));

				try {
					while (results.next()) {
						String db_postcode_start = results.getString("start_point");
						String db_postcode_end = results.getString("end_point");

						hours = textField_5.getText();
						minutes = textField_6.getText();
						if (time_match(hours, minutes) == true) {
							if (Converted_Distance(start_postcode, db_postcode_start) == true
									&& (Converted_Distance(end_postcode, db_postcode_end) == true)
									&& (start_postcode != db_postcode_start) && (end_postcode != db_postcode_end)) {
								draw_route(start_postcode, end_postcode, "red");
								draw_route(db_postcode_start, db_postcode_end, "blue");
							}

						}

					}

					String a = "INSERT into main (user, start_point,end_point, date,hours,minutes ) VALUES (?, ?, ?,?, ?, ?);";
					PreparedStatement ps;
					try {
						java.util.Date date = null;
						Date sqlStartDate = null;
						Date sqlStartTime = null;
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy ");

						date = simpleDateFormat.parse(dateAndTime);
						sqlStartDate = new java.sql.Date(date.getTime());
						// sqlStartTime = new java.sql.Time(date.getTime());

						System.out.println("date : " + simpleDateFormat.format(date));
						ps = c.prepareStatement(a);

						ps.setString(1, user);
						ps.setString(2, start_postcode);
						ps.setString(3, end_postcode);
						ps.setDate(4, sqlStartDate);
						ps.setInt(5, hour);
						ps.setInt(6, min);
						ps.execute();
						//
					} catch (SQLException ez) {
						// // TODO Auto-generated catch block
						ez.printStackTrace();
						//
					} catch (java.text.ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnShowSharedRoute.setBounds(6, 466, 141, 29);
		contentPane.add(btnShowSharedRoute);

		textField_5 = new JTextField();
		textField_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}

		});
		textField_5.setBounds(32, 376, 26, 26);
		contentPane.add(textField_5);
		textField_5.setColumns(10);

		textField_6 = new JTextField();
		textField_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String minutes = textField_6.getText();
				int minutesInt = Integer.parseInt(minutes);

			}
		});
		textField_6.setBounds(70, 376, 26, 26);
		contentPane.add(textField_6);
		textField_6.setColumns(10);

		JLabel label = new JLabel(":");
		label.setBounds(59, 381, 10, 16);
		contentPane.add(label);

		JButton btnNewButton_1 = new JButton("Third");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				try {

					PreparedStatement stat = c.prepareStatement("SELECT * from main");

					ResultSet results = stat.executeQuery();

					results = stat.executeQuery();
					while (results.next()) {
						String db_postcode_start = results.getString("start_point");
						String db_postcode_end = results.getString("end_point");
						third(db_postcode_start, db_postcode_end);

					}

				} catch (SQLException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}
			}

		});
		btnNewButton_1.setBounds(17, 507, 117, 29);
		contentPane.add(btnNewButton_1);

		// BUTTON 2 IMPLEMENTATION
		JButton btnNewButton_2 = new JButton("Toggle");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				mapViewer.removeAllMapMarkers();
				mapViewer.removeAllMapPolygons();

				String user = textField.getText();
				String start_postcode = textField_1.getText();
				String end_postcode = textField_2.getText();
				String dateAndTime = textField_3.getText();
				String hours = textField_5.getText();
				String minutes = textField_6.getText();

				PreparedStatement stat = null;

				try {

					stat = c.prepareStatement("SELECT * from main");

				} catch (SQLException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}

				ResultSet results = null;

				try {
					results = stat.executeQuery();
				}

				catch (SQLException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}

				try {
					int current_loop = 0;

					if (count == 0) {
						resultsize = 0;
					}

					while (results.next()) {

						String db_postcode_start = results.getString("start_point");
						String db_postcode_end = results.getString("end_point");
						String username = results.getString("user");

						// System.out.println(db_postcode_start + " " + current_loop);
						hours = textField_5.getText();
						minutes = textField_6.getText();

						// System.out.println(count + "COUNT");
						// System.out.println(current_loop + "CURRENT LOOP");
						if (time_match(hours, minutes) == true) {

							if (Converted_Distance(start_postcode, db_postcode_start) == true
									&& (Converted_Distance(end_postcode, db_postcode_end) == true)) {

								if (count == current_loop && (start_postcode != db_postcode_start)
										&& (end_postcode != db_postcode_end)) {

									PreparedStatement stat2 = c
											.prepareStatement("SELECT phoneNumber from login where name = ? ");
									stat2.setString(1, username);

									ResultSet resultsNew = stat2.executeQuery();

									String Number = null;
									while (resultsNew.next()) {
										Number = resultsNew.getString("phoneNumber");

									}

									draw_route(start_postcode, end_postcode, "red");
									draw_route(db_postcode_start, db_postcode_end, "blue");
									showSharedRoute(start_postcode, db_postcode_start, end_postcode, db_postcode_end);
									JOptionPane.showMessageDialog(btnNewButton_2, Number);

								}
								if (count == 0) {
									resultsize = resultsize + 1;
								}

							}
						}

						current_loop = current_loop + 1;

					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				if (count < resultsize - 1) {

					count = count + 1;

				} else {
					count = 0;

				}
			}
		});
		btnNewButton_2.setBounds(27, 548, 96, 29);
		contentPane.add(btnNewButton_2);

		JLabel lblTimehoursmins = new JLabel("Time[Hours:Mins]");
		lblTimehoursmins.setBounds(17, 348, 122, 16);
		contentPane.add(lblTimehoursmins);

		JLabel lblWalkAndSharing = new JLabel("WALK AND SHARING");
		lblWalkAndSharing.setBounds(6, 19, 170, 16);
		contentPane.add(lblWalkAndSharing);

		JLabel lblApp = new JLabel("APP");
		lblApp.setBounds(50, 35, 61, 16);
		contentPane.add(lblApp);
	}

	// THE MAIN METHOD
	public static void main(String[] args) {

		// time_common_meeting("EH74DG", "EH105DT");
		try {

			c = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/db?useSSL=false", "root",
					"Fatehjiom.!7");

		} catch (Exception e9) {
			e9.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MAIN_ frame = new MAIN_();
					frame.setVisible(true);
					// frame.mapViewer.setDisplayToFitMapMarkers();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}
}
