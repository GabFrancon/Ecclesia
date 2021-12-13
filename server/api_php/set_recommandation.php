<?php

/*INPUT TYPE TO RESPECT : example with length=2 - Maxi 3 areas and 3 categories per project

'{
"length":2,
"items":[
    {"project":143,"user":123,"grade":"0.345"},
    {"project":193,"user":193,"grade":"0.2438"}
]
}'

*/

//import DataBaseConnect class
require_once __DIR__.'/db_connect.php';

//Connecting to database and get the PDO connector
$dbConnect = new DatabaseConnect();
$db = $dbConnect->getDB();

//Store the array containing project/user couples, and their grade
$input= file_get_contents("php://input");
$data=json_decode($input,true);

$length=$data['length'];
$items=$data['items'];

//Create response array that will be returned
$response = array();

for($i=0;$i<$length;$i++)
{
  //stores each time the current project/user couple, then separates them
  $item=$items[$i];


  $project_id=$item['project'];
  $user_id=$item['user'];
  $grade=$item['grade'];

  $query = $db->prepare('INSERT INTO Project_recommandation (project_id, user_id, grade)
                         VALUES (:project_id,:user_id,:grade)
                        ');

  $query->bindParam(':project_id',$project_id);
  $query->bindParam(':user_id',$user_id);
  $query->bindParam(':grade',$grade);

  try
  {
    $query->execute();
    $response['success']=true;
  } catch (Exception $e)
  {
    $response['success']=false;
  }
}

//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
