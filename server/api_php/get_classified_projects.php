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
$collections=array();


////////////////////////////
//GET PROJECTS OF THE WEEK//
////////////////////////////

$weekCollection=array();

$query = $db->prepare('SELECT Project.id, title, summary, picture, website, meeting_date, end_date, meeting_time, place, likes, lat, lng
                       FROM Project INNER JOIN Meeting
                       ON Project.id=Meeting.project_id
                       WHERE meeting_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 1 WEEK)
                       OR end_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 1 WEEK)
                       GROUP BY Project.id, meeting_date, end_date, meeting_time, place, lat, lng
                       ORDER BY RAND()
                       LIMIT 20
                      ');

try
{
  $query->execute();

  if ( $query->rowCount()!=0 )
  {
    $result=$query->fetchAll(PDO::FETCH_ASSOC);

    $weekCollection['projects']=$result;
    $weekCollection['classification']='Cette semaine';
    array_push($collections, $weekCollection);
  }
} catch (Exception $e)
{
  $response['message']='une erreur est survenue lors du chargement de projets';
  $response['success']=false;
}








//////////////////////
//GET CURRENT TRENDS//
//////////////////////

$trendCollection=array();

$query = $db->prepare('SELECT Project.id, title, summary, picture, website, meeting_date, end_date, meeting_time, place, likes, lat, lng
                       FROM Project INNER JOIN Meeting
                       ON Project.id=Meeting.project_id
                       GROUP BY Project.id, meeting_date, end_date, meeting_time, place, lat, lng
                       ORDER BY likes DESC
                       LIMIT 20
                      ');

try
{
  $query->execute();

  if ( $query->rowCount()!=0 )
  {
    $result=$query->fetchAll(PDO::FETCH_ASSOC);

    $trendCollection['projects']=$result;
    $trendCollection['classification']='Tendances';
    array_push($collections, $trendCollection);
  }
} catch (Exception $e)
{
  $response['message']='une erreur est survenue lors du chargement des tendances';
  $response['success']=false;
}








////////////////////////////////////
//GET PROJECTS CLASSIFIED BY AREAS//
////////////////////////////////////


$query = $db->prepare('SELECT name FROM Area
                      ORDER BY RAND()
                      ');
$query->execute();
$count=$query->rowCount();
$areas=$query->fetchAll(PDO::FETCH_ASSOC);

try
{
  for ($i=0;$i<$count;$i++)
  {
    $areaCollection=array();
    $area=$areas[$i]['name'];

    $query = $db->prepare('SELECT Project.id, title, summary, picture, website, meeting_date, end_date, meeting_time, place, likes, lat, lng
                           FROM Project INNER JOIN
                           (Meeting INNER JOIN Project_area ON Meeting.project_id=Project_area.project)
                           ON Project.id=Meeting.project_id
                           WHERE area=(SELECT id FROM Area WHERE name=:area)
                           GROUP BY Project.id, meeting_date, end_date, meeting_time, place, lat, lng
                           ORDER BY RAND()
                          ');

    $query->bindParam(':area',$area);
    $query->execute();

    if ( $query->rowCount()!=0 )
    {
      $result=$query->fetchAll(PDO::FETCH_ASSOC);

      $areaCollection['projects']=$result;
      $areaCollection['classification']=$area;
      array_push($collections, $areaCollection);
    }
  }
} catch (Exception $e)
{
  $response['success']=false;
}








/////////////////////////////////////////
//GET PROJECTS CLASSIFIED BY CATEGORIES//
/////////////////////////////////////////

$query = $db->prepare('SELECT name FROM Category
                      ORDER BY RAND()
                      ');
$query->execute();
$categoryCount=$query->rowCount();
$categories=$query->fetchAll(PDO::FETCH_ASSOC);

try
{
  for ($j=0;$j<$categoryCount;$j++)
  {
    $categoryCollection=array();
    $category=$categories[$j]['name'];

    $query = $db->prepare('SELECT Project.id, title, summary, picture, website, meeting_date, end_date, meeting_time, place, likes, lat, lng
                           FROM Project INNER JOIN
                           (Meeting INNER JOIN Project_category ON Meeting.project_id=Project_category.project)
                           ON Project.id=Meeting.project_id
                           WHERE category=(SELECT id FROM Category WHERE name=:category)
                           GROUP BY Project.id, meeting_date, end_date, meeting_time, place, lat, lng
                           ORDER BY RAND()
                          ');

    $query->bindParam(':category',$category);
    $query->execute();

    if ( $query->rowCount()!=0 )
    {
      $result=$query->fetchAll(PDO::FETCH_ASSOC);

      $categoryCollection['projects']=$result;
      $categoryCollection['classification']=$category;
      array_push($collections, $categoryCollection);
    }
  }
  $response['success']=true;

} catch (Exception $e)
{
$response['success']=false;
}











$response['collections']=$collections;


header('Content-Type: application/json');
//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);


//Disconnecting from database
$dbConnect->close();

?>
