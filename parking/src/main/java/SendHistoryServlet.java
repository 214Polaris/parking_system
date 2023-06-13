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


public class SendHistoryServlet extends HttpServlet{
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
            String query = "SELECT * FROM cars";  
            PreparedStatement queryPreparedStatement = conn.prepareStatement(query);
            ResultSet rs = queryPreparedStatement.executeQuery();
            JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
            JsonArrayBuilder carsBuilder = Json.createArrayBuilder();
            int carsCount = 0;
            while(rs.next()){
                //获取停车记录数据
                String id = rs.getString(1);
                String licensePlate = rs.getString(2);
                String entryTime = rs.getString(3);
                String departureTime = rs.getString(4);
                if(departureTime == null){
                    departureTime = "";
                }
                Float fee = rs.getFloat(5);
                int locx = rs.getInt(6);
                int locy = rs.getInt(7);
                //建立data部分报文
                JsonObjectBuilder carBuilder = Json.createObjectBuilder()
                .add("id", id)
                .add("licensePlate", licensePlate)
                .add("entryTime", entryTime)
                .add("departureTime", departureTime)
                .add("fee", fee)
                .add("location", "(" + locx + "," + locy + ")");
                carsBuilder.add(carBuilder);
                carsCount++;
            }
            // 获取数据项索引范围
            int startIndex = (pageNum - 1) * 5; 
            int endIndex = startIndex + 4;
            // 防止越界
            if(endIndex + 1 > carsCount){
              endIndex = carsCount - 1;
            }
            // 构建一个新的 JsonArrayBuilder，只包含指定索引范围内的数据项
            JsonArrayBuilder selectedlocationsBuilder = Json.createArrayBuilder();
            JsonArray originalDataArray = carsBuilder.build();

            for (int i = startIndex; i <= endIndex; i++) {
              JsonObject data = originalDataArray.getJsonObject(i);
              selectedlocationsBuilder.add(data);
            }

            //构建最后的报文
            jsonBuilder.add("totalPage", (carsCount - 1) / 5 + 1)
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
