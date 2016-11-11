///////////////////////////////////////////////////////////////////
//CS9223-INT Cloud Computing
//Assignment 1-A Virtual Company (October 3th 2016)
//Utkarsh Gautam
//Christopher Chan
//Amazon advertises, but does not support Java-based Lambda with AWS SDK for instance control/evented service
/////////////////////////////////////////////////////////////////
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.AttachRolePolicyRequest;
import com.amazonaws.services.identitymanagement.model.CreatePolicyRequest;
import com.amazonaws.services.identitymanagement.model.CreateRoleRequest;
import com.amazonaws.services.identitymanagement.model.CreateRoleResult;
import com.amazonaws.services.identitymanagement.model.GetRoleRequest;
import com.amazonaws.services.identitymanagement.model.GetRoleResult;
import com.amazonaws.services.identitymanagement.model.Role;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.CreateFunctionRequest;
import com.amazonaws.services.lambda.model.DeleteFunctionRequest;
import com.amazonaws.services.lambda.model.FunctionCode;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;


public class CS9223Assignment1 {
	
	public static void main(String[] args) throws InterruptedException {
		//Credentials
		String AWSAccessKeyId ="";//"AKIAJKFOYQBMFDQZFBCA";
		String AWSSecretKey ="";//gFpTXmaMTk3Q4v0ZCpi8UW/uM61Ei5y5OLvAy1FO";
		Scanner scanner; 
		if (AWSAccessKeyId.length()<1){
			scanner = new Scanner (System.in);
			System.out.println("AWSAcessKeyId:");
			AWSAccessKeyId = scanner.nextLine();
		}
		if (AWSSecretKey.length()<1){
			scanner = new Scanner (System.in);
			System.out.println("AWSSecretKey:");
			AWSSecretKey = scanner.nextLine();
		}
		String endPoint = "https://rds.us-west-2.amazonaws.com";
		Region region = Region.getRegion(Regions.US_WEST_2);
		AWSCredentials credentials = new BasicAWSCredentials(AWSAccessKeyId, AWSSecretKey);

		//S3 Connection
		AmazonS3 s3Client = new AmazonS3Client(credentials);
		s3Client.setEndpoint(endPoint);
		s3Client.setRegion(region);
				
		//EC2
		AmazonEC2Client ec2client = new AmazonEC2Client(credentials);
		ec2client.setEndpoint(endPoint);
		ec2client.setRegion(region);
		
		//AWS Lambda Client
		AWSLambdaClient lambda = new AWSLambdaClient(credentials);
		lambda.setEndpoint(endPoint);
		lambda.setRegion(region);
		
		//AWS IdentityManagement
		AmazonIdentityManagement iam = new AmazonIdentityManagementClient(credentials);
		iam.setEndpoint(endPoint);
		iam.setRegion(region);
		
	    //Parameters
		String keyName = "cs9223_keys";
		String groupName = "cs9223SecurityGroup";
		String groupDescription = "This is a Basic Security Group";
		String imageId ="ami-b04e92d0";
		String instanceType ="t2.micro";
		String instanceName = "Instance4Employee";
		String logFilename = "logFile.txt";
		String jarFileName = "lambda.jar";
		String javaClassName = "lambdaScheduler.InstanceScheduler";
		
		scanner = new Scanner (System.in);
		String defaultFilePath = System.getProperty("user.home")+"\\.aws";
		while (Files.notExists(FileSystems.getDefault().getPath(defaultFilePath))) {
			System.out.println(defaultFilePath+" does not exist. Please enter a valid path...");
			System.out.print("Path:");
			defaultFilePath = scanner.nextLine(); 
		}
		System.out.println("Default FilePath:"+defaultFilePath);
		String keyPath = defaultFilePath;
		String logFilePath = defaultFilePath;
		String jarFilePath = defaultFilePath;
		
		//Date
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mmaa");
		Calendar startTime = Calendar.getInstance();
		Calendar stopTime = Calendar.getInstance();
		
		int numEmployees = getNumEmployees(scanner);
		getWorkDayTimes(scanner,sdf, startTime, stopTime);
		String bucketName = createBucket(scanner,s3Client);
		
		long startMillisec = startTime.getTimeInMillis()-Calendar.getInstance().getTimeInMillis();
		long stopMillisec = stopTime.getTimeInMillis()-Calendar.getInstance().getTimeInMillis();
		
		// list buckets
		System.out.print("Secret text:");
		String secretText = scanner.nextLine();
		
		System.out.println("-----------------------------------");
		System.out.println("Num Employees:"+numEmployees);
		System.out.println("Start of workday:"+sdf.format(startTime.getTime()));
		System.out.println("End of workday:"+sdf.format(stopTime.getTime()));
		System.out.println("Bucket name:"+bucketName);
		System.out.println("Secret text:"+secretText);
		System.out.println("-----------------------------------");
		scanner.close();

		createEC2SecurityGroup(groupName, groupDescription, ec2client);

		//Lambda Function-Amazon's Evented Service-Not used as Java AWS is not supported on the Lambda system 
		//uploadJarFileToBucket(s3Client,jarFilePath,jarFileName,bucketName);
		//deployLambdaFunction(iam,lambda,"InstanceScheduler",bucketName,jarFileName,javaClassName);
		//invokeLambdaFunction(lambda, startMillisec, stopMillisec,numEmployees,bucketName,secretText);
		
		//Scheduler-A variant of the source code below was intended to run on Lambda
		while (startMillisec>0) {
			if (startMillisec<10000) {
				Thread.sleep(startMillisec);
				startMillisec = 0;
			} else {
				Thread.sleep(10000);
				startMillisec-=10000;
			}
			long startSec = startMillisec/1000;
			System.out.println(startSec+" seconds remaining till workday begins and instances are started");
		}
		
		if (stopMillisec>0) {
			for (int employeeID=1;employeeID<=numEmployees;employeeID++) {
				String currentKey = keyName+employeeID;
				createKeyPair(currentKey,ec2client,keyPath);
				System.out.println("Created public/private key pair:"+keyPath+currentKey+".pem");
				String currentInstance = instanceName+employeeID;
				createEC2Instance(logFilePath+logFilename,currentKey,groupName,imageId,instanceType,currentInstance,ec2client,secretText);
			}
			// upload file to bucket and set it to Private
			s3Client.putObject(new PutObjectRequest(bucketName, logFilename, 
					new File(logFilePath+logFilename)).withCannedAcl(CannedAccessControlList.Private));
			while (stopMillisec>0) {
				if (stopMillisec<10000) {
					Thread.sleep(stopMillisec);
					stopMillisec = 0;
				} else {
					Thread.sleep(10000);
					stopMillisec-=10000;
				}
				long stopSec = stopMillisec/1000;
				System.out.println(stopSec+" seconds remaining till workday ends and instances are stopped");
			}
			for (int employeeID=1;employeeID<=numEmployees;employeeID++) {
				String currentInstance = instanceName+employeeID;
				String instanceID = getInstanceID(currentInstance,ec2client,"running");
				StopInstancesRequest sir = new StopInstancesRequest();
				sir.withInstanceIds(instanceID);
				ec2client.stopInstances(sir);
				waitForInstance(logFilePath+logFilename,currentInstance,instanceID,ec2client,"stopped",secretText);
			}
			// upload file to bucket and set it to Private
			s3Client.putObject(new PutObjectRequest(bucketName, logFilename, 
					new File(logFilePath+logFilename)).withCannedAcl(CannedAccessControlList.Private));
		} else {
			System.out.println("Stop time is prior to current time so no instances will be instantiated");
		}
	}

	private static String createBucket(Scanner scanner, AmazonS3 s3Client) {
		try {
		System.out.print("Bucket name:");
		String bucketName = scanner.nextLine();
		for (Bucket bucket : s3Client.listBuckets()) {
			if (bucket.getName().equals(bucketName)) {
				System.out.println("Bucket "+bucketName+" found");
				return bucketName;
			}
		}
		s3Client.createBucket(bucketName);
		System.out.println("Created bucket:"+bucketName);
		return bucketName;
		}  catch (Exception e) {
			System.out.println(e.getMessage());
			return createBucket(scanner,s3Client);
		}
	}

	private static int getNumEmployees(Scanner scanner) {
		int numEmployees = -1;
		while (numEmployees<1||3<numEmployees) {
			try {
			System.out.print("Number of employees (1-3):");  
			String numEmployeesStr = scanner.nextLine(); // Get what the user types.
			numEmployees = Integer.parseInt(numEmployeesStr);
			} catch (Exception e) {
				System.out.println("Message:"+e.getMessage());
			}
		}
		return numEmployees;
	}

	private static void getWorkDayTimes(Scanner scanner,SimpleDateFormat sdf, Calendar startTime, Calendar stopTime) {
		try {
			System.out.print("Start of workday (hh:mmaa):");
			String workDayStart = scanner.nextLine();
			startTime.setTime(sdf.parse(workDayStart));
			System.out.print("End of workday (hh:mmaa):");
			String workDayEnd = scanner.nextLine();
			stopTime.setTime(sdf.parse(workDayEnd));
			Calendar currentTime = Calendar.getInstance();
			startTime.set(Calendar.YEAR,currentTime.get(Calendar.YEAR));
			startTime.set(Calendar.MONTH,currentTime.get(Calendar.MONTH));
			startTime.set(Calendar.DAY_OF_MONTH,currentTime.get(Calendar.DAY_OF_MONTH));
			stopTime.set(Calendar.YEAR,currentTime.get(Calendar.YEAR));
			stopTime.set(Calendar.MONTH,currentTime.get(Calendar.MONTH));
			stopTime.set(Calendar.DAY_OF_MONTH,currentTime.get(Calendar.DAY_OF_MONTH));
			if (stopTime.before(startTime)) {
				System.out.println(sdf.format(startTime.getTime())+" cannot be after "+sdf.format(stopTime.getTime()));
				getWorkDayTimes(scanner,sdf,startTime,stopTime);
			}
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			System.out.println("Example: 11:00AM or 3:00PM");
			getWorkDayTimes(scanner,sdf,startTime,stopTime);
		}
	}
	
	private static void createEC2Instance(String logFilename, String keyName, String groupName, String imageID, String instanceType, String instanceName, AmazonEC2Client ec2client, String secretText){
		try {
			String instanceID = getInstanceID(instanceName,ec2client,"stopped");
			if (instanceID!=null) {
				StartInstancesRequest stir = new StartInstancesRequest();
				stir.withInstanceIds(instanceID);
				ec2client.startInstances(stir);
				waitForInstance(logFilename,instanceName,instanceID,ec2client,"running",secretText);
			} else { 
				// request for new on demand instance
				RunInstancesRequest rir = new RunInstancesRequest();
				rir.withImageId(imageID);
				rir.withInstanceType(instanceType);
				rir.withMinCount(1);
				rir.withMaxCount(1);
				rir.withKeyName(keyName);
				rir.withMonitoring(true);
				rir.withSecurityGroups(groupName);
				System.out.println("Creating instance "+instanceName);
				ec2client.runInstances(rir);
				DescribeInstancesResult result = ec2client.describeInstances();
				Iterator<Reservation> i = result.getReservations().iterator();
				while (i.hasNext()) {
					Reservation r = i.next();
					List<Instance> instances = r.getInstances();
					for (Instance ii : instances) {
						//System.out.println(ii.getImageId() + "t" + ii.getInstanceId()+ "t" + ii.getState().getName() + "t"+ ii.getPrivateDnsName());
						if (ii.getState().getName().equals("pending")) {
							instanceID = ii.getInstanceId();
						}
					}
				 }
				waitForInstance(logFilename,instanceName,instanceID,ec2client,"running",secretText);
				/// Creating Tag for New Instance ////
				CreateTagsRequest crt = new CreateTagsRequest();
				ArrayList<Tag> arrTag = new ArrayList<Tag>();
				arrTag.add(new Tag().withKey("Name").withValue(instanceName));
				crt.setTags(arrTag);
				
				ArrayList<String> arrInstances = new ArrayList<String>();
				arrInstances.add(instanceID);
				crt.setResources(arrInstances);
				System.out.println("Creating Tags for New Instance:"+instanceName);
				ec2client.createTags(crt);		
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	private static void createEC2SecurityGroup(String groupName, String groupDescription, AmazonEC2Client ec2client){
		String sshIpRange = "0.0.0.0/0";
		String sshprotocol = "tcp";
		int sshFromPort = 22;
		int sshToPort =22;
		
		String httpIpRange = "0.0.0.0/0";
		String httpProtocol = "tcp";
		int httpFromPort = 80;
		int httpToPort = 80;
		
		String httpsIpRange = "0.0.0.0/0";
		String httpsProtocol = "tcp";
		int httpsFromPort = 443;
		int httpsToProtocol = 443;
		try {
			DescribeSecurityGroupsRequest describeSecurityGroupsRequest = new DescribeSecurityGroupsRequest();
			describeSecurityGroupsRequest.withGroupNames(groupName);
			try {
				System.out.println("Querying for Pre-existing Security Group Request:"+groupName);
				DescribeSecurityGroupsResult dsgr = ec2client.describeSecurityGroups(describeSecurityGroupsRequest);
				List<SecurityGroup> securityGrpList = dsgr.getSecurityGroups();
				System.out.println("Number of Security Groups:"+securityGrpList.size());
				if (securityGrpList.size()>0){
					return;
				} 
			} catch (Exception e){}
			CreateSecurityGroupRequest createSecurityGroupRequest =  new CreateSecurityGroupRequest();
			createSecurityGroupRequest.withGroupName(groupName).withDescription(groupDescription);
			CreateSecurityGroupResult csgr = ec2client.createSecurityGroup(createSecurityGroupRequest);
			System.out.println("Created "+groupName+" with Security Group Id: "+csgr.getGroupId());
			
			System.out.println("Setting Security Group Permission");
			Collection<IpPermission> ips = new ArrayList<IpPermission>();
			// Permission for SSH only to your ip
			IpPermission ipssh = new IpPermission();
			ipssh.withIpRanges(sshIpRange).withIpProtocol(sshprotocol).withFromPort(sshFromPort).withToPort(sshToPort);
			ips.add(ipssh);
				
			// Permission for HTTP, any one can access
			IpPermission iphttp = new IpPermission();
			iphttp.withIpRanges(httpIpRange).withIpProtocol(httpProtocol).withFromPort(httpFromPort).withToPort(httpToPort);
			ips.add(iphttp);
				
			//Permission for HTTPS, any one can access
			IpPermission iphttps = new IpPermission();
			iphttps.withIpRanges(httpsIpRange).withIpProtocol(httpsProtocol).withFromPort(httpsFromPort).withToPort(httpsToProtocol);
			ips.add(iphttps);
				
			System.out.println("Attach Owner to security group");
			// Register this security group with owner
			AuthorizeSecurityGroupIngressRequest authorizeSecurityGroupIngressRequest =	new AuthorizeSecurityGroupIngressRequest();
			authorizeSecurityGroupIngressRequest.withGroupName(groupName).withIpPermissions(ips);
			ec2client.authorizeSecurityGroupIngress(authorizeSecurityGroupIngressRequest);		
		
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	 private static void createKeyPair(String keyName, AmazonEC2Client ec2client, String pemFilePath){
		  try {
			  String pemFileName = keyName;
			  DeleteKeyPairRequest deleteKeyPairRequest = new DeleteKeyPairRequest();
			  deleteKeyPairRequest.setKeyName(keyName);
			  ec2client.deleteKeyPair(deleteKeyPairRequest);
		   CreateKeyPairRequest ckpr = new CreateKeyPairRequest();
		   ckpr.withKeyName(keyName);
		   CreateKeyPairResult ckpresult = ec2client.createKeyPair(ckpr);
		   KeyPair keypair = ckpresult.getKeyPair();
		   String privateKey = keypair.getKeyMaterial();
		   writePemFile(privateKey,pemFilePath,pemFileName); 
		  } catch (Exception e) {
		   e.printStackTrace();
		   System.exit(0);
		  }
		 }

	 public static String getInstanceID(String instanceName,AmazonEC2Client ec2client,String state){
			List<TagDescription> lstTags= ec2client.describeTags().getTags();
			for (TagDescription td : lstTags) {
				if(td.getValue().equals(instanceName)){
					String instanceId = td.getResourceId();
					List<Reservation> lstReservations = ec2client.describeInstances().getReservations();
					for (Reservation rr : lstReservations) {
						for(Instance ii : rr.getInstances()){
							if (ii.getState().getName().equals(state) && ii.getInstanceId().equals(instanceId)) {
								return instanceId;
							}
						}
					}
				}
			}
			return null;
	 }
	 
	 private static void writePemFile(String privateKey,String pemFilePath,String keyname){
			try {
				PrintWriter writer = new PrintWriter(pemFilePath + "/" + keyname + ".pem", "UTF-8");
				writer.print(privateKey);
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	 
	 private static void waitForInstance(String logFilename, String instanceName, String instanceID, AmazonEC2Client ec2client, String state, String secretText) throws InterruptedException {
		 boolean isWaiting = true;
		 while (isWaiting) {
			 Calendar currentTime = Calendar.getInstance();
			 System.out.println((new SimpleDateFormat("yyyy MMM-dd hh:mm:ss")).format(currentTime.getTime())+"-"+instanceName+"("+instanceID+") transitioning to "+state);
			 Thread.sleep(2000);
			 DescribeInstancesResult r = ec2client.describeInstances();
			 Iterator<Reservation> ir = r.getReservations().iterator();
			 while(ir.hasNext()){
				 Reservation rr = ir.next();
				 List<Instance> instances = rr.getInstances();
				 for(Instance ii : instances){
					 if (ii.getState().getName().equals(state) && ii.getInstanceId().equals(instanceID) ) {
						 System.out.println((new SimpleDateFormat("yyyy MMM-dd hh:mm:ss")).format(currentTime.getTime())+"-"+instanceName+"\t("+ii.getInstanceId()+ ")\t" +ii.getImageId() + "\t" + ii.getState().getName() + "\t"+ ii.getPrivateDnsName()+"\t"+ ii.getPublicDnsName());
						 String lineEntry = instanceID+"/"+instanceName+","+(new SimpleDateFormat("hh:mm:ss")).format(currentTime.getTime())+","+ii.getState().getName()+".";
						 if (ii.getState().getName().equals("running")) {
							 lineEntry = instanceID+"/"+instanceName+","+(new SimpleDateFormat("hh:mm:ss")).format(currentTime.getTime())+", started. the secret is "+secretText;
					 	 } 
						 logFile(logFilename,lineEntry);
						 isWaiting=false;
						 break;
					 }
				 }
			 }
		 }
	 }
	 
	 private static void logFile(String fileName, String newLine) {
		 try(FileWriter fw = new FileWriter(fileName, true);
				    BufferedWriter bw = new BufferedWriter(fw);
				    PrintWriter out = new PrintWriter(bw))
		{
			 out.println(newLine);
			 out.close();bw.close();fw.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	 }
	 
	 public static String byteBufferToString(ByteBuffer buffer, Charset charset) {
	        byte[] bytes;
	        if (buffer.hasArray()) {
	            bytes = buffer.array();
	        } else {
	            bytes = new byte[buffer.remaining()];
	            buffer.get(bytes);
	        }
	        return new String(bytes, charset);
	    }
	 
	 private static String getRole(AmazonIdentityManagement iam, String roleName) {
		 try {
			 GetRoleResult getRoleResult = iam.getRole(new GetRoleRequest()
				 									.withRoleName(roleName));
			 return getRoleResult.getRole().getArn();
		 } catch (Exception e) {
			 System.out.println(e.getMessage());
			 return CreateRole(iam,roleName).getArn();
		 }
	 }
	 
	 private static Role CreateRole(AmazonIdentityManagement iam, String roleName){
		 CreateRoleResult result = iam.createRole(new CreateRoleRequest()				
				 					.withRoleName(roleName)
				 					.withAssumeRolePolicyDocument(ASSUME_ROLE_POLICY));
		 String policyArn = iam.createPolicy(
				 				new CreatePolicyRequest()
				 				.withPolicyName(getRandomPolicyName())
				 				.withPolicyDocument(BASIC_ROLE_POLICY)
				 				).getPolicy().getArn();
		 iam.attachRolePolicy(new AttachRolePolicyRequest()
				 				.withRoleName(roleName)
				 				.withPolicyArn(policyArn));
		 try {
			 Thread.sleep(10*1000);
		 } catch (InterruptedException e) {
			 Thread.currentThread().interrupt();
		 }
		 return result.getRole();
	 }	
	 
	 private static String getRandomPolicyName() {
		 	return "lambdaRolePolicy_"+UUID.randomUUID().toString();
	 }
	 
	 private static CreateFunctionRequest makeFunctionRequest(AmazonIdentityManagement iam,String bucketName, String jarInBucket, String lambdaFuncName, String javaClassName) {
		 FunctionCode functionCode = new FunctionCode();
		 functionCode.setS3Bucket(bucketName);
		 functionCode.setS3Key(jarInBucket);
		 CreateFunctionRequest createFunctionRequest = new CreateFunctionRequest();
		 createFunctionRequest.setCode(functionCode);
		 createFunctionRequest.setDescription(lambdaFuncName+" is what it is");
		 createFunctionRequest.setRole(getRole(iam,lambdaFuncName+"Role"));
		 createFunctionRequest.setHandler(javaClassName);
		 createFunctionRequest.setRuntime("java8");
		 createFunctionRequest.setFunctionName(lambdaFuncName);
		 return createFunctionRequest;
	 }
	 
	 private static void invokeLambdaFunction(AWSLambdaClient lambda, long startMillisec, long stopMillisec, int numEmployees, String bucketName,String secretText) {
			try {
	            InvokeRequest invokeRequest = new InvokeRequest();
	            invokeRequest.setFunctionName("InstanceScheduler");
	            String payLoad = "\""+startMillisec+":"+stopMillisec+":"+numEmployees+":"+bucketName+":"+secretText+"\"";
	            invokeRequest.setPayload(payLoad);
	            System.out.println(byteBufferToString(
	                    lambda.invoke(invokeRequest).getPayload(),
	                    Charset.forName("UTF-8")));
		    } catch (Exception e) {
		            System.out.println(e.getMessage());
		    }
	 }
	 
	 private static void uploadJarFileToBucket(AmazonS3 s3Client,String jarFilePath,String jarFileName,String bucketName) {
		 	s3Client.putObject(bucketName,jarFileName,new File(jarFilePath+jarFileName));
	 }
	 
	 private static void deployLambdaFunction(AmazonIdentityManagement iam, AWSLambdaClient awsLambdaClient, String lambdaFuncName, String bucketName, String jarInBucket, String javaClassName){
		 try {
		 DeleteFunctionRequest deleteRequest = new DeleteFunctionRequest();
		 deleteRequest = deleteRequest.withFunctionName(lambdaFuncName);
		 awsLambdaClient.deleteFunction(deleteRequest);
		 } catch (Exception e) {
			 System.out.println(e.getMessage());
		 }
		 awsLambdaClient.createFunction(makeFunctionRequest(iam,bucketName,jarInBucket,lambdaFuncName,javaClassName));
	 }
	 
	 private static final String BASIC_ROLE_POLICY = 
	            "{" + 
	                "\"Version\": \"2012-10-17\"," + 
	                "\"Statement\": [" + 
	                    "{" + 
	                        "\"Effect\": \"Allow\"," + 
	                        "\"Action\": [" + 
	                            "\"logs:*\"" + 
	                        "]," + 
	                        "\"Resource\": \"arn:aws:logs:*:*:*\"" + 
	                    "}" + 
	                "]" + 
	            "}"; 
	 
	    private static final String ASSUME_ROLE_POLICY = 
	            "{" + 
	                "\"Version\": \"2012-10-17\"," + 
	                "\"Statement\": [" + 
	                    "{" + 
	                        "\"Sid\": \"\"," + 
	                        "\"Effect\": \"Allow\"," + 
	                        "\"Principal\": {" + 
	                            "\"Service\": \"lambda.amazonaws.com\"" + 
	                        "}," + 
	                        "\"Action\": \"sts:AssumeRole\"" + 
	                    "}" + 
	                "]" + 
	            "}";
}
