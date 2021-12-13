<?php

//import jwtConnect class
require_once __DIR__.'/jwt_connect.php';

//Store id of the user who wants to get projects
$input=file_get_contents("php://input");
$data=json_decode($input);
$jwt=$data->jwt;
$project=$data->project;

//Connecting to database and get the PDO connector
$dbConnect = new jwtConnect($jwt);
$db = $dbConnect->getDB();
$user = $dbConnect->getID();
$authorization = $dbConnect->isDev();

$response = array();

//Executing the SQL query
$projectQuery = $db->prepare('SELECT likes FROM Project
                       WHERE id=:project');

$projectQuery->bindParam(':project',$project);

try
{
  $projectQuery->execute();
  $projectResult=$projectQuery->fetch(PDO::FETCH_ASSOC);

  //Put the data into the response
  $response['number_of_likes']=$projectResult['likes'];

  $userQuery = $db->prepare('SELECT * FROM User_like
                             WHERE project_id=:project
                             AND user_id=:user');

  $userQuery->bindParam(':project',$project);
  $userQuery->bindParam(':user',$user);

  try
  {
    $userQuery->execute();
    $userResult=$userQuery->fetch(PDO::FETCH_ASSOC);

    if ($userQuery->rowCount()==0)
    {
      $response['user_opinion']="none";
    }
    else
    {
      $opinion=$userResult['opinion'];

      if ($opinion==1) {$response['user_opinion']="like";}
      elseif ($opinion==0) {$response['user_opinion']="dislike";}
      else {
        $response['message']='erreur dans la table User_like';
        $response['success']=false;
      }
    }
    $response['success']=true;
  } catch (Exception $e)
  {
    $response['message']='erreur lors du chargement du like de l\'utilisateur';
    $response['success']=false;
  }
}
catch (Exception $e)
{
  $response['message']='erreur lors du chargement des likes';
  $response['success']=false;
}

//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
