# OfficeAdministration_Cloud
This is an Office Administration Software, hosted on the AWS. The frontend communicates with the backend database stored on the AWS' S3 Buckets using REST API calls. 

Following are the features of the software:
<ol>
<li>The program takes the following inputs:</li>
<ul>
<li>Employee ID</li>
<li>Start of Work day </li>
<li> End of Work day </li>
<li> S3 Bucket Name </li>
</ul>
<li> The program intializes the Office Environment with an EC2 instance per employee </li>
<li> Next it creates and allocates a private shared S3 bucket, accessible by all the instances created above </li>
<li> Starts and Stops the EC2 instances by comparing the present time and the Start and End of work day entered earlier </li>



