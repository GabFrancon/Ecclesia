<?php

//import jwtConnect class
require_once __DIR__.'/jwt_connect.php';

//Store id of the user who wants to get projects
$input=file_get_contents("php://input");
$data=json_decode($input);
$jwt=$data->jwt;
$sender=$data->sender;
$time_stamping=$data->time_stamping;
$sharing=$data->sharing;

//Connecting to database and get the PDO connector
$dbConnect = new jwtConnect($jwt);
$db = $dbConnect->getDB();
$user_id = $dbConnect->getID();
$authorization = $dbConnect->isDev();

$response = array();

if ($sharing==1)
{
  $query = $db->prepare('UPDATE Sharing SET new=0
                         WHERE receiver=:user_id
                         AND sender=:sender
                         AND time_stamping=:time_stamping
                         ');

  $query->bindParam(':user_id',$user_id);
  $query->bindParam(':sender',$sender);
  $query->bindParam(':time_stamping',$time_stamping);
}
else
{
  $query = $db->prepare('UPDATE Friendship SET new=0
                         WHERE acceptor=:user_id
                         AND applicant=:applicant
                         ');
  $query->bindParam(':user_id',$user_id);
  $query->bindParam(':applicant',$sender);
}

try
{
  $query->execute();
  $response['success']=true;

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
