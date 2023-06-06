import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import java.sql.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();

    String username = request.getParameter("username");
    String password = request.getParameter("psw");

    System.out.println("Received a request with data: " + username + ", " + password);

      Class.forName("com.mysql.cj.jdbc.Driver");

      System.out.println("Connected to the database.");

      String query = "SELECT * FROM users WHERE username = ? and password = ?";
      PreparedStatement ps = conn.prepareStatement(query);
      ps.setString(1, username);
      ps.setString(2, password);

      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        // Login Success
        out.println("登录成功，正在跳转...");
        System.out.println("Login success.");
      } else {
        // Login Failed
        out.println("登录失败，用户名或密码错误");
        System.out.println("Login failed.");
      }
    } catch (Exception se) {
      se.printStackTrace();
      out.println("登录失败，出现错误。错误信息：" + se.getMessage());
    }
  }
