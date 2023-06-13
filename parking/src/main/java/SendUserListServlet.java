import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;


public class SendUserListServlet extends HttpServlet{
    private static final String DB_URL = "jdbc:mysql://localhost:3306/parking";
    private static final String USER = "root";
    private static final String PASS = "Hzm13602985871";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pageString = request.getParameter("pageNum");
        int pageNum = Integer.parseInt(pageString);
        try {
            // 使用新的驱动类
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String query = "SELECT * FROM users";  
            PreparedStatement queryPreparedStatement = conn.prepareStatement(query);
            ResultSet rs = queryPreparedStatement.executeQuery();
            JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
            JsonArrayBuilder usersBuilder = Json.createArrayBuilder();
            int usersCount = 0;
            while(rs.next()){
                //获取用户记录数据
                String username = rs.getString(1);
                String licensePlate = rs.getString(2);
                String password = rs.getString(3);
                int locx = rs.getInt(4);
                int locy = rs.getInt(5);
                //建立data部分报文
                JsonObjectBuilder userBuilder = Json.createObjectBuilder()
                .add("username", username)
                .add("licensePlate", licensePlate)
                .add("password", password)
                .add("location", "[" + locx + "," + locy + "]");
                usersBuilder.add(userBuilder);
                usersCount++;
            }
            // 获取数据项索引范围
            int startIndex = (pageNum - 1) * 5; 
            int endIndex = startIndex + 4;
            // 防止越界
            if(endIndex + 1 > usersCount){
              endIndex = usersCount - 1;
            }
            // 构建一个新的 JsonArrayBuilder，只包含指定索引范围内的数据项
            JsonArrayBuilder selectedlocationsBuilder = Json.createArrayBuilder();
            JsonArray originalDataArray = usersBuilder.build();

            for (int i = startIndex; i <= endIndex; i++) {
              JsonObject data = originalDataArray.getJsonObject(i);
              selectedlocationsBuilder.add(data);
            }

            //构建最后的报文
            jsonBuilder.add("totalPage", (usersCount - 1) / 5 + 1)
            .add("data", selectedlocationsBuilder);
            //发送报文
            response.setStatus(200);
            response.getWriter().write(jsonBuilder.build().toString());

            queryPreparedStatement.close();
            conn.close();
    
        } catch (Exception e) {
          e.printStackTrace();
          // 把错误详情发送到响应中
          response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred: " + e.toString());
        }
      }
}
