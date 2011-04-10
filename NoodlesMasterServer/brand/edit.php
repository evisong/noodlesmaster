<?php
include '../include/common.php';

$selected_id = $_GET["uuid"];
$brand_model = new Brand();
$brand = $brand_model->read($selected_id);

if (!$brand) {
    redirect_to_from();
}
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>修改品牌 -【泡面管家】泡面大全</title>
<?php include '../include/htmlhead.php'; ?>
</head>
<body>
<?php include '../include/header.php'; ?>
<div id="page-wrap">
<h1>修改品牌</h1>
<a class="cancel" href="<?php echo urldecode($_GET['from']) ?>">撤销</a>
<form action="controller.php?from=<?php echo $_GET['from'] ?>" method="post">
	<div><label for="name">名称</label><input id="name" type="text" name="name" value="<?php echo $brand->name ?>" /></div>
	<div><label for="logo">Logo</label><input id="logo" type="text" name="logo" value="<?php echo $brand->logo ?>" /></div>
	<div>
		<label></label>
    	<input type="submit" value="修改" />
    	<input type="reset" value="重置" />
    	<input type="hidden" name="uuid" value="<?php echo $brand->uuid ?>" />
    	<input type="hidden" name="manufacturer_uuid" value="<?php echo $brand->manufacturer_uuid ?>" />
    	<input type="hidden" name="action" value="update" />
	</div>
</form>
</div>
<?php include '../include/footer.php'; ?>
</body>
</html>