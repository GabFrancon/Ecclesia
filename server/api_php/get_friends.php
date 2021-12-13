<?php

//import jwtConnect class
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

$response = array();

//Executing the SQL query
$query = $db->prepare('SELECT id, firstname, lastname, profile_picture
                      FROM User WHERE id IN
                      (SELECT applicant FROM Friendship
                      WHERE acceptor=:user
                      AND friend_request_accepted=1)
                      OR id IN
                      (SELECT acceptor FROM Friendship
                      WHERE applicant=:user
                      AND friend_request_accepted=1)
                      ORDER BY firstname, lastname
                      ');

$query->bindParam(':user',$user);

try
{
  $query->execute();

  if ( $query->rowCount()!=0 )
  {
    /*Retrieving data result in a multidimensional array
    containing arrays indexed by column names*/
    $result=$query->fetchAll(PDO::FETCH_ASSOC);

    //Put the data into the response
    $response['friends']=$result;
    $response['success']=true;
  }
  else
  {
    //Put a failure message in the response with the key 'message'
    $response['message']='vous n\'avez pas encore d\'ami';
    $response['success']=false;
  }
} catch (Exception $e)
{
  $response['message']='vous n\'avez pas encore d\'ami';
  $response['success']=false;
}

//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
