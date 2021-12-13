<?php

//import DataBaseConnect class
require_once __DIR__.'/jwt_connect.php';

//Store id of the user who wants to get projects
$input=file_get_contents("php://input");
$data=json_decode($input);
$jwt=$data->jwt;

//Connecting to database and get the PDO connector
$dbConnect = new jwtConnect($jwt);
$db = $dbConnect->getDB();
$user = $dbConnect->getID();
$authorization = $dbConnect->isDev();

$response = array ('user'=>$user, 'dev'=>$authorization);

echo json_encode($response);


?>
