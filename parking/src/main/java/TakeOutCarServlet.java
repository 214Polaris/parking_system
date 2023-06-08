import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;

public class TakeOutCarServlet extends HttpServlet {
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String licensePlate = request.getParameter("licensePlate");
    Timestamp exitTime = new Timestamp(System.currentTimeMillis()); // 创建当前时间戳

    try {
      Class.forName("com.mysql.jdbc.Driver");
      Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/parking", "root", "Hzm13602985871");

      //先判断是否有存车
      String query = "SELECT * FROM cars WHERE licensePlate = ? ORDER BY id DESC LIMIT 1";
      PreparedStatement queryStatement = conn.prepareStatement(query);
      queryStatement.setString(1, licensePlate);
      ResultSet rs = queryStatement.executeQuery();
      if (rs.next()){
        //看取车时间是否为空，非空说明没有存车
        if(rs.getObject(3) != null){
          response.setStatus(422);
          System.out.println("还未存车，不能取车");
          queryStatement.close();
          return;
        }
      }

      //取车，修改取车时间和费用
      String selectQuery = "SELECT entryTime FROM cars WHERE licensePlate = ? ORDER BY id DESC LIMIT 1";
      PreparedStatement preparedStatement = conn.prepareStatement(selectQuery);
      preparedStatement.setString(1, licensePlate);
      ResultSet resultSet = preparedStatement.executeQuery();

      if (resultSet.next()) {
        Timestamp entryTime = resultSet.getTimestamp("entryTime");
        long duration = exitTime.getTime() - entryTime.getTime();
        long durationInHours = duration / (60 * 60 * 1000);
        long cost = 5 * durationInHours;

        // 添加更新语句来保存departureTime和fee
        String updateQuery = "UPDATE cars SET departureTime = ?, fee = ? WHERE licensePlate = ? and entryTime = ?";
        PreparedStatement updateStatement = conn.prepareStatement(updateQuery);
        updateStatement.setTimestamp(1, exitTime);
        updateStatement.setLong(2, cost);
        updateStatement.setString(3, licensePlate);
        updateStatement.setTimestamp(4, entryTime);
        updateStatement.executeUpdate();
        // 将返回的消息和费用信息封装成JSON格式
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("message", "Take out Car Operation Successful!");
        builder.add("cost", cost);
        response.setContentType("application/json");
        response.getWriter().write(builder.build().toString());
        response.setStatus(HttpServletResponse.SC_OK);

      } else {
        // 如果数据库中没有这辆车的记录
        response.getWriter().write("No record found for this car.");
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      }

      resultSet.close();
      preparedStatement.close();
      conn.close();
    } catch (Exception e) {
      e.printStackTrace();
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }
}
