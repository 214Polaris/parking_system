import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import java.sql.*;
@WebServlet("/edit")
public class EditServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String username = request.getParameter("username");
        String password = request.getParameter("psw");
        String licensePlate = request.getParameter("licensePlate");

        System.out.println("Received a request with data: " + username + ", " + password + ", " + licensePlate);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/parking", "root", "Hzm13602985871");

            System.out.println("Connected to the database.");

            //更新信息
            String Updatequery = "UPDATE users SET password = '?', licensePlate = '?' WHERE username = '?'";
            PreparedStatement ps = conn.prepareStatement(Updatequery);
            ps.setString(1, password);
            ps.setString(2, licensePlate);
            ps.setString(3, username);

            int row = ps.executeUpdate();
            if (row > 0) {
                // Edit Success
                out.println("更新个人信息成功");
                System.out.println("Edit success.");
            } else {
                // Edit Failed
                out.println("更新失败");
                System.out.println("Edit failed.");
            }
        } catch(Exception se) {
            se.printStackTrace();
            out.println("更新失败，出现错误。错误信息：" + se.getMessage());
        }
    }
}

