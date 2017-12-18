package simpledemo;

import java.awt.BorderLayout;
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
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import au.com.bytecode.opencsv.CSVReader;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.*;

import java.awt.Image;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;

public class Ok extends JFrame {

	private static final double DEFAULT_PRECISION = 1E5;

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

	private JFrame game;
	private Image image;
	JMapViewer map;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	CSVReader reader;

	String user;
	String start_postcode;
	String end_postcode;
	String dateAndTime;
	static Connection c = null;
	Statement stmt = null;

	/**
	 * Launch the application.
	 */

	public Ok(JFrame game) {
		this.game = game;
		image = (new ImageIcon("Image001.jpg")).getImage();

	}

	/**
	 * Create the frame.
	 */

	public Ok() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 800);
		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel centrePanel = new JPanel();
		getContentPane().add(centrePanel, BorderLayout.CENTER);
		centrePanel.setLayout(new BorderLayout(0, 0));
		map = new JMapViewer();
		centrePanel.add(map, BorderLayout.CENTER);

		JPanel controlPanel = new JPanel();
		controlPanel.setPreferredSize(new Dimension(200, 10));
		centrePanel.add(controlPanel, BorderLayout.WEST);
		controlPanel.setLayout(null);

		JPanel contentPane = new JPanel();

		JMapViewer mapViewer = new JMapViewer();
		mapViewer.setBounds(97, 11, 441, 559);
		contentPane.add(mapViewer);

		JButton btnNewButton = new JButton("Click me");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				// map.getMapMarkerList().clear();
				//
				// MapMarkerDot marker = new MapMarkerDot(52, -6);
				// marker.setColor(Color.RED);
				// // map.addMapMarker(marker);
				//
				// user = textField.getText();
				// start_postcode = textField_1.getText();
				// end_postcode = textField_2.getText();
				// dateAndTime = textField_3.getText();

				// reader = null;
				JSONObject geocode;

				String start_postcode = "22+buccleuch+street";
				String end_postcode = "10+colinton+road";

				String url4 = String.format(
						"https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&mode=walking&key=AIzaSyAMMNuVPAxuZURzMPdEl_fQXB4wyse8XAA",
						start_postcode, end_postcode);

				try {

					// PreparedStatement stat = c.prepareStatement("SELECT * from main");
					// ResultSet results = stat.executeQuery();
					// while (results.next()) {
					// String db_postcode_start = results.getString("start_point");
					// System.out.println(db_postcode_start + " yyyyyy ");

					// if ((start_postcode.substring(0, 2).equals(db_postcode_start.substring(0,
					// 2)))) {

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
						JSONArray steps = (JSONArray) a2.get("steps");
						System.out.println("LEG " + i + "\n");
						// List<ICoordinate> coordinates = new ArrayList<>();
						// MapPolygonImpl m = new MapPolygonImpl(coordinates);

						System.out.println("steps********** " + i + steps);

						for (int j = 0; j <= steps.size() - 1; j++) {
							JSONObject substeps = (JSONObject) steps.get(j);
							JMapViewer mapViewer = new JMapViewer();

							if (j == 0) {
								JSONObject startP = (JSONObject) substeps.get("start_location");

								JSONObject endP = (JSONObject) substeps.get("end_location");
								// System.out.println(" End point is: " + endP);
								Double e1 = (Double) startP.get("lng");
								Double eee = (Double) startP.get("lat");

								Double f = (Double) endP.get("lat");

								Double ffff = (Double) endP.get("lng");

								System.out.println(
										" start and End point is: " + startP + endP + "Correct" + e1 + " Correct " + f);
								System.out.println("this is e1= " + e1);
								System.out.println("this is f =" + f);
								// double g = Math.sqrt(((e1 - f) * (e1 - f)) + ((eee - ffff) * (eee - ffff)));
								// System.out.println("The shortest distance" + g);

								int zoom = 0;

								System.out.println("DECODE" + decode(a, 1));

								for (int k = 0; k <= decode(a, 1).size() - 1; k++) {

									Point point = decode(a, 1).get(k);
									double _latt = point.getLat();
									double _long = point.getLng();
									System.out.println("Yoooooooo IF: " + _latt / 100000.0);
									System.out.println("Loooooooo IF: " + _long / 100000.0);
									// Coordinate c = new Coordinate(_latt / 100000.0, _long / 100000.0);
								}

							} else {

								System.out.println("ELSEEEEEEEEEEEE!!!!!");

								JSONObject startP = (JSONObject) substeps.get("start_location");
								JSONObject endP = (JSONObject) substeps.get("end_location");
								System.out.println(" End point is: " + endP);
								Double e1 = (Double) startP.get("lng");
								Double eee = (Double) startP.get("lat");

								Double f = (Double) endP.get("lat");

								Double ffff = (Double) endP.get("lng");
								System.out.println("this is e1 " + e1);
								System.out.println("this is f" + f);

								// double g = Math.sqrt(((e1 - f) * (e1 - f)) + ((eee - ffff) * (eee - ffff)));
								// System.out.println("The shortest distance" + g);
								System.out.println("DECODE" + decode(a, 1));

								for (int k = 0; k <= decode(a, 1).size() - 1; k++) {

									Point point = decode(a, 1).get(k);
									double _latt = point.getLat();
									double _long = point.getLng();
									System.out.println("Yoooooooo ELSE : " + _latt / 100000.0);
									System.out.println("Loooooooo  ELSE: " + _long / 100000.0);
									// Coordinate c = new Coordinate(_latt / 100000.0, _long / 100000.0);
									List<ICoordinate> coordinates = new ArrayList<>();
									Coordinate c = new Coordinate(-3.21218, 55.9333);
									coordinates.add(c);
									c = new Coordinate(-3.2099, 55.93574);
									coordinates.add(c);
									c = new Coordinate(-3.2102, 55.93412);
									coordinates.add(c);
									MapPolygonImpl m = new MapPolygonImpl(coordinates);
									mapViewer.addMapPolygon(m);

								}
							}
						}
						// mapViewer.addMapPolygon(m);
					}

					// }}

				} catch (IOException e2) {

				} catch (org.json.simple.parser.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*
				 * java.util.Date date = null; Date sqlStartDate = null; Date sqlStartTime =
				 * null;
				 * 
				 * SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm");
				 * try { date = simpleDateFormat.parse(dateAndTime); sqlStartDate = new
				 * java.sql.Date(date.getTime()); // sqlStartTime = new
				 * java.sql.Time(date.getTime());
				 * 
				 * System.out.println("date : " + simpleDateFormat.format(date)); } catch
				 * (ParseException ex) { System.out.println("Exception " + ex); }
				 * 
				 * System.out.println(user); System.out.println(start_postcode);
				 * System.out.println(end_postcode); System.out.println(sqlStartDate);
				 * 
				 * String a =
				 * "INSERT into main (user, start_point,end_point, date, time) VALUES (?, ?, ?, ?, ?);"
				 * ; PreparedStatement ps; try { ps = c.prepareStatement(a);
				 * 
				 * ps.setString(1, user); ps.setString(2, start_postcode); ps.setString(3,
				 * end_postcode); ps.setDate(4, sqlStartDate); ps.setDate(5, sqlStartTime);
				 * ps.execute();
				 * 
				 * } catch (SQLException e) { // TODO Auto-generated catch block
				 * e.printStackTrace(); } }
				 * 
				 * private double deg2rad(double d) { // TODO Auto-generated method stub return
				 * 0; }
				 */}
		});

		btnNewButton.setBounds(58, 370, 89, 23);
		controlPanel.add(btnNewButton);

		textField = new JTextField();
		textField.setBounds(32, 102, 130, 26);
		controlPanel.add(textField);
		textField.setColumns(10);

		textField_1 = new JTextField();
		textField_1.setBounds(32, 159, 130, 26);
		controlPanel.add(textField_1);
		textField_1.setColumns(10);

		textField_2 = new JTextField();
		textField_2.setBounds(32, 215, 130, 26);
		controlPanel.add(textField_2);
		textField_2.setColumns(10);

		textField_3 = new JTextField();
		textField_3.setBounds(32, 279, 130, 26);
		controlPanel.add(textField_3);
		textField_3.setColumns(10);

		JLabel lblNewLabel = new JLabel("Your Name");
		lblNewLabel.setBounds(32, 62, 115, 16);
		controlPanel.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Your starting point");
		lblNewLabel_1.setBounds(32, 131, 140, 16);
		controlPanel.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("Your Ending point");
		lblNewLabel_2.setBounds(32, 197, 130, 16);
		controlPanel.add(lblNewLabel_2);

		JLabel lblNewLabel_3 = new JLabel("What time?");
		lblNewLabel_3.setBounds(32, 253, 115, 16);
		controlPanel.add(lblNewLabel_3);

		JLabel lblNewLabel_4 = new JLabel(" ");
		lblNewLabel_4.setBounds(16, 405, 162, 128);
		Image img = new ImageIcon(this.getClass().getResource("/Ok-icon.png")).getImage();
		lblNewLabel_4.setIcon(new ImageIcon(img));
		controlPanel.add(lblNewLabel_4);

	}

	public static void main(String[] args) {

		// try {
		/*
		 * c = (Connection)
		 * DriverManager.getConnection("jdbc:mysql://localhost:3306/db?useSSL=false",
		 * "root", "Fatehjiom.!7");
		 * 
		 * List<ICoordinate> coordinates = new ArrayList<>(); Coordinate cT = new
		 * Coordinate(56.1, -2.1); coordinates.add(cT); Coordinate cC = new
		 * Coordinate(56.7, -2.1); coordinates.add(cC); Coordinate cCC = new
		 * Coordinate(56.8, -2.2); coordinates.add(cCC); JMapViewer mapviewer = new
		 * JMapViewer(); MapPolygonImpl m = new MapPolygonImpl(coordinates);
		 * mapviewer.addMapPolygon(m);
		 * 
		 * } catch (Exception e9) { e9.printStackTrace(); }
		 */
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Ok frame = new Ok();
					frame.setVisible(true);
					frame.map.setDisplayToFitMapMarkers();
				} catch (Exception e) {
					e.printStackTrace();

				}
			}
		});

	}
}
