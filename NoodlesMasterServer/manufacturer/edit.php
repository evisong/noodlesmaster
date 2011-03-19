<?php
include '../include/common.php';

$selected_id = $_GET["uuid"];
$manufacturer_model = new Manufacturer();
$manufacturer = $manufacturer_model->read($selected_id);

if (!$manufacturer) {
    redirect_to_from();
}
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>修改生产厂家</title>
<link rel="stylesheet" href="../include/style.css" type="text/css" />
</head>
<body>
<h1>修改生产厂家</h1>
<form action="controller.php?from=<?php echo $_GET['from'] ?>" method="post">
	<div><label for="name">名称</label><input id="name" type="text" name="name" value="<?php echo $manufacturer->name ?>" /></div>
	<div><label for="logo">Logo</label><input id="logo" type="text" name="logo" value="<?php echo $manufacturer->logo ?>" /></div>
	<div>
		<label></label>
    	<input type="submit" value="修改" />
    	<input type="reset" value="重置" />
    	<input type="hidden" name="uuid" value="<?php echo $manufacturer->uuid ?>" />
    	<input type="hidden" name="action" value="update" />
	</div>
</form>
</body>
</html>