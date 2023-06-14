import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import java.sql.*;

@WebServlet("/edit")
public class EditServlet extends HttpServlet {
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    String typeNum = request.getParameter("type");
    int type = Integer.parseInt(typeNum);
    String oldName = request.getParameter("name");
    String context;
    // 判断要修改的内容类型
    if (type == 1) {
      context = request.getParameter("username");
    } else if (type == 2) {
      context = request.getParameter("psw");
    } else {
      context = request.getParameter("licensePlate");
    }
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/parking", "root", "Hzm13602985871");

      System.out.println("Connected to the database.");

      // 更新信息
      // 判断要修改的内容类型
      String Updatequery = "";
      if (type == 1) {
        Updatequery = "UPDATE users SET username = ? WHERE username = ?";
      } else if (type == 2) {
        Updatequery = "UPDATE users SET password = ? WHERE username = ?";
      } else {
        Updatequery = "UPDATE users SET licensePlate = ? WHERE username = ?";
      }
      PreparedStatement ps = conn.prepareStatement(Updatequery);
      ps.setString(1, context);
      ps.setString(2, oldName);
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
    } catch (Exception se) {
      se.printStackTrace();
      out.println("更新失败，出现错误。错误信息：" + se.getMessage());
    }
  }
}
