<?php

/*INPUT TYPE TO RESPECT : example with length=2

{
  "items":[
          {
           "project":{"title":"titre1","summary":"description1","website":"site_web1","picture":"photo1","child_friendly":"Y"},
           "meeting":{"date":"2021-01-01","end_date":"2021-02-04",time":"11:11:11","place":"place1","format":"P"}
          },
          {"project":{"title":"titre2","summary":"description2","website":"site_web2","picture":"photo2","child_friendly":"Y"},
           "meeting":{"date":"2022-02-02","end_date":"null","time":"22:22:22","place":"place2","format":"P"}
          }
         ],
  "length":2
}

*/


//import DataBaseConnect class
require_once __DIR__.'/db_connect.php';

//Connecting to database and get the PDO connector
$dbConnect = new DatabaseConnect();
$db = $dbConnect->getDB();

//Store the array containing project/meeting couples, and their number
$input=file_get_contents("php://input");
$data=json_decode($input,true);

$length=$data['length'];
$items=$data['items'];

//Create response array that will be returned
$response = array();

//Retrieve the presently max project id
$idQuery = $db->prepare('SELECT MAX(id) FROM Project');
$idQuery->execute();
$result=$idQuery->fetch(PDO::FETCH_ASSOC);
$current_max_id=(int)$result["MAX(id)"];


for($i=0;$i<$length;$i++)
{
  //stores each time the current project/meeting couple, then separates them
  $item=$items[$i];

  $project=$item['project'];
  $meeting=$item['meeting'];


  $projectQuery = $db->prepare('INSERT INTO Project (title, summary, picture, website, child_friendly)
                                VALUES (:title,:summary,:picture,:website,:child_friendly)
                              ');

    $projectQuery->bindParam(':title',$project['title']);
    $projectQuery->bindParam(':summary',$project['summary']);
    $projectQuery->bindParam(':picture',$project['picture']);
    $projectQuery->bindParam(':website',$project['website']);
    $projectQuery->bindParam(':child_friendly',$project['child_friendly']);

  try{
      $projectQuery->execute();

      $meetingQuery=$db->prepare('INSERT INTO Meeting (project_id, meeting_date, end_date, meeting_time, place, format)
                                  VALUES (:project_id,:meeting_date,:end_date,:meeting_time,:place,:format)
                                ');

      $project_id=$current_max_id+$i+1;

      $meetingQuery->bindParam(':project_id',$project_id);
      $meetingQuery->bindParam(':meeting_date',$meeting['date']);
      $meetingQuery->bindParam(':end_date',$meeting['end_date']);
      $meetingQuery->bindParam(':meeting_time',$meeting['time']);
      $meetingQuery->bindParam(':place',$meeting['place']);
      $meetingQuery->bindParam(':format',$meeting['format']);

      try{
        $meetingQuery->execute();
        $response['success']=true;

      }catch(Exception $e) {$response['success']=false;}

  }catch (Exception $e) {$response['success']=false;}
}


//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
