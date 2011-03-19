<?php
include '../include/classes.php';

$manufacturer = new Manufacturer();
$manufacturers = $manufacturer->read_list();
$selected_id = $_GET["id"];
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>生产厂家</title>
<link rel="stylesheet" href="../include/style.css" type="text/css" />
</head>
<body>
<ul>
<?php foreach ($manufacturers as $manufacturer) { ?>
    <li<?php if ($manufacturer->uuid == $selected_id) echo ' class="selected"' ?>>
    	<div>名称：<?php echo $manufacturer->name ?></div>
    	<div>Logo：<?php echo $manufacturer->logo ?></div>
    	<div>
    		<a href="?id=<?php echo $manufacturer->uuid ?>">选择</a>
    		<a href="edit.php?id=<?php echo $manufacturer->uuid ?>">修改</a>
    	</div>
    </li>
<?php } ?>
</ul>
<a href="new.php?from=<?php echo urlencode($_SERVER['REQUEST_URI']) ?>">添加厂家</a>
</body>
</html>