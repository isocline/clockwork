package isocline.clockwork.examples;

import java.sql.*;

public class JdbcTest {

    public static void main(String[] args) throws Exception {

        Class.forName("org.gjt.mm.mysql.Driver");

        String connectionUrl = "jdbc:mysql://192.168.99.100:13306/dbridge?autoReconnect=true&amp;useUnicode=true&amp;characterEncoding=utf-8&amp;mysqlEncoding=utf8;user=root;password=root";

        try (Connection con = DriverManager.getConnection(connectionUrl, "root", "root");
        ) {

            String SQL = "SELECT * FROM DBT_BO_MASTER";
            long t0 = System.currentTimeMillis();
            PreparedStatement stmt = con.prepareStatement(SQL);

            //Statement stmt = con.createStatement();

            //ResultSet rs = stmt.executeQuery(SQL);


            long t1 = System.currentTimeMillis();
            stmt.execute();
            long t2 = System.currentTimeMillis();

            ResultSet rs = stmt.getResultSet();
            long t3 = System.currentTimeMillis();


            System.err.println(t1 - t0);
            System.err.println(t2 - t1);
            System.err.println(t3 - t2);


            while (rs.next()) {
                //System.out.println(rs.getString("USR_ID") + " " + rs.getString("USR_NM"));
            }
            long t4 = System.currentTimeMillis();
            System.err.println(t4 - t3);
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
