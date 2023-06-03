import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.json.Json;
import javax.json.JsonObjectBuilder;

public class UserInfoServlet extends HttpServlet {

  private static final String DB_URL = "jdbc:mysql://localhost:3306/parking";
  private static final String USER = "root";
  private static final String PASS = "Hzm13602985871";

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String username = request.getParameter("username");

    if (username == null || username.trim().isEmpty()) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Username is required.");
      return;
    }

    try {
      // 使用新的驱动类
      Class.forName("com.mysql.cj.jdbc.Driver");

      Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

      String sql = "SELECT licensePlate FROM users WHERE username = ?";
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setString(1, username);

      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        String licensePlate = rs.getString("licensePlate");

        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("username", username);
        builder.add("licensePlate", licensePlate);

        response.setContentType("application/json");
        response.getWriter().write(builder.build().toString());
      } else {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found.");
      }

      rs.close();
      stmt.close();
      conn.close();

    } catch (Exception e) {
      e.printStackTrace();
      // 把错误详情发送到响应中
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred: " + e.toString());
    }
  }
}
