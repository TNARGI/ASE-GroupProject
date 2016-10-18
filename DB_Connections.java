
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by mehmetparlak on 16.10.2016.
 */

public class DB_Connections {

	public static String con_host = "ec2-54-75-232-50.eu-west-1.compute.amazonaws.com";
	public static String con_db = "dco8tkjakrusuh";
	public static String con_user = "cdkxzqakhjojtw";
	public static String con_pass = "iSwVqlYEeZ8XQEBdSfMuJHZI3Y";

	public DB_Connections() {

	}

	public DB_Connections(String con_host, String con_user, String con_pass) {
		this.con_host = con_host;
		this.con_user = con_user;
		this.con_pass = con_pass;
	}

	public static void ConnecttoDatabase() throws SQLException {
		System.out.println("DB'ye baglanmaya calisiyor.");

		try {

			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {

			System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
			e.printStackTrace();
			return;

		}

		String host = con_host;
		String user = con_user;
		String pass = con_pass;

		try {
			// Connection con= DriverManager.getConnection(host,user,pass);
			/*Connection con = DriverManager.getConnection(
					"jdbc:postgresql://ec2-54-75-232-50.eu-west-1.compute.amazonaws.com/dco8tkjakrusuh",
					"cdkxzqakhjojtw", "iSwVqlYEeZ8XQEBdSfMuJHZI3Y");*/
			
			Connection con= DriverManager.getConnection("jdbc:postgres://<cdkxzqakhjojtw>:<iSwVqlYEeZ8XQEBdSfMuJHZI3Y>@<ec2-54-75-232-50.eu-west-1.compute.amazonaws.com>:<5432>/<dco8tkjakrusuh>");

			
			

			String query = "Select longitude from public.location where id=1";
			String query2 = "INSERT INTO public.location(\n" + "\tuser_id, longitude, latitude, d_date)\n"
					+ "\tVALUES (2, '35.333', '17.345', '16.10.2016');";

			Statement s = con.createStatement();
			Statement s2 = con.createStatement();
			s2.execute(query2);
			ResultSet rs = s.executeQuery(query);
			while (rs.next()) {
				String st_long = rs.getString("longitude");
				System.out.println(st_long);

			}

			s.close();
			con.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
