<?php

//import DataBaseConnect class
require_once __DIR__.'/db_connect.php';

//Connecting to database and get the PDO connector
$dbConnect = new DatabaseConnect();
$db = $dbConnect->getDB();

//Store id of the two potential friends
$input=file_get_contents("php://input");
$data=json_decode($input);
$user_id_1=$data->user1;
$user_id_2=$data->user2;

//Create response array that will be returned
$response = array();

//Executing the SQL query
$query = $db->prepare('SELECT COUNT(*) AS are_friends FROM Friendship
                       WHERE ((applicant=:user_id_1 AND acceptor=:user_id_2)
                       OR (applicant=:user_id_2 AND acceptor=:user_id_1))
                       AND friend_request_accepted=1
                      ');

$query->bindParam(':user_id_1',$user_id_1);
$query->bindParam(':user_id_2',$user_id_2);

 try
 {
   $query->execute();

   if ($query->rowCount()>0)
   {
     $result=$query->fetch(PDO::FETCH_ASSOC);

     if ($result['are_friends']>0)
     {
       $response['are_friends']=true;
       $response['success']=true;
     }
     else
     {
       $response['are_friends']=false;
       $response['success']=true;
     }
   }
   else
   {
     $response['message']='an error occured in the counting request';
     $response['success']=false;
   }
 } catch (Exception $e)
 {
   $response['message']='an error occured when trying to execute the request';
   $response['success']=false;
 }

//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
