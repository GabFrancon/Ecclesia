<?php

//import DataBaseConnect class
require_once __DIR__.'/db_connect.php';

//Connecting to database and get the PDO connector
$dbConnect = new DatabaseConnect();
$db = $dbConnect->getDB();

//Store each project, its informations and meetings
$input=file_get_contents("php://input");
$data=json_decode($input);
$length=$data->length;
$project=$data->project;

//Create response array that will be returned
$response = array();

//Retrieve the presently max project id
$idQuery = $db->prepare('SELECT MAX(id) FROM Project');
$idQuery->execute();
$current_max_id=$idQuery->fetch(PDO::FETCH_ASSOC);



$projectQuery = $db->prepare('INSERT INTO Project (title, summary, picture, website, child_friendly)
                              VALUES (:title,:summary,:picture,:website,:child_friendly)
                            ');

  $projectQuery->bindParam(':title',$project->title);
  $projectQuery->bindParam(':summary',$project->summary);
  $projectQuery->bindParam(':picture',$project->picture);
  $projectQuery->bindParam(':website',$project->website);
  $projectQuery->bindParam(':child_friendly',$project->child_friendly);

try{
    $projectQuery->execute();
    $response['success']=true;
}catch (Exception $e) {$success[false];}

//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
