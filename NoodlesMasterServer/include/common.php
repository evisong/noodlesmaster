<?php
function __autoload($classname) {
    require_once "../classes/" . $classname . ".php";
}

function redirect_to_from() {
    $from = urldecode($_GET['from']);
    $from = $from ? $from : "index.php";
    header("location:" . $from);
}

function generate_uuid() {
    $guid = com_create_guid();
    return substr($guid, 1, strlen($guid) - 2);
}
?>