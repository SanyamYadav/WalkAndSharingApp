package simpledemo;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.event.ActionEvent;

public class Test3 extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {

				try {
					Test3 frame = new Test3();
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
	public Test3() {
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

				List<ICoordinate> initial = new ArrayList<>();
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


				for (int i = 0; i < initial.size() - 1; i++) {
					List<ICoordinate> coordinates = new ArrayList<>();
					Coordinate z = (Coordinate) initial.get(i);
					coordinates.add(z);
					z = (Coordinate) initial.get(i + 1);
					coordinates.add(z);
					z = (Coordinate) initial.get(i + 1);
					coordinates.add(z);
					MapPolygonImpl x = new MapPolygonImpl(coordinates);
					mapViewer.addMapPolygon(x);
				}

			}
		});
		btnNewButton.setBounds(10, 43, 77, 23);
		contentPane.add(btnNewButton);
	}
}