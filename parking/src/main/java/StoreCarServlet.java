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

      // 建立车库数组
      int[][] garageMap = new int[4][4];
      // 判断有无空位
      String garageQuery = "SELECT * FROM garage";
      PreparedStatement garageStatement = conn.prepareStatement(garageQuery);
      ResultSet garage_rs = garageStatement.executeQuery();
      int count = 0;// 记录有多少个车位被使用
      while (garage_rs.next()) {
        int locx = (int) garage_rs.getObject(2);
        int locy = (int) garage_rs.getObject(3);
        garageMap[locx][locy] = 1;
        count++;
      }
      // 如果count等于16.说明已经没有空车位，返回420
      if (count == 16) {
        response.setStatus(420);
        System.out.println("车库已满");
        return;
      }
      garageStatement.close();

      // 先查询是否已经存车
      String query = "SELECT * FROM cars WHERE licensePlate = ? ORDER BY id DESC LIMIT 1";
      PreparedStatement queryStatement = conn.prepareStatement(query);
      queryStatement.setString(1, licensePlate);
      ResultSet rs = queryStatement.executeQuery();
      if (rs.next()) {
        // 看取车时间是否为空，空说明已经存车
        if (rs.getObject(4) == null) {
          response.setStatus(401);
          System.out.println("已存车，不能重复存车");
          queryStatement.close();
          return;
        }
      }

      // 遍历garage表确定locx和locy
      int locx = -1;
      int locy = -1;
      for (int i = 0; i < 4; i++) {
        for (int j = 0; j < 4; j++) {
          if (garageMap[i][j] == 0) {
            locx = i;
            locy = j;
            break;
          }
        }
        // 已经找到车位
        if (locx != -1 && locy != -1) {
          break;
        }
      }

      // 修改users表
      String usersQuery = "UPDATE users SET locx = ?, locy = ? WHERE licensePlate = ?";
      PreparedStatement usersStatement = conn.prepareStatement(usersQuery);
      usersStatement.setInt(1, locx);
      usersStatement.setInt(2, locy);
      usersStatement.setString(3, licensePlate);
      usersStatement.executeUpdate();
      usersStatement.close();

      // 修改cars表
      String insertQuery = "INSERT INTO cars (licensePlate, entryTime, locx, locy) VALUES (?, ?, ?, ?)";
      PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);
      preparedStatement.setString(1, licensePlate);
      preparedStatement.setTimestamp(2, entryTime);
      preparedStatement.setInt(3, locx);
      preparedStatement.setInt(4, locy);
      preparedStatement.executeUpdate();
      preparedStatement.close();

      // 添加到garage表
      String garageInsert = "INSERT INTO garage (licensePlate, locx, locy) VALUES (?, ?, ?)";
      PreparedStatement garageInsertStatement = conn.prepareStatement(garageInsert);
      garageInsertStatement.setString(1, licensePlate);
      garageInsertStatement.setInt(2, locx);
      garageInsertStatement.setInt(3, locy);
      garageInsertStatement.executeUpdate();
      garageInsertStatement.close();

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