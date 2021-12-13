<?php
//import DataBaseConnect class
require_once __DIR__.'/db_connect.php';

//Connecting to database and get the PDO connector
$dbConnect = new DatabaseConnect();
$db = $dbConnect->getDB();

//Store id of the user who wants to get projects
$input=file_get_contents("php://input");
$data=json_decode($input);
$coordinates=$data->coordinates;

$response=array();

/*$query=$db->prepare('SELECT place FROM Meeting
                    WHERE lat IS NULL OR lng IS NULL
                   ');

$query->execute();

$length=$query->rowCount();*/
$length=count($coordinates);
for ($i=0;$i<$length;$i++)
{
  /*$result=$query->fetch(PDO::FETCH_ASSOC);
  $address=$result['place'];
  $map_address=$address.",+CA&key=AIzaSyDpXLnsFkVpfJo5k7dwE7XJmjoLNDKHuJQ");
  $url = "https://maps.googleapis.com/maps/api/geocode/json?address=".$map_address;
  //$options=array('http'=>array('method'=>'GET'));
  //$context=stream_context_create($options);

  $result = get_object_vars(json_decode(file_get_contents($url)));
  $lat= $result['results'][0]->geometry->location->lat ;
  $lgn = $result['results'][0]->geometry->location->lng;
  $place = $result['results'][0]-> ... ?; */

  $item=$coordinates[$i];
  $project_id=$item->project_id;
  $lat=$item->lat;
  $lng=$item->lng;

  $query=$db->prepare('UPDATE Meeting
                      SET lat=:lat, lng=:lng
                      WHERE project_id=:project_id
                            ');

  $query->bindParam(':project_id',$project_id);
  $query->bindParam(':lat',$lat);
  $query->bindParam(':lng',$lng);

  try
  {
    $query->execute();
    $response['success']=true;
  }catch (Exception $e) {$response['success']=false;}

}

//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
