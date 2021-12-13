<?php
//import jwtConnect class
require_once __DIR__.'/jwt_connect.php';

//Store id of the user who wants to get projects
$input=file_get_contents("php://input");
$data=json_decode($input);
$jwt=$data->jwt;
$project_id=$data->project;

//Connecting to database and get the PDO connector
$dbConnect = new jwtConnect($jwt);
$db = $dbConnect->getDB();
$user_id = $dbConnect->getID();
$authorization = $dbConnect->isDev();

$response = array();

//Executing the query
$query = $db->prepare('DELETE FROM User_pref_project
                       WHERE user_id=:user_id
                       AND project_id=:project_id
                     ');

$query->bindParam(':user_id',$user_id);
$query->bindParam(':project_id',$project_id);

try
{
  $query->execute();
  $response['success']=true;
}
catch (Exception $e)
{
  $resonse['success']=false;
  $response['message']='Le projet n\'a pas pu être ajouté aux favoris';
}

//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
