package simpledemo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.awt.event.ActionEvent;

public class trying extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		List<ICoordinate> initial = new ArrayList<>();
		List<ICoordinate> initial2 = new ArrayList<>();

		Coordinate c = new Coordinate(56.1, -2);
		initial.add(c);
		c = new Coordinate(56.7, -2.1);
		initial.add(c);
		c = new Coordinate(55.7, -2.5);
		initial.add(c);
		c = new Coordinate(60.7, -3.4);
		initial.add(c);
		c = new Coordinate(65.7, -5.4);
		initial.add(c);
		c = new Coordinate(20.7, -1.4);
		initial.add(c);
		c = new Coordinate(12, -12);
		initial.add(c);

		// ========
		Coordinate d = new Coordinate(56, -2);
		initial2.add(d);
		d = new Coordinate(55, -2);
		initial2.add(d);
		d = new Coordinate(60.7, -3.4);
		initial2.add(d);
		d = new Coordinate(65.7, -5.4);
		initial2.add(d);
		d = new Coordinate(20.7, -1.4);
		initial2.add(d);
		d = new Coordinate(12, -12);
		initial2.add(d);
		d = new Coordinate(11, -12);
		initial2.add(d);
		d = new Coordinate(10, -12);
		initial2.add(d);

		partial(initial, initial2);

		EventQueue.invokeLater(new Runnable() {
			public void run() {

				try {
					trying frame = new trying();
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
	static Coordinate common_start;
	static Coordinate common_end;

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

				}
				// System.out.println(initial);
			}
		}

		String one = common_start.toString();

		one = one.replaceAll(" ", "");
		one = one.replaceAll("\\(", "");
		one = one.replaceAll("\\)", "");

		System.out.println(common_start);
		System.out.println(common_end);
		System.out.println(one);

		return fin;

	}

	public trying() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 564, 619);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JMapViewer mapViewer = new JMapViewer();
		mapViewer.setBounds(97, 11, 441, 559);
		contentPane.add(mapViewer);

		JButton btnNewButton = new JButton("Show Point");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean False = false;

				MapMarkerDot marker = new MapMarkerDot(56.7, -2.1);
				marker.setName("FIRST");
				
				boolean True = true;
				marker.setColor(Color.RED);
				mapViewer.addMapMarker(marker);
				marker.setVisible(True);
				
				
				MapMarkerDot marker2 = new MapMarkerDot(55.7, -2.5);
				marker2.setName("SECOND");
				
				marker2.setColor(Color.RED);
				mapViewer.addMapMarker(marker2);
				marker2.setVisible(True);
				
				System.out.println(Login.name);

				List<ICoordinate> initial = new ArrayList<>();

				List<ICoordinate> coordinates_ = new ArrayList<>();
				List<ICoordinate> final_ = new ArrayList<>();

				Coordinate c = new Coordinate(56.1, -2);
				initial.add(c);
				c = new Coordinate(56.7, -2.1);
				initial.add(c);
				c = new Coordinate(55.7, -2.5);
				initial.add(c);
				c = new Coordinate(60.7, -3.4);
				initial.add(c);
				c = new Coordinate(65.7, -5.4);
				initial.add(c);
				c = new Coordinate(20.7, -1.4);
				initial.add(c);
				c = new Coordinate(12, -12);
				initial.add(c);

				int l = initial.size();
				System.out.println(l + "l");
				int ll = initial.size() * 2;
				for (int i = 0; i < l; i++) {

					Coordinate z = (Coordinate) initial.get(i);
					coordinates_.add(z);

					System.out.println("zzz" + z);
					System.out.println("IIIIII" + coordinates_);

					final_.add(z);
					System.out.println(l + "l");
					if (i == l - 1) {

						// System.out.println(ll+"ll");
						for (int j = l - 1; j > -1; j--) {
							Coordinate y = (Coordinate) initial.get(j);
							coordinates_.add(y);
							System.out.println("yyy" + y);
							System.out.println("JJJJJ" + coordinates_);
							final_.add(y);
						}

					}

				}
				System.out.println("final_" + final_);
				MapPolygonImpl x = new MapPolygonImpl(final_);
				mapViewer.addMapPolygon(x);
				mapViewer.setDisplayToFitMapPolygons();
			}
		});
		btnNewButton.setBounds(10, 43, 77, 23);
		contentPane.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("New button");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		btnNewButton_1.setBounds(6, 188, 86, 29);
		contentPane.add(btnNewButton_1);
	}
}