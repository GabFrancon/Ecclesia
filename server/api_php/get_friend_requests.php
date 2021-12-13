<?php

//import DataBaseConnect class
require_once __DIR__.'/db_connect.php';

//Connecting to database and get the PDO connector
$dbConnect = new DatabaseConnect();
$db = $dbConnect->getDB();

//Store id of the user who wants to get his friends requests
$input=file_get_contents("php://input");
$data=json_decode($input);
$id=$data->id;

//Create response array that will be returned
$response = array();

//Executing the SQL query
$query = $db->prepare('SELECT id, firstname, lastname, profile_picture
                      FROM User WHERE id IN
                      (SELECT id FROM User INNER JOIN Friendship
                      ON User.id=Friendship.applicant
                      WHERE acceptor=:id
                      AND NOT friend_request_accepted)
                      ORDER BY firstname, lastname
                      ');

$query->bindParam(':id',$id);
$success=$query->execute();

if ( $query->rowCount()!=0 )
{
  /*Retrieving data result in a multidimensional array
  containing arrays indexed by column names*/
  $result=$query->fetchAll(PDO::FETCH_ASSOC);

  //Put the data into the response with the key 'projects'
  $response['friend_requests']=$result;
}
else
{
  //Put a failure message in the response with the key 'message'
  $response['message']='vous n\'avez pas de demande d\'ami';
}
//Add a success or failure tag in the response with the key 'success'
$response['success']=$success;

//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
