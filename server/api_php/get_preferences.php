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
$user_id = $dbConnect->getID();
$authorization = $dbConnect->isDev();

$response = array();
$areaSuccess=false;
$categorySuccess=false;

//Executing the area query
$areaQuery = $db->prepare('SELECT name
                           FROM Area INNER JOIN User_pref_area
                           ON Area.id=User_pref_area.area
                           WHERE user=:user_id
                        ');

$areaQuery->bindParam(':user_id',$user_id);

try
{
  $areaQuery->execute();

  if ( $areaQuery->rowCount()!=0 )
  {
    $areaList=array();

    $areaResult=$areaQuery->fetchAll(PDO::FETCH_ASSOC);
    $areaCount=count($areaResult);

    for($i=0;$i<$areaCount;$i++)
    {
      $area=$areaResult[$i];
      array_push($areaList, $area['name']);
    }
    $response['areas']=$areaList;
    $areaSuccess=true;

    //Executing the category query
    $categoryQuery = $db->prepare('SELECT name
                                   FROM Category INNER JOIN User_pref_category
                                   ON Category.id=User_pref_category.category
                                   WHERE user=:user_id
                                 ');

    $categoryQuery->bindParam(':user_id',$user_id);

    try
    {
      $categorySuccess=$categoryQuery->execute();

      if ( $categoryQuery->rowCount()!=0 )
      {
        $categoryList=array();


        $categoryResult=$categoryQuery->fetchAll(PDO::FETCH_ASSOC);
        $categoryCount=count($categoryResult);

        for($i=0;$i<$categoryCount;$i++)
        {
          $category=$categoryResult[$i];
          array_push($categoryList, $category['name']);
        }
        $response['categories']=$categoryList;
        $categorySuccess=true;
      }
      else
      {
        //Put a failure message in the response with the key 'message'
        $response['message']='aucune catégorie trouvée';
      }
    } catch (Exception $e)
    {
      $response['message']="une erreur est survenue en chargeant les préférences catégories";
    }

  }
  else
  {
    //Put a failure message in the response with the key 'message'
    $response['message']='aucun domaine trouvé';
  }

} catch (Exception $e)
{
  $response['message']="une erreur est survenue en chargeant les préférences domaines";
}


//Add a success or failure tag in the response with the key 'success'
$response['success']=$areaSuccess && $categorySuccess;

//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
