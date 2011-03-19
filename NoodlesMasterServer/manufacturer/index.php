<?php
include '../include/common.php';

$manufacturer_model = new Manufacturer();
$manufacturers = $manufacturer_model->read_list();

$selected_id = $_GET["uuid"];
if (!$manufacturer_model->read($selected_id)) {
    $selected_id = NULL;
}

$brands;
if ($selected_id) {
    $brand_model = new Brand();
    $brands = $brand_model->read_list($selected_id);
}
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>生产厂家</title>
<link rel="stylesheet" href="../include/style.css" type="text/css" />
</head>
<body>
<h1>生产厂家</h1>
<ul>
<?php foreach ($manufacturers as $manufacturer) { ?>
    <li<?php if ($manufacturer->uuid == $selected_id) echo ' class="selected"' ?>>
    	<div>名称：<?php echo $manufacturer->name ?></div>
    	<div>Logo：<?php echo $manufacturer->logo ?></div>
    	<div>
    		<a href="?uuid=<?php echo $manufacturer->uuid ?>">选择</a>
    		<a href="edit.php?uuid=<?php echo $manufacturer->uuid ?>&from=<?php echo urlencode($_SERVER['REQUEST_URI']) ?>">修改</a>
    	</div>
    </li>
<?php } ?>
</ul>
<a href="new.php?from=<?php echo urlencode($_SERVER['REQUEST_URI']) ?>">添加厂家</a>
<?php if ($selected_id) { ?>
<h1>拥有品牌</h1>
<a href="../brand/new.php?manufacturer_uuid=<?php echo $selected_id ?>&from=<?php echo urlencode($_SERVER['REQUEST_URI']) ?>">添加品牌</a>
<ul>
<?php     foreach ($brands as $brand) { ?>
    <li>
    	<div>名称：<?php echo $brand->name ?></div>
    	<div>Logo：<?php echo $brand->logo ?></div>
    	<div>
    		<a href="../brand/index.php?uuid=<?php echo $brand->uuid ?>">查看产品目录</a>
    		<a href="../brand/edit.php?uuid=<?php echo $brand->uuid ?>&from=<?php echo urlencode($_SERVER['REQUEST_URI']) ?>">修改</a>
    	</div>
    </li>
<?php     } ?>
</ul>
<?php } ?>
</body>
</html>