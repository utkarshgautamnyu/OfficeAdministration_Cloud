# OfficeAdministration_Cloud
This is an Office Administration Web Application, hosted on the AWS. The frontend communicates with the backend database stored on the AWS' S3 Buckets using REST API calls. 

Following are the functions of the application:
<ol>
<li>Take the following inputs:</li>
<ul>
<li>Employee ID</li>
<li>Start of Work day </li>
<li> End of Work day </li>
<li> S3 Bucket Name </li>
</ul>
<li> Intialize the Office Environment with an EC2 instance per employee </li>
<li> Create and allocate a private shared S3 bucket, accessible by all the instances created above </li>
<li> Start and Stop the EC2 instances by comparing the present time and the Start and End of work day entered earlier </li>



