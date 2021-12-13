<?php

//import DataBaseConnect class
require_once __DIR__.'/db_connect.php';

//Connecting to database and get the PDO connector
$dbConnect = new DatabaseConnect();
$db = $dbConnect->getDB();

//Store profile information inputs from future user
$input =file_get_contents("php://input");
$data=json_decode($input);

$firstname=$data->firstname;
$lastname=$data->lastname;
$birth=$data->birth;
$gender=$data->gender;
$location=$data->location;
$email=$data->email;
$password=$data->password;
$profile_picture=$data->picture;
$facebook_id=$data->facebook_id;

//Create response array that will be returned
$response = array();

//Test email format
if (!filter_var($email, FILTER_VALIDATE_EMAIL))
{
  $response['message']='format de l\'email invalide';
  $response['success']=false;
}
else
{
  //Preparing the SQL query
  $query = $db->prepare('INSERT INTO User (firstname, email, location, birth, lastname,gender,password,facebook_id,profile_picture)
                        VALUES (:firstname,:email,:user_location,:birth,:lastname,:gender,:password,:facebook_id,:profile_picture)
                       ');

  $query->bindParam(':firstname',$firstname);
  $query->bindParam(':lastname',$lastname);
  $query->bindParam(':birth',$birth);
  $query->bindParam(':gender',$gender);
  $query->bindParam(':user_location',$location);
  $query->bindParam(':email',$email);
  $query->bindParam(':password',$password);
  $query->bindParam(':profile_picture',$profile_picture);
  $query->bindParam(':facebook_id',$facebook_id);

  //Executing the query
  try
  {
    $query->execute();

    //Retrieving the id of the new user
    $idQuery = $db->prepare('SELECT MAX(id) FROM User');
    $idQuery->execute();
    $result=$idQuery->fetch(PDO::FETCH_ASSOC);
    $user_id=$result['MAX(id)'];

    echo $user_id;
    //get JWT for user
    $url="https://pact2321.r2.enst.fr/jwt_manager.php";

    $data=array('name'=>$firstname.'.'.$lastname,
                'id'=>$user_id,
                'auth'=>'standard');

    $options=array('http'=>array('method'=>'POST',
                                 'header'=>'Content-type: application/json',
                                 'content'=>json_encode($data))
                               );


    $context=stream_context_create($options);
    $token=file_get_contents($url,false,$context);

    $response['token']=substr($token, 17);
    $response['success']=true;
  } catch(Exception $e)
  {
    $response['success']=false;
    $response['message']='Impossible de créer le nouveau compte';
  }
}
//return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
