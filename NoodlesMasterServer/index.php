<?php
session_start();
$_SESSION["user"] =  "Webmaster";

$url="manufacturer/index.php";
header("location:" . $url);
?>