<?php

//import jwtConnect class
require_once __DIR__.'/jwt_connect.php';

//Store id of the user who wants to get projects
$input=file_get_contents("php://input");
$data=json_decode($input);
$jwt=$data->jwt;
$area=$data->area;
$project=$data->project;

//Connecting to database and get the PDO connector
$dbConnect = new jwtConnect($jwt);
$db = $dbConnect->getDB();
$user = $dbConnect->getID();
$authorization = $dbConnect->isDev();

$response = array();

//Executing the SQL query
$query = $db->prepare('SELECT Project.id, title, summary, picture, website, meeting_date, end_date, meeting_time, place, likes, lat, lng
                       FROM Project INNER JOIN
                       (Meeting INNER JOIN Project_area ON Meeting.project_id=Project_area.project)
                       ON Project.id=Meeting.project_id
                       WHERE Project.id NOT IN
                      (SELECT project_id FROM User_like
                       WHERE user_id=:user AND opinion=0)
                       AND Project.id !=:project
                       AND area=(SELECT id FROM Area WHERE name=:area)
                       GROUP BY Project.id, meeting_date, end_date, meeting_time, place, lat, lng
                       ORDER BY RAND()
                       LIMIT 3
                      ');

$query->bindParam(':user',$user);
$query->bindParam(':area',$area);
$query->bindParam(':project',$project);
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
    $response['message']='aucun projet trouvé';
    $response['success']=false;
  }
} catch (Exception $e)
{
  $response['message']='une erreur est survenue lors du chargement de projets';
  $response['success']=false;
}

//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
