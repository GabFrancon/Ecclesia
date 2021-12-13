<?php

//import jwtConnect class
require_once __DIR__.'/jwt_connect.php';

//Store id of the user who wants to get projects
$input=file_get_contents("php://input");
$data=json_decode($input);
$jwt=$data->jwt;
$project_id=$data->project;
$opinion=$data->like;

//Connecting to database and get the PDO connector
$dbConnect = new jwtConnect($jwt);
$db = $dbConnect->getDB();
$user_id = $dbConnect->getID();
$authorization = $dbConnect->isDev();

$response = array();

$update_user_like=false;
$update_project=false;

//Executing the add like query
$query = $db->prepare('INSERT INTO User_like (user_id, project_id,opinion)
                       VALUES (:user_id,:project_id,:opinion)
                     ');

$query->bindParam(':user_id',$user_id);
$query->bindParam(':project_id',$project_id);
$query->bindParam(':opinion',$opinion);

try
{
  $query->execute();
  $update_user_like=true;
}
catch (Exception $e)
{
  $query = $db->prepare('UPDATE User_like
                        SET opinion=:opinion
                        WHERE (user_id=:user_id
                        AND project_id=:project_id)
                       ');

  $query->bindParam(':user_id',$user_id);
  $query->bindParam(':project_id',$project_id);
  $query->bindParam(':opinion',$opinion);

  try
  {
      $query->execute();
      $update_user_like=true;
  }
  catch (Exception $e)
  {
    $response['message']='une erreur est survenue lors de l\'éxecution';
  }
}

//Executing the set number of like query
$query = $db->prepare('UPDATE Project
                       SET likes = (SELECT COUNT(*)
                       FROM User_like
                       WHERE project_id=:project_id
                       AND opinion = 1)
                       WHERE id=:project_id
                     ');

$query->bindParam(':project_id',$project_id);

try
{
  $query->execute();
  $update_project=true;

} catch (Exception $e)
{
  $response['message']='une erreur est survenue lors de la mise à jour du nombre de likes ';
}

$response['success']=$update_project && $update_user_like;


//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
