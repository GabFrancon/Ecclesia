<?php
//import jwtConnect class
require_once __DIR__.'/jwt_connect.php';

//Store id of the user who wants to get projects
$input=file_get_contents("php://input");
$data=json_decode($input);
$jwt=$data->jwt;
$old_email=$data->old_email;
$new_email=$data->new_email;

//Connecting to database and get the PDO connector
$dbConnect = new jwtConnect($jwt);
$db = $dbConnect->getDB();
$user = $dbConnect->getID();
$authorization = $dbConnect->isDev();

$response = array();


$query=$db->prepare('SELECT email FROM User WHERE id=:user');

$query->bindParam(':user',$user);
$query->execute();

if ($query->rowCount()!=0)
{
  $result=$query->fetch(PDO::FETCH_ASSOC);

  $email=$result['email'];

  if (strcmp($email,$old_email)==0)
  {
    if (!filter_var($new_email, FILTER_VALIDATE_EMAIL))
    {
      $response['message']='format du nouvel email invalide';
      $response['success']=false;
    }
    else
    {
      $query = $db->prepare('UPDATE User
                            SET email=:email
                            WHERE id=:user
                           ');

      $query->bindParam(':email',$new_email);
      $query->bindParam(':user',$user);

      //Executing the query
      try
      {
        $query->execute();
        $response['success']=true;
      } catch(Exception $e)
      {
        $response['success']=false;
        $response['message']='Impossible de mettre à jour l\'email';
      }
    }
  }
  else
  {
    $response['success']=false;
    $response['message']='Email actuel incorrect';
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
