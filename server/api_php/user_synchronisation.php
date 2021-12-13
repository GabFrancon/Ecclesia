<?php

//import DataBaseConnect class
require_once __DIR__.'/db_connect.php';

//Connecting to database and get the PDO connector
$dbConnect = new DatabaseConnect();
$db = $dbConnect->getDB();

//Store id and facebook id from user who wants to synchronize
$input=file_get_contents("php://input");
$data=json_decode($input);
$id=$data->id;
$facebook_id=$data->facebook_id;


//Create response array that will be returned
$response = array();

//Preparing the SQL query
$query = $db->prepare('UPDATE User
                      SET facebook_id=:facebook_id
                      WHERE id=:id
                     ');

$query->bindParam(':id',$id);
$query->bindParam(':facebook_id',$facebook_id);

$success=$query->execute();
$response['success']=$success;

//return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
