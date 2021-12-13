<?php

//import jwtConnect class
require_once __DIR__.'/jwt_connect.php';

//Store id of the user who wants to get projects
$input=file_get_contents("php://input");
$data=json_decode($input);
$jwt=$data->jwt;
$search=$data->search;

//Connecting to database and get the PDO connector
$dbConnect = new jwtConnect($jwt);
$db = $dbConnect->getDB();
$user = $dbConnect->getID();
$authorization = $dbConnect->isDev();

$response = array();

$keyword =  $search . '*';

//Executing the SQL query
$query = $db->prepare('SELECT Project.id, title, picture, summary, website, likes, meeting_date, end_date, meeting_time, place, lat, lng
                       FROM Project INNER JOIN
                       (Meeting INNER JOIN
                         (Project_area NATURAL JOIN Project_category)
                       ON Meeting.project_id=Project_area.project)
                       ON Project.id=Meeting.project_id
                       WHERE MATCH (title)
                       AGAINST (:keyword IN BOOLEAN MODE)
                       OR area IN (SELECT id FROM Area
                         WHERE MATCH (name) AGAINST (:keyword IN BOOLEAN MODE))
                       OR category IN (SELECT id FROM Category
                         WHERE MATCH (name) AGAINST (:keyword IN BOOLEAN MODE))
                       GROUP BY Project.id, meeting_date, end_date, meeting_time, place, lat, lng
                       ORDER BY likes DESC
                      ');

$query->bindParam(':keyword',$keyword);

try
{
  $query->execute();

  if ( $query->rowCount()!=0 )
  {
    /*Retrieving data result in a multidimensional array
    containing arrays indexed by column names*/
    $result=$query->fetchAll(PDO::FETCH_ASSOC);

    //Put the data into the response
    $response['projects']=$result;
    $response['success']=true;
  }
  else
  {
    //Put a failure message in the response with the key 'message'
    $response['message']='aucun résultat';
    $response['success']=false;
  }
} catch (Exception $e)
{
  $response['message']='une erreur est survenue lors de la recherche';
  $response['success']=false;
}

//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
