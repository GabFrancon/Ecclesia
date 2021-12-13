<?php
error_reporting(0);
/*INPUT TYPE TO RESPECT : example with length=2 - Maxi 3 areas and 3 categories per project

{
  "items":[
          {
           "project":"28",
           "area":["area11","area12","area13"],
           "category":["category11","category12","category3"]
         },
          {
           "project":"42",
           "area":["area21","area22","area23"],
           "category":["category21","category22","category23"]
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

//Store the array containing project/classification couples, and their number
$input=file_get_contents("php://input");
$data=json_decode($input,true);

$length=$data['length'];
$items=$data['items'];

//Create response array that will be returned
$response = array();

for($i=0;$i<$length;$i++)
{
  //stores each time the current project/classification couple, then separates them
  $item=$items[$i];

  $project=$item['project'];
  $area=$item['area'];
  $category=$item['category'];

  list($area1,$area2,$area3)=$area;
  list($category1,$category2,$category3)=$category;

  $areaQuery = $db->prepare('INSERT INTO Project_area (project, area)
                             SELECT :project_id, id
                             FROM Area WHERE name IN (:area1,:area2,:area3);
                          ');

  $areaQuery->bindParam(':project_id',$project);
  $areaQuery->bindParam(':area1',$area1);
  $areaQuery->bindParam(':area2',$area2);
  $areaQuery->bindParam(':area3',$area3);

  try{
      $areaQuery->execute();

      $categoryQuery = $db->prepare('INSERT INTO Project_category (project, category)
                                     SELECT :project_id, id
                                     FROM Category WHERE name IN (:category1,:category2,:category3);
                                   ');

      $categoryQuery->bindParam(':project_id',$project);
      $categoryQuery->bindParam(':category1',$category1);
      $categoryQuery->bindParam(':category2',$category2);
      $categoryQuery->bindParam(':category3',$category3);

      try {
        $categoryQuery->execute();
        $response['success']=true;

      } catch (Exception $e) {$response['success']=false;}

     } catch (Exception $e) {$response['success']=false;}
  }

//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
