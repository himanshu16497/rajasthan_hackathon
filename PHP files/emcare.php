<?php
include 'config.php';
$flag['code']=0;
$bit1=0;
if($conn)
{
	$uname=$_POST['uname'];
	$name=$_POST['name'];
	$lat=$_POST['lat'];
	$lng=$_POST['lng'];
$password=$_POST['password'];

	
	do
	{   $uid=mt_rand(100000,999999);
		$result4=mysqli_query($conn,"SELECT * from tblcare where UID='$uid'");
        $no=mysqli_num_rows($result4);	
	}while($no!=0);
    $result=mysqli_query($conn,"insert into tblcare values ('$uid','$uname','$name','$lat','$lng','$password')");
	if($result){$bit1=1;}
	if($bit1==1)
	{
		$flag['code']=1;
	}
	else{$flag['code']=0;}
}
print(json_encode($flag));

?>