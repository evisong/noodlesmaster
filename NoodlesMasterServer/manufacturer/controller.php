<?php 
include '../include/classes.php';

import_request_variables("p", "reqvar_");
switch ($reqvar_action) {
    case 'create':
        $manufacturer = new Manufacturer();
        $guid = com_create_guid();
        $manufacturer->uuid = substr($guid, 1, strlen($guid) - 2);
        $manufacturer->name = $reqvar_name;
        $manufacturer->logo = $reqvar_logo;
        $manufacturer->create();
        break;
    
    default:
        ;
        break;
}

$from = urldecode($_GET['from']);
$from = $from ? $from : "index.php";
header("location:" . $from);
?>