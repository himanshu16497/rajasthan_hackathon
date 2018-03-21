<?php

include 'config.php';

$usernamelogin = $_POST['usernamelogin'];

$password = $_POST['password'];
#$usernamelogin = 'gumber';
#$password = 'gumber';

if($conn) 
{
	$result['code_conn'] = 1;

	$code_conn = 1;

      //login using phone number
		
		
       $query1 = mysqli_query($conn, "SELECT UID from tblcare WHERE USERNAME = '$usernamelogin';");

	$row1 = mysqli_fetch_array($query1);
		
        $uid = $row1['UID'];

	if($uid)			
        {
	$query2 = mysqli_query($conn, "SELECT PASSWORD from tblcare WHERE UID = '$uid'");
		       
	$row2 = mysqli_fetch_array($query2);

	if($password == $row2['PASSWORD']) 
           { 
              //check password
	
	     $query3 = mysqli_query($conn, "SELECT NAME, USERNAME , LAT,LNG from tblcare WHERE USERNAME = '$usernamelogin'");

	     $row3 = mysqli_fetch_array($query3);
	
	$code_auth = 1;
			
	//$result['code_auth'] = 1; //if correct password
	
	$uname = $row3['USERNAME'];
	
	$name = $row3['NAME'];
	$lat = $row3['LAT'];
	$lng = $row3['LNG'];		
	$message = "Successful login";
		
	}
			
	else 
	{

		$code_auth = 0; 
				
		$message = "Incorrect password";//if incorrect password
			
	}

	}			//$code_db = 1; //if userdetails found
		
		
	else 
	{
	
		$code_auth = 0;
			//$code_db = 0; //if userdetails not found
			$message = "User not found";
		
	}
	
	
}
if($code_auth == 1) {
	$result = array(
	"flag" => 1,
	"uid" => $uid,
	"uname" => $name,
	"name" => $uname,
	"lat" => $lat,
	"lng" => $lng,
	"message" => $message
	);
}

else {
	$result = array(
	"flag" => 0,
	"message" => $message
	);
}

echo json_encode($result);

?>