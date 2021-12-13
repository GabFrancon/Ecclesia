<?php

//import DataBaseConnect class
require_once __DIR__.'/db_connect.php';

//Connecting to database and get the PDO connector
$dbConnect = new DatabaseConnect();
$db = $dbConnect->getDB();

//Store email and password inputs from alleged user
$input=file_get_contents("php://input");
$data=json_decode($input);
$email=$data->email;
$password=$data->password;

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
  $query = $db->prepare('SELECT id, firstname, lastname, profile_picture, password
                         FROM User WHERE email=:email
                       ');

  $query->bindParam(':email',$email);

  try
  {
    $query->execute();
    $result=$query->fetch(PDO::FETCH_ASSOC);


    $success = ($result['password']==$password);
    //Test identifiers input
    if ( $success && ($result['id']!=null) )
    {
      //get JWT for user
      $url="https://pact2321.r2.enst.fr/jwt_manager.php";

      $data=array('name'=>$result['firstname'].'.'.$result['lastname'],
                  'id'=>$result['id'],
                  'auth'=>'standard');

      $options=array('http'=>array('method'=>'POST',
                                   'header'=>'Content-type: application/json',
                                   'content'=>json_encode($data))
                                 );


      $context=stream_context_create($options);
      $token=file_get_contents($url,false,$context);

      //Add info from user profile (+valid token and refresh token normally)
      $profile=array();
      $profile['firstname']=$result['firstname'];
      $profile ['id']=$result ['id'];
      $profile['profile_picture']=$result['profile_picture'];

      $response['token']=substr($token, 17);
      $response['profile']=$profile;
      $response['success']=true;
    }
    else
    {
      $response['success']=false;
      $response['message']='identifiants incorrects';
    }
  }catch (Exception $e)
  {
    $response['success']=false;
    $response['message']='une erreur est survenue lors de la vérification de vos identifiants';
  }
}

//return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
