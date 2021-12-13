<?php

//import jwtConnect class
require_once __DIR__.'/jwt_connect.php';

//Store id of the user who wants to get projects
$input=file_get_contents("php://input");
$data=json_decode($input);
$jwt=$data->jwt;
$applicant_id=$data->applicant_id;

//Connecting to database and get the PDO connector
$dbConnect = new jwtConnect($jwt);
$db = $dbConnect->getDB();
$acceptor_id = $dbConnect->getID();
$authorization = $dbConnect->isDev();

$response = array();

//Executing the SQL query
$query = $db->prepare('UPDATE Friendship
                      SET friend_request_accepted=1
                      WHERE applicant=:applicant_id
                      AND acceptor=:acceptor_id
                      ');

$query->bindParam(':applicant_id',$applicant_id);
$query->bindParam(':acceptor_id',$acceptor_id);

try
{
  $query->execute();
  $response['success']=true;
} catch (Exception $e)
{
  $response['success']=false;
  $response['message']='une erreur est survenue lors de la validation de la demande d\'ami';
}

//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
