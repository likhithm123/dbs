package edu.campus;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet(urlPatterns={"/"})
public class AppServlet extends HttpServlet {
    private static final Map<String,String> TABLES = Map.of(
        "Events","EVENT","Venues","VENUE","Sessions","SESSION","Resources","RESOURCE",
        "Registrations","REGISTRATION","Payments","PAYMENT","Allocations","RESOURCE_ALLOCATION","Users","APP_USER");
    private static final Map<String,List<String>> COLUMNS = Map.of(
        "EVENT", List.of("name","type","start_date","end_date","status"),
        "VENUE", List.of("name","capacity","location"),
        "SESSION", List.of("event_id","title","start_time","end_time","venue_id"),
        "RESOURCE", List.of("name","type","quantity_total","location"),
        "REGISTRATION", List.of("event_id","user_id","registration_date","status"),
        "PAYMENT", List.of("registration_id","amount","payment_date","status"),
        "RESOURCE_ALLOCATION", List.of("event_id","session_id","resource_id","quantity","allocated_at"),
        "APP_USER", List.of("name","email","phone","password_hash","role"));
    private static final Map<String,String> PK = Map.of("EVENT","event_id","VENUE","venue_id","SESSION","session_id","RESOURCE","resource_id","REGISTRATION","registration_id","PAYMENT","payment_id","RESOURCE_ALLOCATION","allocation_id","APP_USER","user_id");

    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException {
        String chosen=req.getParameter("table");
        if(chosen==null || !TABLES.containsKey(chosen)) chosen="Events";
        req.setAttribute("chosen",chosen); req.setAttribute("tables",TABLES);
        List<String> columns=new ArrayList<>(); List<Map<String,Object>> rows=new ArrayList<>(); String error=null;
        try(Connection c=Db.get(); Statement s=c.createStatement(); ResultSet rs=s.executeQuery("SELECT * FROM "+TABLES.get(chosen)+" FETCH FIRST 100 ROWS ONLY")) {
            ResultSetMetaData md=rs.getMetaData(); for(int i=1;i<=md.getColumnCount();i++) columns.add(md.getColumnLabel(i));
            while(rs.next()){Map<String,Object> row=new LinkedHashMap<>(); for(String col:columns) row.put(col,rs.getObject(col)); rows.add(row);}
        } catch(Exception e) { error=e.getMessage(); }
        req.setAttribute("columns",columns); req.setAttribute("rows",rows); req.setAttribute("error",error);
        req.setAttribute("formColumns",COLUMNS.get(TABLES.get(chosen)));
        req.getRequestDispatcher("/index.jsp").forward(req,resp);
    }
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8"); String chosen=req.getParameter("table");
        if(chosen==null || !TABLES.containsKey(chosen)){resp.sendError(400);return;}
        String table=TABLES.get(chosen), action=req.getParameter("action");
        try(Connection c=Db.get()) {
            if("create".equals(action)) {
                List<String> cols=COLUMNS.get(table); StringJoiner names=new StringJoiner(","), marks=new StringJoiner(",");
                for(String col:cols){names.add(col);marks.add("?");}
                try(PreparedStatement ps=c.prepareStatement("INSERT INTO "+table+" ("+names+") VALUES ("+marks+")")) {
                    for(int i=0;i<cols.size();i++){String val=req.getParameter(cols.get(i)); if(val==null||val.isBlank()) ps.setNull(i+1,Types.NULL); else ps.setString(i+1,val);}
                    ps.executeUpdate(); req.getSession().setAttribute("notice","Record created successfully.");
                }
            } else if("delete".equals(action)) {
                String id=req.getParameter("id");
                try(PreparedStatement ps=c.prepareStatement("DELETE FROM "+table+" WHERE "+PK.get(table)+"=?")){ps.setString(1,id);ps.executeUpdate();}
                req.getSession().setAttribute("notice","Delete request processed.");
            }
        } catch(Exception e){req.getSession().setAttribute("notice","Operation failed: "+e.getMessage());}
        resp.sendRedirect(req.getContextPath()+"/?table="+chosen);
    }
}