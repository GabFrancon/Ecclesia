<?php

//import jwtConnect class
require_once __DIR__.'/jwt_connect.php';

//Store id of the user who wants to get projects
$input=file_get_contents("php://input");
$data=json_decode($input);
$jwt=$data->jwt;
$search=$data->search;

//Connecting to database and get the PDO connector
$dbConnect = new jwtConnect($jwt);
$db = $dbConnect->getDB();
$user = $dbConnect->getID();
$authorization = $dbConnect->isDev();

$response = array();

$keyword =  $search . '*';

//Executing the SQL query
$query = $db->prepare('SELECT id, firstname, lastname, profile_picture FROM User
                       WHERE MATCH (firstname, lastname)
                       AGAINST (:keyword IN BOOLEAN MODE)
                       ORDER BY firstname, lastname
                      ');

$query->bindParam(':keyword',$keyword);

try
{
  $query->execute();

  if ( $query->rowCount()!=0 )
  {
    /*Retrieving data result in a multidimensional array
    containing arrays indexed by column names*/
    $result=$query->fetchAll(PDO::FETCH_ASSOC);

    //Put the data into the response
    $response['users']=$result;
    $response['success']=true;
  }
  else
  {
    //Put a failure message in the response with the key 'message'
    $response['message']='aucun résultat';
    $response['success']=false;
  }
} catch (Exception $e)
{
  $response['message']='une erreur est survenue lors de la recherche';
  $response['success']=false;
}

//Return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
