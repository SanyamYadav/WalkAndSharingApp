package simpledemo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.io.Serializable;
import java.util.Iterator;
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

public class Test extends JFrame {
	/**
	 * Launch the application.
	 */
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
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;

	static Connection c = null;
	Statement stmt = null;
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

	Test() {


		JPanel contentPane;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 564, 619);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JMapViewer mapViewer = new JMapViewer();
		mapViewer.setBounds(151, 6, 441, 559);
		contentPane.add(mapViewer);

		JButton btnNewButton = new JButton("Show Point");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// map.getMapMarkerList().clear();
				MapMarkerDot marker = new MapMarkerDot(52, -6);
				marker.setColor(Color.RED);

				MapMarkerDot markerr = new MapMarkerDot(52, -6);
				markerr.setColor(Color.YELLOW);

				MapMarkerDot markerrr = new MapMarkerDot(52, -6);
				markerrr.setColor(Color.GREEN);

				MapMarkerDot markerrrr = new MapMarkerDot(52, -6);
				markerrrr.setColor(Color.BLACK);

				JSONObject geocode;
				JSONObject geocode__;
				String user = textField.getText();
				String start_postcode = textField_1.getText();
				String end_postcode = textField_2.getText();
				String dateAndTime = textField_3.getText();

				String var_distance = textField_4.getText();
				double var_dist = Double.parseDouble(var_distance);
				String url2 = "https://maps.googleapis.com/maps/api/geocode/json?address=EH7+4dg&key=AIzaSyAMMNuVPAxuZURzMPdEl_fQXB4wyse8XAA"
						+ "";

				String url4 = String.format(
						"https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&mode=walking&key=AIzaSyAMMNuVPAxuZURzMPdEl_fQXB4wyse8XAA",
						start_postcode, end_postcode);

				try {
					PreparedStatement stat = c.prepareStatement("SELECT * from main");
					ResultSet results = stat.executeQuery();
					// String JSON = IOUtils.toString(new URL(url2));

					while (results.next()) {
						String db_postcode_start = results.getString("start_point");
						String db_postcode_end = results.getString("end_point");
						System.out.println(db_postcode_start + "    yyyyyy ");

						System.out.println(db_postcode_end + "    zzzzzz ");

						String JSON1 = IOUtils.toString(new URL(url4));
						geocode = (JSONObject) JSONValue.parseWithException(JSON1);

						JSONArray routes = (JSONArray) geocode.get("routes");
						JSONObject zero = (JSONObject) routes.get(0);
						JSONObject polyline = (JSONObject) zero.get("overview_polyline");
						System.out.println("This is polyline" + polyline);
						String a = (String) polyline.get("points");
						System.out.println("This is a" + a);

						JSONArray legs = (JSONArray) zero.get("legs");

						System.out.println("legs size" + legs.size());
						for (int i = 0; i <= legs.size() - 1; i++) {

							JSONObject a2 = (JSONObject) legs.get(i);
							JSONObject start_ = (JSONObject) a2.get("start_location");

							System.out.println("db_postcode_start" + db_postcode_start);

							System.out.println("start_postcode" + start_postcode);

							// if ((db_postcode_start.matches(start_postcode))
							// && (db_postcode_end.matches(end_postcode))) {

							Double start_lat = (Double) start_.get("lat");
							Double start_long = (Double) start_.get("lng");

							JSONObject end_ = (JSONObject) a2.get("end_location");

							Double end_lat = (Double) end_.get("lat");
							Double end_long = (Double) end_.get("lng");

							System.out.println("start_postcode" + start_postcode);

							System.out.println("db_postcode_start" + db_postcode_start);

							System.out.println("end_postcode" + end_postcode);

							System.out.println("db_postcode_end" + db_postcode_end);

							List<ICoordinate> coordinates__ = new ArrayList<>(); //

							if ((start_postcode.substring(0, 2).equals(db_postcode_start.substring(0, 2))
									&& (var_dist < 100)
									&& (end_postcode.substring(0, 2).equals(db_postcode_end.substring(0, 2))))) {
								//

								String url5 = String.format(
										"https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&mode=walking&key=AIzaSyAMMNuVPAxuZURzMPdEl_fQXB4wyse8XAA",
										db_postcode_start, db_postcode_end);
								// **********************************************************************//
								String JSON1__ = IOUtils.toString(new URL(url5));
								geocode__ = (JSONObject) JSONValue.parseWithException(JSON1__);

								JSONArray routes__ = (JSONArray) geocode__.get("routes");
								JSONObject zero__ = (JSONObject) routes__.get(0);
								JSONObject polyline__ = (JSONObject) zero__.get("overview_polyline");
								System.out.println("This is polyline" + polyline__);
								String a__ = (String) polyline__.get("points");
								System.out.println("This is a" + a__);

								JSONArray legs__ = (JSONArray) zero__.get("legs");

								System.out.println("legs size" + legs__.size());
								for (int o = 0; o <= legs__.size() - 1; o++) {

									JSONObject a2__ = (JSONObject) legs__.get(o);
									JSONObject start__ = (JSONObject) a2__.get("start_location");
									JSONObject end__ = (JSONObject) a2__.get("end_location");

									System.out.println("db_postcode_start" + db_postcode_start);
									System.out.println("start_postcode" + start_postcode);

									// if ((db_postcode_start.matches(start_postcode))
									// && (db_postcode_end.matches(end_postcode))) {

									Double start_lat__ = (Double) start__.get("lat");
									Double start_long__ = (Double) start__.get("lng");

									Double end_lat__ = (Double) end__.get("lat");
									Double end_long__ = (Double) end__.get("lng");


									
									
									System.out.println("The shortest distance   1" + start_lat);

									System.out.println("The shortest distance   2" + start_lat__);

									System.out.println("The shortest distance   3" + start_long);

									System.out.println("The shortest distance   4" + start_long__);

									System.out.println("The shortest distance   5" + end_lat);

									System.out.println("The shortest distance   6" + end_lat__);

									System.out.println("The shortest distance   7" + end_long);

									System.out.println("The shortest distance   8" + end_long__);

									JSONArray steps__ = (JSONArray) a2__.get("steps");
									// System.out.println("LEG " + i + "\n");

									for (int j = 0; j <= steps__.size() - 1; j++) {
										JSONObject substeps__ = (JSONObject) steps__.get(j);

										if (j == 0) {
											JSONObject startP__ = (JSONObject) substeps__.get("start_location");
											JSONObject endP__ = (JSONObject) substeps__.get("end_location");
											// System.out.println(" End point is: " + endP);
											Double e1 = (Double) startP__.get("lng");
											Double eee = (Double) startP__.get("lat");

											Double f = (Double) endP__.get("lat");
											Double fff = (Double) endP__.get("lng");
											System.out.println(" start and End point is: " + startP__ + endP__
													+ "Correct" + e1 + " Correct " + f);
											System.out.println("this is e1= " + e1 + " this is eee" + eee);
											System.out.println("this is f =" + f + "this is fff" + fff);
											System.out.println("DECODE" + decode(a__, 1));

											for (int k = 0; k <= decode(a__, 1).size() - 1; k++) {

												Point point = decode(a__, 1).get(k);
												double _latt = point.getLat();
												double _long = point.getLng();
												System.out.println("Yoooooooo __ : " + _latt / 100000.0);
												System.out.println("Loooooooo __ : " + _long / 100000.0);
												Coordinate c = new Coordinate(_latt / 100000.0, _long / 100000.0);
												coordinates__.add(c);

											}

										} else {
											JSONObject startP__ = (JSONObject) substeps__.get("start_location");
											JSONObject endP__ = (JSONObject) substeps__.get("end_location");
											System.out.println(" End point is: " + endP__);
											Double e1 = (Double) startP__.get("lng");
											Double f = (Double) endP__.get("lat");
											System.out.println("this is e1 " + e1);
											System.out.println("this is f" + f);

											System.out.println("DECODE else" + decode(a__, 1));

											for (int k = 0; k <= decode(a__, 1).size() - 1; k++) {

												Point point = decode(a__, 1).get(k);
												double _latt = point.getLat();
												double _long = point.getLng();
												System.out.println("Yoooooooo __ : " + _latt / 100000.0);
												System.out.println("Loooooooo __ : " + _long / 100000.0);
												Coordinate c = new Coordinate(_latt / 100000.0, _long / 100000.0);
												coordinates__.add(c);

											}
										}
									}

									markerr.setLat(start_lat__);
									markerr.setLon(start_long__);
									mapViewer.addMapMarker(markerr);
									System.out.println("YELLOW");

									markerrr.setLat(end_lat__);
									markerrr.setLon(end_long__);
									mapViewer.addMapMarker(markerrr);
									System.out.println("GREEN");

									double gg = (distance(start_lat, start_long, start_lat__, start_long__));

									System.out.println("start_lat ***********" + " " + " " + start_lat);

									System.out.println("start_long ***********" + start_long);

									System.out.println("start_lat__ ********" + start_lat__);

									System.out.println("start_long __*********" + start_long__);

									System.out.println("THE CORRECT DISTANCE IS in metres " + gg);
								}

								// ********************************************************//

								System.out.println("OUTPUT FROM JSON" + start_lat + start_long + end_lat + end_long);

								JSONArray steps = (JSONArray) a2.get("steps");
								System.out.println("LEG " + i + "\n");

								List<ICoordinate> coordinates = new ArrayList<>(); //

								for (int j = 0; j <= steps.size() - 1; j++) {
									JSONObject substeps = (JSONObject) steps.get(j);
									// JMapViewer mapViewer = new JMapViewer();
	mapViewer.setDisplayToFitMapPolygons();
									if (j == 0) {
										JSONObject startP = (JSONObject) substeps.get("start_location");
										JSONObject endP = (JSONObject) substeps.get("end_location");
										// System.out.println(" End point is: " + endP);
										Double e1 = (Double) startP.get("lng");
										Double eee = (Double) startP.get("lat");

										Double f = (Double) endP.get("lat");
										Double fff = (Double) endP.get("lng");
										System.out.println(" start and End point is: " + startP + endP + "Correct" + e1
												+ " Correct " + f);
										System.out.println("this is e1= " + e1 + " this is eee" + eee);
										System.out.println("this is f =" + f + "this is fff" + fff);
										System.out.println("DECODE" + decode(a, 1));

										for (int k = 0; k <= decode(a, 1).size() - 1; k++) {

											Point point = decode(a, 1).get(k);
											double _latt = point.getLat();
											double _long = point.getLng();
											System.out.println("Yoooooooo : " + _latt / 100000.0);
											System.out.println("Loooooooo : " + _long / 100000.0);
											Coordinate c = new Coordinate(_latt / 100000.0, _long / 100000.0);
											coordinates.add(c);

											marker.setLat(start_lat);
											marker.setLon(start_long);
											mapViewer.addMapMarker(marker);
											System.out.println("RED");

											markerrrr.setLat(end_lat);
											markerrrr.setLon(end_long);
											mapViewer.addMapMarker(markerrrr);
											System.out.println("BLACK");

										}

									} else {
										JSONObject startP = (JSONObject) substeps.get("start_location");
										JSONObject endP = (JSONObject) substeps.get("end_location");
										System.out.println(" End point is: " + endP);
										Double e1 = (Double) startP.get("lng");
										Double f = (Double) endP.get("lat");
										System.out.println("this is e1 " + e1);
										System.out.println("this is f" + f);

										System.out.println("DECODE else" + decode(a, 1));

										for (int k = 0; k <= decode(a, 1).size() - 1; k++) {

											Point point = decode(a, 1).get(k);
											double _latt = point.getLat();
											double _long = point.getLng();
											System.out.println("Yoooooooo : " + _latt / 100000.0);
											System.out.println("Loooooooo : " + _long / 100000.0);
											Coordinate c = new Coordinate(_latt / 100000.0, _long / 100000.0);
											coordinates.add(c);

										}

									}
								}
								System.out.println("111111" + coordinates);
								System.out.println("222222" + coordinates__);

								MapPolygonImpl m = new MapPolygonImpl(coordinates); //
								mapViewer.addMapPolygon(m);
								mapViewer.setDisplayToFitMapPolygons();
								MapPolygonImpl m__ = new MapPolygonImpl(coordinates__); //
								mapViewer.addMapPolygon(m__);
	mapViewer.setDisplayToFitMapPolygons();
							}
							// }
						}
					}
				} catch (IOException | ParseException e2) {

				} catch (SQLException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

				java.util.Date date = null;
				Date sqlStartDate = null;
				Date sqlStartTime = null;

				// String s = "03/24/2013 21:54";
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm");
				try {
					date = simpleDateFormat.parse(dateAndTime);
					sqlStartDate = new java.sql.Date(date.getTime());
					// sqlStartTime = new java.sql.Time(date.getTime());

					System.out.println("date : " + simpleDateFormat.format(date));

				} catch (java.text.ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				System.out.println(user);
				System.out.println(start_postcode);
				System.out.println(end_postcode);
				System.out.println(sqlStartDate);

				String a = "INSERT into main (user, start_point,end_point, date, time) VALUES (?, ?, ?, ?, ?);";
				PreparedStatement ps;
				try {
					ps = c.prepareStatement(a);

					ps.setString(1, user);
					ps.setString(2, start_postcode);
					ps.setString(3, end_postcode);
					ps.setDate(4, sqlStartDate);
					ps.setDate(5, sqlStartTime);
					ps.execute();

				} catch (SQLException ez) {
					// TODO Auto-generated catch block
					ez.printStackTrace();

				}
			}
		});
		btnNewButton.setBounds(32, 362, 99, 23);
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
		textField_4.setBounds(17, 300, 130, 26);
		contentPane.add(textField_4);
		textField_4.setColumns(10);

		JLabel lblName = new JLabel("Name");
		lblName.setBounds(33, 40, 61, 16);
		contentPane.add(lblName);

		JLabel lblStartpoint = new JLabel("Start_Point");
		lblStartpoint.setBounds(32, 115, 79, 16);
		contentPane.add(lblStartpoint);

		JLabel lblEndPoint = new JLabel("End Point ");
		lblEndPoint.setBounds(32, 172, 79, 16);
		contentPane.add(lblEndPoint);

		JLabel lblDateandtime = new JLabel("Date_And_Time");
		lblDateandtime.setBounds(32, 226, 107, 16);
		contentPane.add(lblDateandtime);

		JLabel lblVariableDistance = new JLabel("Variable DIstance");
		lblVariableDistance.setBounds(17, 277, 114, 16);
		contentPane.add(lblVariableDistance);
	}

	public static void main(String[] args) {

		try {

			c = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/db?useSSL=false", "root",
					"Fatehjiom.!7");
		} catch (Exception e9) {
			e9.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Test frame = new Test();
					frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
