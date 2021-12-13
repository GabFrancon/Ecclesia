<?php

//import DataBaseConnect class
require_once __DIR__.'/db_connect.php';

//Connecting to database and get the PDO connector
$dbConnect = new DatabaseConnect();
$db = $dbConnect->getDB();

//Store id of the user who wants to retrieve friend requests that have been made to him
$input=file_get_contents("php://input");
$data=json_decode($input);
$user_id=$data->user_id;

//Create response array that will be returned
$response = array();

//Executing the SQL query
$query = $db->prepare('SELECT id, firstname, lastname, profile_picture, time_stamping
                      FROM User INNER JOIN Friendship ON User.id=Friendship.applicant
                      WHERE acceptor=:user_id AND friend_request_accepted=0
                      ORDER BY time_stamping DESC
                      ');

$query->bindParam(':user_id',$user_id);

try
{
  $query->execute();
  $response['success']=true;

  if($query->rowCount()!=0)
  {
    /*Retrieving data result in a multidimensional array
    containing arrays indexed by column names*/
    $result=$query->fetchAll(PDO::FETCH_ASSOC);

    //Put the data into the response
    $response['friends']=$result;
  }
} catch (Exception $e)
{
  $response['success']=false;
  $response['message']='une erreur est survenue lors du chargement des demandes d\'ami';
}

//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
