<?php
define('DS', DIRECTORY_SEPARATOR);
define('DOCROOT', realpath(dirname(__FILE__)).DS);

echo "Hello, evis." . "<br />";
echo $_SERVER['HTTP_USER_AGENT'] . "<br />";
echo strpos("ABCDEFG", "E") . "<br />";
$arr = array(d => "dafda", e => 313);
echo serialize($arr);
?>
<form action="." method="post">
	<div><input type="text" name="" /></div>
</form>