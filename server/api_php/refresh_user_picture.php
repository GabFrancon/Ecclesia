<?php
//import jwtConnect class
require_once __DIR__.'/jwt_connect.php';

//Store id of the user who wants to get projects
$input=file_get_contents("php://input");
$data=json_decode($input);
$jwt=$data->jwt;
$picture=$data->picture;

//Connecting to database and get the PDO connector
$dbConnect = new jwtConnect($jwt);
$db = $dbConnect->getDB();
$user = $dbConnect->getID();
$authorization = $dbConnect->isDev();

$response = array();

//Preparing the SQL query
$query = $db->prepare('UPDATE User
                      SET profile_picture=:picture
                      WHERE id=:user
                     ');

$query->bindParam(':picture',$picture);
$query->bindParam(':user',$user);

//Executing the query
try
{
  $query->execute();
  $response['success']=true;
} catch(Exception $e)
{
  $response['success']=false;
  $response['message']='Impossible de mettre à jour la photo de profile';
}
//return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
