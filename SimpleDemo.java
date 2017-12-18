package simpledemo;

import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.ArrayList;
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
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.*;
import java.awt.*;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;

public class SimpleDemo extends JFrame {
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

	public SimpleDemo(JFrame game) {
		this.game = game;
		image = (new ImageIcon("Image001.jpg")).getImage();

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
					SimpleDemo frame = new SimpleDemo();
					frame.setVisible(true);
					frame.map.setDisplayToFitMapMarkers();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SimpleDemo() {
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

				map.getMapMarkerList().clear();
				MapMarkerDot marker = new MapMarkerDot(52, -6);
				marker.setColor(Color.RED);
				map.addMapMarker(marker);

				user = textField.getText();
				start_postcode = textField_1.getText();
				end_postcode = textField_2.getText();
				dateAndTime = textField_3.getText();

				reader = null;
				JSONObject geocode;

				// static JSONArray geo;

				String url4 = String.format(

						// "https://graphhopper.com/api/1/route?point=51.131%2C12.414&point=48.224%2C3.867&vehicle=car&locale=de&key=716138f5-af87-4f49-91d8-d63100bc9eb5"
						"https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&mode=walking&key=AIzaSyAMMNuVPAxuZURzMPdEl_fQXB4wyse8XAA",
						start_postcode, end_postcode);
				/////////////
				try {
					reader = new CSVReader(
							new FileReader("/Users/SanyamYadav/Desktop/osmpostcodes/postcodeData/postcodes.csv"), ',',
							'"', 1);

					// Read CSV line by line and use the string array as you want
					String[] nextLine;

					PreparedStatement stat = c.prepareStatement("SELECT * from main");
					ResultSet results = stat.executeQuery();

					while (results.next()) {
						String db_postcode_start = results.getString("start_point");
						System.out.println(db_postcode_start + "    yyyyyy ");
						while ((nextLine = reader.readNext()) != null) {
							// if (results != null) {
							if (db_postcode_start.matches(nextLine[0])) {
								Double p = Double.parseDouble(nextLine[1]);
								// System.out.println(x);
								Double t = Double.parseDouble(nextLine[2]);
								System.out.println("Start Match Latitude" + p);

								System.out.println("End Match Longitude " + t);
								System.out.println("start_postcode " + start_postcode);
								System.out.println("db_postcode_start " + db_postcode_start);

								if ((start_postcode.substring(0, 2).equals(db_postcode_start.substring(0, 2)))
										&& ((nextLine = reader.readNext()) != null)
										&& (start_postcode.matches(nextLine[0]))) {
									// || (start_postcode.matches(nextLine[0]))

									Double p2 = Double.parseDouble(nextLine[1]);
									// System.out.println(x);
									Double t2 = Double.parseDouble(nextLine[2]);
									System.out.println(" Second Start Match Latitude" + p2);
									System.out.println("Second End Match Longitude " + t2);

									double g = Math.sqrt(((p - t) * (p - t)) + ((p - t) * (p - t)));
									System.out.println("The shortest distance" + g);

									System.out.println("The shortest distance" + p + t);
									marker.setLat(p);
									marker.setLon(t);
									ArrayList<PostCode> postCodes = PostCode.getPostcodes();
									for (PostCode pc1 : postCodes) {
										int zoom = 0;
										pc1.setLon(t2);
										pc1.setLat(p2);
										marker.setLat(p);
										marker.setLon(t);
										// map.addMapMarker(p, t);

										map.addMapMarker(new MapMarkerDot(pc1));
										map.addMapMarker(marker);
									}

									String JSON1 = IOUtils.toString(new URL(url4));
									geocode = (JSONObject) JSONValue.parseWithException(JSON1);

									JSONArray routes = (JSONArray) geocode.get("routes");
									JSONObject zero = (JSONObject) routes.get(0);
									JSONArray legs = (JSONArray) zero.get("legs");

									List<ICoordinate> coordinates = new ArrayList<>();
									System.out.println("legs size" + legs.size());
									for (int i = 0; i <= legs.size() - 1; i++) {
										JSONObject a2 = (JSONObject) legs.get(i);
										JSONArray steps = (JSONArray) a2.get("steps");
										System.out.println("LEG " + i + "\n");
										for (int j = 0; j <= steps.size() - 1; j++) {
											JSONObject substeps = (JSONObject) steps.get(j);
											JMapViewer mapViewer = new JMapViewer();

											if (j == 0) {
												JSONObject startP = (JSONObject) substeps.get("start_location");

												JSONObject endP = (JSONObject) substeps.get("end_location");
												// System.out.println(" End point is: " + endP);
												Double e1 = (Double) startP.get("lng");
												Double f = (Double) endP.get("lat");
												System.out.println(" start and End point is: " + startP + endP
														+ "Correct" + e1 + " Correct " + f);
												System.out.println("this is e1= " + e1);
												System.out.println("this is f =" + f);

											} else {
												JSONObject startP = (JSONObject) substeps.get("start_location");
												JSONObject endP = (JSONObject) substeps.get("end_location");
												System.out.println(" End point is: " + endP);
												Double e1 = (Double) startP.get("lng");
												Double f = (Double) endP.get("lat");
												System.out.println("this is e1 " + e1);
												System.out.println("this is f" + f);
												Coordinate c = new Coordinate(f, e1);
												coordinates.add(c);

											}
										}

									}
									MapPolygonImpl m = new MapPolygonImpl(coordinates);
									mapViewer.addMapPolygon(m);

								}

							}

						}

					}
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (org.json.simple.parser.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
				} catch (ParseException ex) {
					System.out.println("Exception " + ex);
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

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			private double deg2rad(double d) {
				// TODO Auto-generated method stub
				return 0;
			}
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
}
