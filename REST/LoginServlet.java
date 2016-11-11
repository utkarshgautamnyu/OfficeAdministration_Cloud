package RESTAPI_CloudComputing.RESTAPICloud;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	
	String user_assign;
	String bucketName_assign;
	String key_assign;
    
    public LoginServlet() {
     }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	
	
   
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doGet(request, response);
		
		user_assign = request.getParameter("username");
         bucketName_assign = request.getParameter("bucketName");
         key_assign = request.getParameter("key");
        
         
        
        
        String temp= "Get Office Status";     
        MyResource.user=user_assign;
		MyResource.bucketName=bucketName_assign;
		MyResource.key=key_assign;
        
        
         
        // build HTML code
		PrintWriter writer = response.getWriter();
       
        String htmlResponse = "";
        htmlResponse+= "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>REST API</title></head>";
        htmlResponse += "<h3> User name is: " + user_assign + "</h3><br/>";      
        htmlResponse += "<h3> Bucket name is: " + bucketName_assign + "</h3><br/>";  
        htmlResponse += "<h3> Object Key  is: " + key_assign + "</h3><br/>";  
        htmlResponse += " <h3> <a href='webapi/OfficeStatus'> "+ temp + "</a> <h3> <br/>"; 
        htmlResponse += "</html>";
         
        // return response
        writer.println(htmlResponse);
	}
	
	
	
	
	
	

}
