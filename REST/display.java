package RESTAPI_CloudComputing.RESTAPICloud;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement  // Path Annotation for XML and JSON formats

public class display
{
	String displayString; 
	
	
	public display()  //Empty Constructor to facilitate JAX RS 
	{}
	
	public display(String s)  // Constructor
	{
		this.displayString=s;
	}

	public String getDisplayString() {  //Getter
		return displayString;
	}

	public void setDisplayString(String displayString) { // Setter
		this.displayString = displayString;
	}
	

	
	
}
