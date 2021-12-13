<?php

//import jwtConnect class
require_once __DIR__.'/jwt_connect.php';

//Store id of the user who wants to get projects
$input=file_get_contents("php://input");
$data=json_decode($input);
$jwt=$data->jwt;
$project_id=$data->project;

//Connecting to database and get the PDO connector
$dbConnect = new jwtConnect($jwt);
$db = $dbConnect->getDB();
$user_id = $dbConnect->getID();
$authorization = $dbConnect->isDev();

$response = array();

//Executing the add like query
$query = $db->prepare('DELETE FROM User_like
                       WHERE user_id=:user_id
                       AND project_id=:project_id
                     ');

$query->bindParam(':user_id',$user_id);
$query->bindParam(':project_id',$project_id);


try
{
  $query->execute();

  //Executing the set number of like query
  $query = $db->prepare('UPDATE Project
                         SET likes= (SELECT COUNT(*)
                         FROM User_like
                         WHERE project_id=:project_id
                         AND opinion = 1)
                         WHERE id=:project_id
                       ');

  $query->bindParam(':project_id',$project_id);

  try
  {
    $query->execute();
    $response['success']=true;
  } catch (Exception $e)
  {
    $response['success']=false;
    $response['message']='une erreur est survenue lors de la mise à jour du nombre de likes';
  }
} catch(Exception $e)
{
  $response['success']=false;
  $response['message']='vous n\'aimez déjà pas ce projet';
}

//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
