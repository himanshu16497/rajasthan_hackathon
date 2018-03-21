<?PHP
$database='emergency';
$server='127.0.0.1';
$user='root';
$pass='';
$conn=mysqli_connect($server,$user,$pass,$database);
$sql="Select * from tblcare";
$result=mysqli_query($conn,$sql);
while($row=mysqli_fetch_assoc($result))
{
	$flag[]=$row;
}
print(json_encode($flag));
?>