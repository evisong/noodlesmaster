<?php
function __autoload($classname) {
    require_once "../classes/" . $classname . ".php";
}
?>