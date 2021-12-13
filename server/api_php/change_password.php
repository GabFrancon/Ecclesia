<?php
//import jwtConnect class
require_once __DIR__.'/jwt_connect.php';

//Store id of the user who wants to get projects
$input=file_get_contents("php://input");
$data=json_decode($input);
$jwt=$data->jwt;
$old_password=$data->old_password;
$new_password=$data->new_password;

//Connecting to database and get the PDO connector
$dbConnect = new jwtConnect($jwt);
$db = $dbConnect->getDB();
$user = $dbConnect->getID();
$authorization = $dbConnect->isDev();

$response = array();


$query=$db->prepare('SELECT password FROM User WHERE id=:user');

$query->bindParam(':user',$user);
$query->execute();

if ($query->rowCount()!=0)
{
  $result=$query->fetch(PDO::FETCH_ASSOC);
  $password=$result['password'];

  if (strcmp($password,$old_password)==0)
  {
    $query = $db->prepare('UPDATE User
                          SET password=:password
                          WHERE id=:user
                         ');

    $query->bindParam(':password',$new_password);
    $query->bindParam(':user',$user);

    //Executing the query
    try
    {
      $query->execute();
      $response['success']=true;
    } catch(Exception $e)
    {
      $response['success']=false;
      $response['message']='Impossible de mettre à jour le mot de passe';
    }
  }
  else
  {
    $response['success']=false;
    $response['message']='mot de passe actuel incorrect';
  }
}
else
{
  $response['success']=false;
  $response['message']='Cet utilisateur n\'existe pas';
}


//return response encoded in JSON
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

//Disconnecting from database
$dbConnect->close();

?>
