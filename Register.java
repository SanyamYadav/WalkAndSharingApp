package simpledemo;

import java.awt.BorderLayout;
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
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JLabel;

public class Register extends JFrame {

	private JPanel contentPane;
	String name;

	static String phoneNumber;
	private JButton btnNewButton_1;
	private JButton btnNewButton_2;

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
					Register frame = new Register();
					// frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	static Connection c = null;
	Statement stmt = null;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;

	public Register() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 564, 619);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JMapViewer mapViewer = new JMapViewer();
		mapViewer.setBounds(184, 6, 374, 559);
		contentPane.add(mapViewer);

		btnNewButton_2 = new JButton("Register");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				try {
					c = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/db?useSSL=false", "root",
							"Fatehjiom.!7");

					PreparedStatement stat = c.prepareStatement("SELECT * from login");
					ResultSet results = stat.executeQuery();
					while (results.next()) {

						PreparedStatement ps;

						String db_name = results.getString("name");
						String db_password = results.getString("password");
						String db_phoneNumber = results.getString("phoneNumber");

						String name = textField.getText();
						String password = textField_1.getText();
						phoneNumber = textField_2.getText();
						int id = 0;

						String a = "INSERT into login (name,password,phoneNumber,id) VALUES (?, ?, ?,?);";
						System.out.println(name);
						System.out.println(password);
						System.out.println(phoneNumber);

						try {

							ps = c.prepareStatement(a);

							ps.setString(1, name);
							ps.setString(2, password);
							ps.setString(3, phoneNumber);
							ps.setInt(4, id);
							ps.execute();

						} catch (SQLException ew) {
							// TODO Auto-generated catch block
							ew.printStackTrace();
						}

						Login t = new Login();
						t.setVisible(true);

					}

				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		btnNewButton_2.setBounds(38, 344, 117, 29);
		contentPane.add(btnNewButton_2);

		textField = new JTextField();
		textField.setBounds(25, 129, 130, 26);
		contentPane.add(textField);
		textField.setColumns(10);

		textField_1 = new JTextField();
		textField_1.setBounds(25, 192, 130, 26);
		contentPane.add(textField_1);
		textField_1.setColumns(10);

		textField_2 = new JTextField();
		textField_2.setBounds(25, 246, 130, 26);
		contentPane.add(textField_2);
		textField_2.setColumns(10);

		lblNewLabel = new JLabel("Your Name");
		lblNewLabel.setBounds(44, 101, 111, 16);
		contentPane.add(lblNewLabel);

		lblNewLabel_1 = new JLabel("Your password");
		lblNewLabel_1.setBounds(35, 164, 93, 16);
		contentPane.add(lblNewLabel_1);

		lblNewLabel_2 = new JLabel("Your Phone No");
		lblNewLabel_2.setBounds(35, 218, 106, 16);
		contentPane.add(lblNewLabel_2);

	}
}
