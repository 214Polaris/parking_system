import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import java.sql.*;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();

    String username = request.getParameter("username");
    String password = request.getParameter("psw");
    String plate = request.getParameter("plate");

    System.out.println("Received a request with data: " + username + ", " + password + ", " + plate);

    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/parking", "root", "chen8574jun");

      System.out.println("Connected to the database.");

      String query = "insert into users (username, password, licensePlate) values(?,?,?)";
      PreparedStatement ps = conn.prepareStatement(query);

      ps.setString(1, username);
      ps.setString(2, password);
      ps.setString(3, plate);

      int i = ps.executeUpdate();
      if (i > 0) {
        // Registration Success
        out.println("注册成功，" + "<a href='index.html'>点击这里返回首页</a>");
        System.out.println("Registration success.");
      } else {
        // Registration Failed
        out.println("注册失败，出现错误。");
        System.out.println("Registration failed.");
      }
    } catch (Exception se) {
      se.printStackTrace();
      out.println("注册失败，出现错误。错误信息：" + se.getMessage());
    }
  }
}
