<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>添加品牌 -【泡面管家】泡面大全</title>
<?php include '../include/htmlhead.php'; ?>
</head>
<body>
<?php include '../include/header.php'; ?>
<div id="page-wrap">
<h1>添加品牌</h1>
<a class="cancel" href="<?php echo urldecode($_GET['from']) ?>">撤销</a>
<form action="controller.php?from=<?php echo $_GET['from'] ?>" method="post">
	<div><label for="name">名称</label><input id="name" type="text" name="name" /></div>
	<div><label for="logo">Logo</label><input id="logo" type="text" name="logo" /></div>
	<div>
    	<label></label>
    	<input type="submit" value="创建" />
    	<input type="hidden" name="manufacturer_uuid" value="<?php echo $_GET['manufacturer_uuid'] ?>" />
    	<input type="hidden" name="action" value="create" />
	</div>
</form>
</div>
<?php include '../include/footer.php'; ?>
</body>
</html>