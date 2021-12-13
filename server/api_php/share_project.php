<?php

//import jwtConnect class
require_once __DIR__.'/jwt_connect.php';

//Store id of the user who wants to get projects
$input=file_get_contents("php://input");
$data=json_decode($input);
$jwt=$data->jwt;
$receiver=$data->receiver;
$project=$data->project;

//Connecting to database and get the PDO connector
$dbConnect = new jwtConnect($jwt);
$db = $dbConnect->getDB();
$sender = $dbConnect->getID();
$authorization = $dbConnect->isDev();

$response = array();

//check whereas sender and receiver are friends or not
$url="https://pact2321.r2.enst.fr/get_friendship_status.php";

$data=array('jwt'=>$jwt,'user_id_2'=>$receiver);

$options=array('http'=>array('method'=>'POST',
                             'header'=>'Content-type: application/json',
                             'content'=>json_encode($data))
                           );


$context=stream_context_create($options);
$result=file_get_contents($url,false,$context);
$start=strpos($result,'{');
$end=strrpos($result,'}');
$json=substr($result,$start,$end-$start+1);

$friendship_status=json_decode($json);
$friend_link=$friendship_status->friend_link;

if($friend_link)
{
  $are_friends=$friendship_status->friend_link;

  if (!$are_friends)
  {
    $response['success']=false;
    $respone['message']='Cet utilisateur ne fait pas partie de vos amis';
  }
  else
  {
    //Executing the SQL query
    $query = $db->prepare('INSERT INTO Sharing (sender, receiver, project) VALUES
                          (:sender,:receiver,:project)
                          ');

    $query->bindParam(':sender',$sender);
    $query->bindParam(':receiver',$receiver);
    $query->bindParam(':project',$project);

    try
    {
      $query->execute();
      $response['success']=true;
    } catch (Exception $e)
    {
      $response['success']=false;
      $response['message']='Vous avez déjà partagé ce projet';
    }
  }
}
else
{
  $response['success']=false;
  $response['message']='Cet utilisateur ne fait pas partie de vos amis';
}

//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();




?>
