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


public class SendLocationServlet extends HttpServlet{
    private static final String DB_URL = "jdbc:mysql://localhost:3306/parking";
    private static final String USER = "root";
    private static final String PASS = "Hzm13602985871";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // 使用新的驱动类
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String query = "SELECT * FROM garage";  
            PreparedStatement queryPreparedStatement = conn.prepareStatement(query);
            ResultSet rs = queryPreparedStatement.executeQuery();
            JsonArrayBuilder locationsBuilder = Json.createArrayBuilder();
            while(rs.next()){
                int locx = rs.getInt(2);
                int locy = rs.getInt(3);
                //建立data部分报文
                JsonObjectBuilder locationBuilder = Json.createObjectBuilder()
                .add("locx", locx)
                .add("locy", locy);
                locationsBuilder.add(locationBuilder);
            }
            //发送报文
            response.setStatus(200);
            response.getWriter().write(locationsBuilder.build().toString());

            queryPreparedStatement.close();
            conn.close();
    
        } catch (Exception e) {
          e.printStackTrace();
          // 把错误详情发送到响应中
          response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred: " + e.toString());
        }
      }
}
