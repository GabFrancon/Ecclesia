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

$query = $db->prepare('SELECT COUNT(*) AS new_count
                       FROM User JOIN Sharing ON User.id=Sharing.receiver
                       WHERE receiver=:user_id AND new=1
                       UNION ALL
                       (SELECT COUNT(*)
                       FROM User JOIN Friendship ON User.id=Friendship.applicant
                       WHERE acceptor=:user_id AND friend_request_accepted=0 AND new=1)
                       ');

$query->bindParam(':user_id',$user_id);

try
{
  $query->execute();
  $response['success']=true;

//count of shared projects
  $result=$query->fetch(PDO::FETCH_ASSOC);
  $count_sharing=$result['new_count'];

//count of friend requests
  $result=$query->fetch(PDO::FETCH_ASSOC);
  $count_request=$result['new_count'];

  //Put the total count into the response
  $response['count']=$count_sharing+$count_request;

} catch (Exception $e)
{
  $response['success']=false;
  $response['message']='une erreur est survenue lors du chargement des demandes d\'ami';
}

//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
