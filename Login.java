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

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JLabel;

public class Login extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JLabel Name;
	private JLabel lblNewLabel_1;
	public static String name;
	static Connection c = null;
	Statement stmt = null;
	private JButton btnNewButton_1;
	private JButton btnNewButton_2;

	/**
	 * Launch the application.
	 */

	/**
	 * Create the frame.
	 */

	public static void main(String[] args) {

		try {

		} catch (Exception e9) {
			e9.printStackTrace();
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Login() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 564, 619);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JMapViewer mapViewer = new JMapViewer();
		mapViewer.setBounds(184, 6, 374, 559);
		contentPane.add(mapViewer);

		JButton btnNewButton = new JButton("Login");
		btnNewButton.addActionListener(new ActionListener() {
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

						String name = textField.getText();
						String password = textField_1.getText();

						// String a = "INSERT into login (name,password) VALUES (?, ?);";
						System.out.println(name);
						System.out.println(password);

						if (name.equals(db_name) && password.equals(db_password)) {

							MAIN_ t = new MAIN_();
							t.setVisible(true);

						}

					}

				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					c.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnNewButton.setBounds(51, 274, 77, 23);
		contentPane.add(btnNewButton);

		textField = new JTextField();
		textField.setBounds(24, 150, 130, 26);
		contentPane.add(textField);
		textField.setColumns(10);

		textField_1 = new JTextField();
		textField_1.setBounds(24, 221, 130, 26);
		contentPane.add(textField_1);
		textField_1.setColumns(10);

		Name = new JLabel("Name");
		Name.setBounds(53, 122, 87, 16);
		contentPane.add(Name);

		lblNewLabel_1 = new JLabel("Password");
		lblNewLabel_1.setBounds(51, 193, 61, 16);
		contentPane.add(lblNewLabel_1);

		btnNewButton_1 = new JButton("New button");
		btnNewButton_1.setBounds(24, 337, -165, -17);
		contentPane.add(btnNewButton_1);

		btnNewButton_2 = new JButton("Register");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				Register t = new Register();
				t.setVisible(true);

			}
		});
		btnNewButton_2.setBounds(34, 323, 117, 29);
		contentPane.add(btnNewButton_2);

	}

}
