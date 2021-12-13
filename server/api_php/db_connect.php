<?php
//class to connect to database
class DatabaseConnect
{
	protected $db;

	function __construct()
	{
		$this->connect();
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
		}
	}
	function close()
	{
		//closing db connection
		$this->db=null;
	}
	function getDB()
	{
		return $this->db;
	}
}

?>
