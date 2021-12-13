<?php
error_reporting(0);
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

//get preferences of the user
$url="https://pact2321.r2.enst.fr/get_preferences.php";

$data=array('jwt'=>$jwt);

$options=array('http'=>array('method'=>'POST',
                             'header'=>'Content-type: application/json',
                             'content'=>json_encode($data))
                           );


$context=stream_context_create($options);
$result=file_get_contents($url,false,$context);
$start=strpos($result,'{');
$end=strrpos($result,'}');
$json=substr($result,$start,$end-$start+1);

$preferences=json_decode($json,true);
$pref_areas=$preferences['areas'];
$pref_categories=$preferences['categories'];
$success=$preferences['success'];

if ($success)
{
  list($area1,$area2,$area3,$area4,$area5,$area6,$area7,$area8,$area9,$area10)=$pref_areas;
  list($category1,$category2,$category3,$category4,$category5,$category6,$category7,$category8)=$pref_categories;

  //Executing the SQL query
  $query = $db->prepare('SELECT Project.id, title, summary, picture, website, meeting_date, end_date, meeting_time, place, likes, lat, lng
                         FROM User JOIN
                         (Project_recommandation JOIN
                         (Project INNER JOIN
                         (Meeting INNER JOIN
                         (Project_area NATURAL JOIN Project_category)
                         ON Meeting.project_id=Project_area.project)
                         ON Project.id=Meeting.project_id)
                         ON Project_recommandation.project_id=Project.id)
                         ON User.id=Project_recommandation.user_id
                         WHERE Project.id NOT IN
                        (SELECT project_id FROM User_like
                         WHERE user_id=:user AND opinion=0)
                         AND Project.id NOT IN
                        (SELECT project_id FROM User_pref_project
                         WHERE user_id=:user)
                         AND Project_recommandation.user_id=:user
                         AND (area IN(SELECT id FROM Area WHERE name IN
                           (:area1,:area2,:area3,:area4,:area5,:area6,:area7,:area8,:area9,:area10))
                           OR category IN(SELECT id FROM Category WHERE name IN
                             (:category1,:category2,:category3,:category4,:category5,:category6,:category7,:category8)))
                         GROUP BY Project.id, meeting_date, end_date, meeting_time, place, lat, lng
                         ORDER BY grade DESC, likes DESC
                        ');




  $query->bindParam(':user',$user);

  $query->bindParam(':area1',$area1);
  $query->bindParam(':area2',$area2);
  $query->bindParam(':area3',$area3);
  $query->bindParam(':area4',$area4);
  $query->bindParam(':area5',$area5);
  $query->bindParam(':area6',$area6);
  $query->bindParam(':area7',$area7);
  $query->bindParam(':area8',$area8);
  $query->bindParam(':area9',$area9);
  $query->bindParam(':area10',$area10);

  $query->bindParam(':category1',$category1);
  $query->bindParam(':category2',$category2);
  $query->bindParam(':category3',$category3);
  $query->bindParam(':category4',$category4);
  $query->bindParam(':category5',$category5);
  $query->bindParam(':category6',$category6);
  $query->bindParam(':category7',$category7);
  $query->bindParam(':category8',$category8);
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
      $query = $db->prepare('SELECT Project.id, title, summary, picture, website, meeting_date, end_date, meeting_time, place, likes, lat, lng
                             FROM Project INNER JOIN
                             (Meeting INNER JOIN
                             (Project_area NATURAL JOIN Project_category)
                             ON Meeting.project_id=Project_area.project)
                             ON Project.id=Meeting.project_id
                             WHERE Project.id NOT IN
                            (SELECT project_id FROM User_like
                             WHERE user_id=:user AND opinion=0)
                             AND Project.id NOT IN
                            (SELECT project_id FROM User_pref_project
                             WHERE user_id=:user)
                             AND (area IN(SELECT id FROM Area WHERE name IN
                               (:area1,:area2,:area3,:area4,:area5,:area6,:area7,:area8,:area9,:area10))
                               OR category IN(SELECT id FROM Category WHERE name IN
                                 (:category1,:category2,:category3,:category4,:category5,:category6,:category7,:category8)))
                             GROUP BY Project.id, meeting_date, end_date, meeting_time, place, lat, lng
                             ORDER BY likes DESC
                            ');

      $query->bindParam(':user',$user);

      $query->bindParam(':area1',$area1);
      $query->bindParam(':area2',$area2);
      $query->bindParam(':area3',$area3);
      $query->bindParam(':area4',$area4);
      $query->bindParam(':area5',$area5);
      $query->bindParam(':area6',$area6);
      $query->bindParam(':area7',$area7);
      $query->bindParam(':area8',$area8);
      $query->bindParam(':area9',$area9);
      $query->bindParam(':area10',$area10);

      $query->bindParam(':category1',$category1);
      $query->bindParam(':category2',$category2);
      $query->bindParam(':category3',$category3);
      $query->bindParam(':category4',$category4);
      $query->bindParam(':category5',$category5);
      $query->bindParam(':category6',$category6);
      $query->bindParam(':category7',$category7);
      $query->bindParam(':category8',$category8);

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
          $response['message']='Aucun projet trouvé';
          $response['success']=false;
        }
      } catch (Exception $e)
      {
        $response['message']='une erreur est survenue lors du chargement de projets';
        $response['success']=false;
      }
    }
  } catch (Exception $e)
  {
    $response['message']='une erreur est survenue lors du chargement de projets';
    $response['success']=false;
  }
}
else
{
  $response['message']='une erreur est survenue lors du chargement des préférences';
  $response['success']=false;
}



//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
