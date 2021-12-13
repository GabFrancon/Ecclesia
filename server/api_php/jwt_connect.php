db_connect.php
<?php

use Firebase\JWT\JWT;

class jwtConnect
{
	protected $db;
	protected $isDev;
	protected $id;

	function __construct($jwt)
	{
		$this->checkAuthorization($jwt);
		$this->connect();
	}

	function checkAuthorization($jwt)
	{
		require_once __DIR__.'/vendor/autoload.php';
		require_once __DIR__.'/db_config.php';

		if(!isset($jwt))
		{
			echo 'HTTP/1.0 400 Bad Request';
			exit;
		}

		$secretKey  = DB_SECRET_KEY;
		$token = JWT::decode($jwt, $secretKey, ['HS512']);

		$now = new DateTimeImmutable();
		$serverName = DB_DATABASE;

		if ($token->iss !== $serverName ||
		    $token->nbf > $now->getTimestamp() ||
		    $token->exp < $now->getTimestamp())
		{
			echo 'HTTP/1.1 401 Unauthorized';
			exit;
		}
		$auth = $token->auth;
		$this->isDev = ( strcmp($auth,'dev')==0 );
		$this->id = $token->userID;
	}

	function connect()
	{
		//import database connection variables
		require_once __DIR__.'/db_config.php';

		//Try connecting to mysql database

		try
		{
			$this->db = new PDO('mysql:host='.DB_SERVER.';dbname='.DB_DATABASE.';port='. 443, DB_USER, DB_PASSWORD);
			//set the PDO error mode to exception
			$this->db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
		} catch(PDOException $e)
		{
			echo 'Connection failed : '.$e->getMessage();
			exit;
		}
	}


	function close()
	{
		$this->db=null;
	}

	function getDB()
	{
		return $this->db;
	}

	function getID()
	{
		return $this->id;
	}

	function isDev()
	{
		return $this->isDev;
	}
}

?>
