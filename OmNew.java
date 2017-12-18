package simpledemo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
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
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.event.ActionEvent;

public class OmNew extends JFrame {

	
	private JPanel contentPane1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				
				
				try {
					OmNew frame = new OmNew();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
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
	
	
	

	public OmNew() {
		
		JMapViewer mapViewer = new JMapViewer();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 564, 619);
		contentPane1 = new JPanel();
		contentPane1.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane1);
		contentPane1.setLayout(null);
		
		mapViewer.setBounds(97, 11, 441, 559);
		contentPane1.add(mapViewer);
	

		
		
		JButton btnNewButton = new JButton("Show Point");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				

				draw_route("AB101AA", "AB101AH");
				
		}

		//	private void draw_route(String string, String string2) {
				private  void draw_route(String start_address, String end_address) {
					MapMarkerDot marker = new MapMarkerDot(52, -6);
					marker.setColor(Color.RED);

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
						System.out.println("This is the polyline" + ActualPolyline);

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

									System.out.println("DECODE" + decode(ActualPolyline, 1));

									for (int k = 0; k <= decode(ActualPolyline, 1).size() - 1; k++) {

										Point point = decode(ActualPolyline, 1).get(k);
										double _latt = point.getLat();
										double _long = point.getLng();
										System.out.println("Yoooooooo __ : " + _latt / 100000.0);
										System.out.println("Loooooooo __ : " + _long / 100000.0);
										Coordinate c = new Coordinate(_latt / 100000.0, _long / 100000.0);
										coordinates.add(c);

									}

								} else {
									JSONObject substep_start = (JSONObject) substeps.get("start_location");
									JSONObject substep_end = (JSONObject) substeps.get("end_location");
									System.out.println(" End point is: " + substep_end);
									Double e1 = (Double) substep_start.get("lng");
									Double f = (Double) substep_end.get("lat");
									System.out.println("this is e1 " + e1);
									System.out.println("this is f" + f);

									System.out.println("DECODE else" + decode(ActualPolyline, 1));

									for (int k = 0; k <= decode(ActualPolyline, 1).size() - 1; k++) {

										Point point = decode(ActualPolyline, 1).get(k);
										double _latt = point.getLat();
										double _long = point.getLng();
										System.out.println("Yoooooooo __ : " + _latt / 100000.0);
										System.out.println("Loooooooo __ : " + _long / 100000.0);
										Coordinate c = new Coordinate(_latt / 100000.0, _long / 100000.0);
										coordinates.add(c);
										

									}
								}
							}
						}	
								
							System.out.println(coordinates);

								MapPolygonImpl me = new MapPolygonImpl(coordinates); //
								mapViewer.addMapPolygon(me);
								
								mapViewer.setDisplayToFitMapPolygons();
							
							

						
					}
					 catch (IOException e) {

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				
				// TODO Auto-generated method stub
				
			}
		});
	
		btnNewButton.setBounds(10, 43, 77, 23);
		contentPane1.add(btnNewButton);
	}
}
