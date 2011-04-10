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
<title>生产厂家 -【泡面管家】泡面大全</title>
<?php include '../include/htmlhead.php'; ?>
</head>
<body>
<?php include '../include/header.php'; ?>
<div id="page-wrap">
<h1>生产厂家</h1>
<a class="add" href="add.php?from=<?php echo urlencode($_SERVER['REQUEST_URI']) ?>">添加厂家</a>
<ul class="manufacturer">
<?php foreach ($manufacturers as $manufacturer) { ?>
    <li<?php if ($manufacturer->uuid == $selected_id) echo ' class="selected"' ?>>
    	<a href="?uuid=<?php echo $manufacturer->uuid ?>">
        	<div class="logo"><img src="../data/manufacturer/logo/<?php echo $manufacturer->logo ?>"/></div>
        	<div class="name"><?php echo $manufacturer->name ?></div>
		</a>
    	<div>
    		<a href="edit.php?uuid=<?php echo $manufacturer->uuid ?>&from=<?php echo urlencode($_SERVER['REQUEST_URI']) ?>">[修改]</a>
    	</div>
    </li>
<?php } ?>
</ul>
<?php if ($selected_id) { ?>
<h1>拥有品牌</h1>
<a class="add" href="../brand/add.php?manufacturer_uuid=<?php echo $selected_id ?>&from=<?php echo urlencode($_SERVER['REQUEST_URI']) ?>">添加品牌</a>
<ul>
<?php     foreach ($brands as $brand) { ?>
    <li>
    	<a href="../brand/index.php?uuid=<?php echo $brand->uuid ?>" title="查看产品目录"><?php echo $brand->name ?></a> <a href="../brand/edit.php?uuid=<?php echo $brand->uuid ?>&from=<?php echo urlencode($_SERVER['REQUEST_URI']) ?>">[修改]</a>
    </li>
<?php     } ?>
</ul>
<?php } ?>
</div>
<?php include '../include/footer.php'; ?>
</body>
</html>