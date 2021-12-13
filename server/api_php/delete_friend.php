<?php

//import jwtConnect class
require_once __DIR__.'/jwt_connect.php';

//Store id of the user who wants to get projects
$input=file_get_contents("php://input");
$data=json_decode($input);
$jwt=$data->jwt;
$user_id_2=$data->user_id_2;

//Connecting to database and get the PDO connector
$dbConnect = new jwtConnect($jwt);
$db = $dbConnect->getDB();
$user_id_1 = $dbConnect->getID();
$authorization = $dbConnect->isDev();

$response = array();

//Executing the SQL query
$query = $db->prepare('DELETE FROM Friendship
                      WHERE (applicant=:user_id_1 AND acceptor=:user_id_2)
                      OR (applicant=:user_id_2 AND acceptor=:user_id_1)
                    ');

$query->bindParam(':user_id_1',$user_id_1);
$query->bindParam(':user_id_2',$user_id_2);

try {
  $query->execute();
  $response['success']=true;
} catch (Exception $e)
{
  $response['success']=false;
  $response['message']='une erreur est survenue lors de la suppression';
}

//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
