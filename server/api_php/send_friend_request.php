<?php

//import jwtConnect class
require_once __DIR__.'/jwt_connect.php';

//Store id of the user who wants to get projects
$input=file_get_contents("php://input");
$data=json_decode($input);
$jwt=$data->jwt;
$acceptor_id=$data->acceptor_id;

//Connecting to database and get the PDO connector
$dbConnect = new jwtConnect($jwt);
$db = $dbConnect->getDB();
$applicant_id = $dbConnect->getID();
$authorization = $dbConnect->isDev();

$response = array();

//Executing the SQL query
$query = $db->prepare('INSERT INTO Friendship (applicant, acceptor, friend_request_accepted)
                       VALUES(:applicant_id,:acceptor_id,0)
                     ');

$query->bindParam(':applicant_id',$applicant_id);
$query->bindParam(':acceptor_id',$acceptor_id);

$success=$query->execute();

$response['success']=$success;

//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
