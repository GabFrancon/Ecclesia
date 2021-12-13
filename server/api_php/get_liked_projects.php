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
$query = $db->prepare('SELECT Project.id, title, picture, summary, website, likes, meeting_date, end_date, meeting_time, place, lat, lng
                       FROM Project INNER JOIN (Meeting NATURAL JOIN User_like)
                       ON Project.id=Meeting.project_id
                       WHERE user_id=:user
                       GROUP BY Project.id, meeting_date, end_date, meeting_time, place, lat, lng
                       ORDER BY time_stamping DESC
                     ');

$query->bindParam(':user',$user);

try
{
  $query->execute();

  if ( $query->rowCount()!=0 )
  {
    $result=$query->fetchAll(PDO::FETCH_ASSOC);
    $response['projects']=$result;
    $response['success']=true;
  }
  else
  {
    $response['message']='Vous n\'avez aimé aucun projet pour le moment';
    $response['success']=false;
  }
} catch (Exception $e)
{
  $response['message']='Une erreure technique est survenue';
  $response['success']=false;
}

//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
