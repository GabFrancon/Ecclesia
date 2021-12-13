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

$query = $db->prepare('SELECT sender, firstname, lastname, profile_picture, time_stamping, new, Project.id, title, summary, picture, website, likes, meeting_date, end_date, meeting_time, place, lat, lng
                       FROM Project JOIN
                       (Meeting JOIN (Sharing JOIN User ON Sharing.sender = User.id) ON Meeting.project_id=Sharing.project)
                       ON Project.id=Sharing.project
                       WHERE receiver=:user_id
                       UNION ALL
                       (SELECT id, firstname, lastname, profile_picture, time_stamping, new, null, null, null, null, null, null, null, null, null, null, null, null
                         FROM User INNER JOIN Friendship ON User.id=Friendship.applicant
                         WHERE acceptor=:user_id AND friend_request_accepted=0)
                         ORDER BY time_stamping DESC');

$query->bindParam(':user_id',$user_id);

try
{
  $query->execute();
  $response['success']=true;

  if($query->rowCount()!=0)
  {
    /*Retrieving data result in a multidimensional array
    containing arrays indexed by column names*/
    $result=$query->fetchAll(PDO::FETCH_ASSOC);

    //Put the data into the response
    $response['notifications']=$result;
  }
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
