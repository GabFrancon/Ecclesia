<?php

//import DataBaseConnect class
require_once __DIR__.'/db_connect.php';

//Connecting to database and get the PDO connector
$dbConnect = new DatabaseConnect();
$db = $dbConnect->getDB();

//Store id of the user who wants to retrieve friend requests that have been made to him
$input=file_get_contents("php://input");
$data=json_decode($input);
$user_id=$data->user_id;

//Create response array that will be returned
$response = array();

//Executing the SQL query
$query = $db->prepare('SELECT Project.id, title, summary, website, picture, sender, time_stamping, meeting_date, meeting_time, place
                      FROM Project JOIN
                      (Sharing JOIN Meeting ON Sharing.project=Meeting.project_id)
                      ON Project.id=Sharing.project
                      WHERE receiver=:user_id
                      ORDER BY time_stamping DESC
                      ');

$query->bindParam(':user_id',$user_id);

try
{
  $query->execute();
  $response['success']=true;
  $length=$query->rowCount();

  if($length!=0)
  {
    /*Retrieving data result in a multidimensional array
    containing arrays indexed by column names*/
    $result=$query->fetchAll(PDO::FETCH_ASSOC);
    //Put the data into the response
    $response['projects']=$result;

    $senders=array();
    for ($i=0;$i<$length;$i++)
    {
      $shared_project=$result[$i];
      $user=$shared_project['sender'];

      $profileQuery = $db->prepare('SELECT id, firstname, lastname, profile_picture
                             FROM User WHERE id=:user
                            ');

      $profileQuery->bindParam(':user',$user);

      try
      {
        $profileQuery->execute();
        $profile=$profileQuery->fetch(PDO::FETCH_ASSOC);
        $senders[]=$profile;
      } catch (Exception $e)
      {
        $e->printStackTrace();
      }
    }
    $response['senders']=$senders;
  }
  else
  {
    $response['success']=false;
    $response['message']='Aucun projets partagés pour le moment';
  }
} catch (Exception $e)
{
  $response['success']=false;
  $response['message']='une erreur est survenue lors du chargement des projets partagés';
}

//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
