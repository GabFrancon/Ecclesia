<?php

declare(strict_types=1);

use Firebase\JWT\JWT;

require_once __DIR__.'/vendor/autoload.php';
require_once __DIR__.'/db_config.php';

$input=file_get_contents("php://input");
$data=json_decode($input);
$name=$data->name;
$id=$data->id;
$auth=$data->auth;

$secretKey  = DB_SECRET_KEY;
$issuedAt   = new DateTimeImmutable();
$expire     = $issuedAt->modify('+15 minutes')->getTimestamp();
$serverName = "e_cclesia";
$username   = $name;
$user_id    = $id;
$user_auth  = $auth;

$data = [
    'iat'  => $issuedAt->getTimestamp(),         // Issued at: time when the token was generated
    'iss'  => $serverName,                       // Issuer
    'nbf'  => $issuedAt->getTimestamp(),         // Not before
    'exp'  => $expire,                           // Expire
    'userName' => $username,                     // User name
    'userID' => $user_id,                        // User ID
    'auth' => $user_auth                         // User Authorization
];

echo JWT::encode(
    $data,
    $secretKey,
    'HS512'
    );

?>
