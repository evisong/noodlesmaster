<?php
include '../include/common.php';

$selected_id = $_GET["uuid"];
$brand_model = new Brand();
$brand = $brand_model->read($selected_id);

if (!$brand) {
    redirect_to_from();
}

$noodles_model = new Noodles();
$noodleses = $noodles_model->read_list($selected_id);
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title><?php echo $brand->name ?> -【泡面管家】泡面大全</title>
<?php include '../include/htmlhead.php'; ?>
</head>
<body>
<?php include '../include/header.php'; ?>
<div id="page-wrap">
<h1><?php echo $brand->name ?></h1>
<a class="edit" href="edit.php?uuid=<?php echo $brand->uuid ?>&from=<?php echo urlencode($_SERVER['REQUEST_URI']) ?>">修改</a>
<div><?php echo $brand->logo ?></div>
<h1>产品</h1>
<a class="add" href="../noodles/add.php?brand_uuid=<?php echo $selected_id ?>&from=<?php echo urlencode($_SERVER['REQUEST_URI']) ?>">添加产品</a>
<ul>
<?php foreach ($noodleses as $noodles) { ?>
    <li>
		<a href="../noodles/index.php?uuid=<?php echo $noodles->uuid ?>" title="查看产品"><?php echo $noodles->name ?></a> <a href="../noodles/edit.php?uuid=<?php echo $noodles->uuid ?>&from=<?php echo urlencode($_SERVER['REQUEST_URI']) ?>">[修改]</a>
    </li>
<?php } ?>
</ul>
</div>
<?php include '../include/footer.php'; ?>
</body>
</html>