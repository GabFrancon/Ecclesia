<?php

//import DataBaseConnect class
require_once __DIR__.'/db_connect.php';

//Connecting to database and get the PDO connector
$dbConnect = new DatabaseConnect();
$db = $dbConnect->getDB();

//Create response array that will be returned
$response = array();

//Store id of the user who wants to get projects
$input=file_get_contents("php://input");
$data=json_decode($input);


//Executing the SQL query
$query = $db->prepare('SELECT Project.id, title, summary, picture, website, meeting_date, end_date, meeting_time, place, likes, lat, lng
                       FROM Project INNER JOIN Meeting
                       ON Project.id=Meeting.project_id
                       WHERE meeting_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 1 WEEK)
                       OR end_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 1 WEEK)
                       GROUP BY Project.id, meeting_date, end_date, meeting_time, place, lat, lng
                       ORDER BY RAND();
                      ');

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
