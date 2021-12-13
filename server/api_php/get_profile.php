<?php

//import jwtConnect class
require_once __DIR__.'/jwt_connect.php';

//Store id of the user who wants to get projects
$input=file_get_contents("php://input");
$data=json_decode($input);
$jwt=$data->jwt;
$user_id=$data->user_id;

//Connecting to database and get the PDO connector
$dbConnect = new jwtConnect($jwt);
$db = $dbConnect->getDB();
$user = $dbConnect->getID();
$authorization = $dbConnect->isDev();

$response = array();

//Executing the SQL query
$query = $db->prepare('SELECT firstname, lastname, profile_picture, location
                       FROM User WHERE id=:user_id
                      ');

$query->bindParam(':user_id',$user_id);

try
{
  $query->execute();

  if ( $query->rowCount() != 0 )
  {
    /*Retrieving data result in a multidimensional array
    containing arrays indexed by column names*/
    $result=$query->fetch(PDO::FETCH_ASSOC);

    $response['profile']=$result;
    $response['success']=true;
  }
  else
  {
    //Put a failure message in the response with the key 'message'
    $response['message']='Cet utilisateur n\'existe pas';
    $response['success']=false;
  }

} catch (Exception $e)
{
  $response['message']='La vérification de l\'utilisateur n\'a pas pu aboutir';
  $response['success']=false;
}

//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
