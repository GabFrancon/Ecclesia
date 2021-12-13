<?php

//import DataBaseConnect class
require_once __DIR__.'/db_connect.php';

//Connecting to database and get the PDO connector
$dbConnect = new DatabaseConnect();
$db = $dbConnect->getDB();

//Create response array that will bu returned
$response = array();


//Executing the SQL query
$query = $db->prepare('SELECT id, title, summary FROM Project
                      WHERE id NOT IN
                      (SELECT project FROM Project_area)
                      AND id NOT IN
                      (SELECT project FROM Project_category)
                      ');

$success=$query->execute();

if ( $query->rowCount()!=0 )
{
  /*Retrieving data result in a multidimensional array
  containing arrays indexed by column names*/
  $result=$query->fetchAll(PDO::FETCH_ASSOC);

  //Put the data into the response with the key 'projects'
  $response['projects']=$result;
}
else
{
  //Put a failure message in the response with the key 'message'
  $response['message']='No project found';
}
//Add a success or failure tag in the response with the key 'success'
$response['success']=$success;

//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
