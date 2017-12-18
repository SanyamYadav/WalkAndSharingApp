package simpledemo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.border.EmptyBorder;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * 
 * @author Kevin Sim
 * 
 *         Very simple demo of JMapViewer
 * 
 *         More info at http://wiki.openstreetmap.org/wiki/JMapViewer
 * 
 *         A more comprehensive example can be fouund in
 *         org.openstreetmap.gui.jmapviewer.Demo
 *
 */
public class simpledemoOLD extends JFrame {

	private JMapViewer map;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					simpledemoOLD frame = new simpledemoOLD();
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
	public simpledemoOLD() {
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

		JButton btnNewButton = new JButton("New button");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				map.getMapMarkerList().clear();

				MapMarkerDot marker = new MapMarkerDot(52, -6);
				marker.setColor(Color.RED);
				map.addMapMarker(marker);

			}
		});
		btnNewButton.setBounds(47, 42, 89, 23);
		controlPanel.add(btnNewButton);

		ArrayList<PostCode> postCodes = PostCode.getPostcodes();
		for (PostCode pc : postCodes) {
			map.addMapMarker(new MapMarkerDot(pc));
		}

	}
}