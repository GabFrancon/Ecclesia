<?php

//import jwtConnect class
require_once __DIR__.'/jwt_connect.php';

//Store id of the user who wants to get projects
$input=file_get_contents("php://input");
$data=json_decode($input);
$jwt=$data->jwt;
$projects=$data->projects;

//Connecting to database and get the PDO connector
$dbConnect = new jwtConnect($jwt);
$db = $dbConnect->getDB();
$user = $dbConnect->getID();
$authorization = $dbConnect->isDev();

$response = array();
$classifications=array();
$success=true;

for ($i=0;$i<count($projects);$i++)
{
  $project_id=$projects[$i];
  $classification=array();

  //Executing the area query
  $areaQuery = $db->prepare('SELECT name
                             FROM Area INNER JOIN Project_area
                             ON Area.id=Project_area.area
                             WHERE project=:project
                          ');

  $areaQuery->bindParam(':project',$project_id);
  try
  {
    $areaQuery->execute();

    $areaList=array();
    $areaResult=$areaQuery->fetchAll(PDO::FETCH_ASSOC);

    $areaCount=count($areaResult);
    for($j=0;$j<$areaCount;$j++)
    {
      $area=$areaResult[$j];
      array_push($areaList, $area['name']);
    }
    $classification['areas']=$areaList;
  } catch (Exception $e) {$success=false;}

  //Executing the category query
  $categoryQuery = $db->prepare('SELECT name
                                 FROM Category INNER JOIN Project_category
                                 ON Category.id=Project_category.category
                                 WHERE project=:project_id
                               ');

  $categoryQuery->bindParam(':project_id',$project_id);
  try
  {
    $categoryQuery->execute();

    $categoryList=array();
    $categoryResult=$categoryQuery->fetchAll(PDO::FETCH_ASSOC);
    $categoryCount=count($categoryResult);

    for($k=0;$k<$categoryCount;$k++)
    {
      $category=$categoryResult[$k];
      array_push($categoryList, $category['name']);
    }
    $classification['categories']=$categoryList;
  } catch (Exception $e) {$success=false;}

  array_push($classifications, $classification);
}
$response['classifications']=$classifications;
$response['success']=$success;

//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
