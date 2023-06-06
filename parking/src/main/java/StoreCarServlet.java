import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class StoreCarServlet extends HttpServlet {
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // 从session中获取用户信息
    String licensePlate = request.getParameter("licensePlate");
    Timestamp entryTime = new Timestamp(System.currentTimeMillis()); // 创建当前时间戳
    // 将licensePlate和entryTime存入数据库的cars表单
    try {
      // 连接数据库
      Class.forName("com.mysql.jdbc.Driver");
      Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/parking", "root", "chen8574jun");

      // 执行插入数据的SQL语句
      String insertQuery = "INSERT INTO cars (licensePlate, entryTime) VALUES (?, ?)";
      PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);
      preparedStatement.setString(1, licensePlate);
      preparedStatement.setTimestamp(2, entryTime);
      preparedStatement.executeUpdate();

      // 关闭连接
      preparedStatement.close();
      conn.close();

    } catch (Exception e) {
      // 处理异常情况
      e.printStackTrace();
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return;
    }
    // 返回存车成功的响应
    response.setStatus(HttpServletResponse.SC_OK);
  }
}