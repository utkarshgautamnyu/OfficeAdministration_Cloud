package RESTAPI_CloudComputing.RESTAPICloud;

import java.util.ArrayList;


import org.apache.commons.io.IOUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;

//@XmlRootElement
/**
 * Root resource (exposed at "myresource" path)
 */

import java.awt.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;


@Path("OfficeStatus") 


public class MyResource {

	
	
	
	        
	//*******************************Global Variables***********************************************
	 static String user;
	Scanner scanner;
	 static String bucketName;
	 static String key;
	static ArrayList<display> result =new ArrayList<>(); // Global Variable to facilitate input stream to JSON conversion
	static ArrayList<display> result_final =new ArrayList<>();
	
	
	 
	@GET
    @Produces(MediaType.APPLICATION_JSON) // Indicates JSON response
	
	
    
	public static  ArrayList<display> getIt() throws IOException {
	    
    	
    		AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider(user).getCredentials(); // Generating credentials for the user
        } 
        catch (Exception e) 
        {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (C:\\Users\\utkar\\.aws\\credentials), and is in valid format.",
                    e);
        }
    

		
		
		AmazonS3 s3 = new AmazonS3Client(credentials);  // Object for AmazonS3 class
        Region usWest = Region.getRegion(Regions.US_WEST_2);
        s3.setRegion(usWest);

        try
        
        {
        	
        	
        	
        	System.out.println("Downloading an object");
        	S3Object object = s3.getObject(new GetObjectRequest(bucketName, key));
        	
        	displayTextInputStream(object.getObjectContent()); // Function to convert input stream obtained from
        													   // getObjectContent() to JSON
        	
        
        	
        	
        	
        }
        catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    	
       // System.out.println(result.toString());
        
        
        
    	return result;
    	
    }
    
    public static void displayTextInputStream(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line;
       
        while((line = reader.readLine()) != null) {
       
        	display d=new display(line);  
        	result.add(d);  // Storing String in an ArrayList result
            
        }
       
        
    }
    
    
    
        
    

}
