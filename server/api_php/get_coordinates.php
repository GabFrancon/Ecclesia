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


$query = $db->prepare('SELECT Project.id, title, summary, picture, website, likes, meeting_date, end_date, meeting_time, place, lat, lng
                       FROM Project INNER JOIN Meeting
                       ON Project.id=Meeting.project_id
                       WHERE Project.id NOT IN
                      (SELECT project_id FROM User_like
                      WHERE user_id=:user AND opinion=0)
                      AND lat IS NOT NULL AND lng IS NOT NULL
                      GROUP BY Project.id, meeting_date, end_date, meeting_time, place, lat, lng
                      ');

$query->bindParam(':user',$user);

try
{
  $query->execute();
  $response['projects']=$query->fetchAll(PDO::FETCH_ASSOC);
  $response['success']=true;
}catch (Exception $e){
  $response['success']=false;
  $response['message']='une erreur technique est survenue';
}

//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
