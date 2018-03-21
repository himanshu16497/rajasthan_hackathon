<?PHP

include 'config.php';
echo $_POST['lat'];
echo $_POST['lon'];
echo $_POST['uid'];
$a=$_POST['uid'];
$b=$_POST['lat'];
$c=$_POST['lon'];
$bit1=0;
do
	
{   $uid=mt_rand(100,9999);
		
$result4=mysqli_query($conn,"SELECT * from tblaccident where UID='$uid'");
       
 $no=mysqli_num_rows($result4);	
	
}while($no!=0);

$result=mysqli_query($conn,"insert into tblaccident (UID,USER_ID,LAT,LNG) values ('$uid','$a','$b','$c')");
	
if($result){$bit1=1;}

?>