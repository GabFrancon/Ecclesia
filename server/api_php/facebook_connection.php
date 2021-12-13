<?php

//import DataBaseConnect class
require_once __DIR__.'/db_connect.php';

//Connecting to database and get the PDO connector
$dbConnect = new DatabaseConnect();
$db = $dbConnect->getDB();
// '{"facebook_id":"228998795621268","facebook_email":"gab.francon@gmail.com"}';
//Store facebook id and facebook email inputs from user
$input= file_get_contents("php://input");
$data=json_decode($input);
$facebook_id=$data->facebook_id;
$facebook_email=$data->facebook_email;

//Create response array that will be returned
$response = array();

//Preparing the SQL query
$query = $db->prepare('SELECT id, firstname, lastname, profile_picture FROM User
                       WHERE facebook_id=:facebook_id
                     ');

$query->bindParam(':facebook_id',$facebook_id);

//Executing the query

try
{
  $query->execute();
  $response['success']=true;

  //check if the user already has an account with Facebook
  if($query->rowCount()!=0)
  {
    $response['fb_account']=true;
    $result=$query->fetch(PDO::FETCH_ASSOC);

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

    $response['profile']=$result;
    $response['token']=substr($token, 17);

  }
  else
  //if not, try to find an existing account with the same email
  {
    $response['fb_account']=false;

    $new_query = $db->prepare('SELECT id, firstname, lastname, profile_picture FROM User
                              WHERE email=:email AND facebook_id=0');

    $new_query->bindParam(':email',$facebook_email);
    $new_query->execute();

    if($new_query->rowCount()!=0)
    {
      $profile=$new_query->fetch(PDO::FETCH_ASSOC);
      $response['account']=true;
      $response['profile']=$profile;
    }
    else
    {
      $response['account']=false;
    }
  }


} catch (Exception $e)
{
  $response['success']=false;
  $response['message']='Une erreur est survenue lors de la vérification de votre compte facebook';
}



//return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
