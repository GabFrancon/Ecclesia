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
$user_id = $dbConnect->getID();
$authorization = $dbConnect->isDev();

$response = array();

//Executing the SQL query
$query = $db->prepare('SELECT firstname, lastname, birth, gender, email, profile_picture, location
                       FROM User WHERE id=:user_id
                      ');

$query->bindParam(':user_id',$user_id);

try
{
  $query->execute();

  if ( $query->rowCount() != 0 )
  {
    //Retrieving profile in an array indexed by column names
    $result=$query->fetch(PDO::FETCH_ASSOC);

    $response['user']=$result;
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
