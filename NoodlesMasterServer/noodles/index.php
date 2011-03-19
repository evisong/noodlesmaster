<?php
include '../include/common.php';

$selected_id = $_GET["uuid"];
$noodles_model = new Noodles();
$noodles = $noodles_model->read($selected_id);

if (!$noodles) {
    redirect_to_from();
}
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title><?php echo $brand->name ?></title>
<link rel="stylesheet" href="../include/style.css" type="text/css" />
</head>
<body>
<h1><?php echo $noodles->name ?></h1>
<div><?php echo $noodles->logo ?></div>
<div>
	<a href="edit.php?uuid=<?php echo $noodles->uuid ?>&from=<?php echo urlencode($_SERVER['REQUEST_URI']) ?>">修改</a>
</div>
</body>
</html>