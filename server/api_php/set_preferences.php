<?php
error_reporting(0);

/*INPUT TYPE TO RESPECT : example with length=2 - Maxi 10 areas and 8 categories per user

{
  "areas":["Agriculture","Travail"],
  "categories":["Rencontre","Excursion"],
  "user_id":"1"
}

*/

//import jwtConnect class
require_once __DIR__.'/jwt_connect.php';

//Store id of the user who wants to get projects
$input=file_get_contents("php://input");
$data=json_decode($input, true);
$jwt=$data['jwt'];
$areas=$data['areas'];
$categories=$data['categories'];

//Connecting to database and get the PDO connector
$dbConnect = new jwtConnect($jwt);
$db = $dbConnect->getDB();
$user_id = $dbConnect->getID();
$authorization = $dbConnect->isDev();

$response = array();

//delete old preferences
if ($areas!=null)
{
  $deleteAreaQuery = $db->prepare('DELETE FROM User_pref_area
                              WHERE user=:user_id');

  $deleteAreaQuery->bindParam(':user_id',$user_id);
  $deleteAreaQuery->execute();

  list($area1,$area2,$area3,$area4,$area5,$area6,$area7,$area8,$area9,$area10)=$areas;

  $areaQuery = $db->prepare('INSERT INTO User_pref_area (user, area)
                             SELECT :user_id, id
                             FROM Area WHERE name IN (:area1,:area2,:area3,:area4,:area5,:area6,:area7,:area8,:area9,:area10);
                          ');

  $areaQuery->bindParam(':user_id',$user_id);
  $areaQuery->bindParam(':area1',$area1);
  $areaQuery->bindParam(':area2',$area2);
  $areaQuery->bindParam(':area3',$area3);
  $areaQuery->bindParam(':area4',$area4);
  $areaQuery->bindParam(':area5',$area5);
  $areaQuery->bindParam(':area6',$area6);
  $areaQuery->bindParam(':area7',$area7);
  $areaQuery->bindParam(':area8',$area8);
  $areaQuery->bindParam(':area9',$area9);
  $areaQuery->bindParam(':area10',$area10);

  try
  {
    $areaQuery->execute();
    $response['success']=true;
  } catch (Exception $e)
  {
    $response['success']=false;
    $response['message']='failed to store area preferences';
  }
}
if ($categories!=null)
{
  $deleteCategoryQuery = $db->prepare('DELETE FROM User_pref_category
                              WHERE user=:user_id');

  $deleteCategoryQuery->bindParam(':user_id',$user_id);
  $deleteCategoryQuery->execute();

  list($category1,$category2,$category3,$category4,$category5,$category6,$category7,$category8)=$categories;

  $categoryQuery = $db->prepare('INSERT INTO User_pref_category (user, category)
                                 SELECT :user_id, id
                                 FROM Category WHERE name IN (:category1,:category2,:category3,:category4,:category5,:category6,:category7,:category8)
                          ');

  $categoryQuery->bindParam(':user_id',$user_id);
  $categoryQuery->bindParam(':category1',$category1);
  $categoryQuery->bindParam(':category2',$category2);
  $categoryQuery->bindParam(':category3',$category3);
  $categoryQuery->bindParam(':category4',$category4);
  $categoryQuery->bindParam(':category5',$category5);
  $categoryQuery->bindParam(':category6',$category6);
  $categoryQuery->bindParam(':category7',$category7);
  $categoryQuery->bindParam(':category8',$category8);

  try
  {
    $categoryQuery->execute();
    $response['success']=true;
  } catch (Exception $e)
  {
    $response['success']=false;
    $response['message']='failed to store category preferences';
  }
}
//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
