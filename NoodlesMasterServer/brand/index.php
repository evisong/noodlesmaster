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
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title><?php echo $brand->name ?></title>
<link rel="stylesheet" href="../include/style.css" type="text/css" />
</head>
<body>
<h1><?php echo $brand->name ?></h1>
<div><?php echo $brand->logo ?></div>
<div>
	<a href="edit.php?uuid=<?php echo $brand->uuid ?>&from=<?php echo urlencode($_SERVER['REQUEST_URI']) ?>">修改</a>
</div>
<h1>产品</h1>
<a href="../noodles/new.php?brand_uuid=<?php echo $selected_id ?>&from=<?php echo urlencode($_SERVER['REQUEST_URI']) ?>">添加产品</a>
<ul>
<?php foreach ($noodleses as $noodles) { ?>
    <li>
    	<div>名称：<?php echo $noodles->name ?></div>
    	<div>Logo：<?php echo $noodles->logo ?></div>
    	<div>
    		<a href="../noodles/index.php?uuid=<?php echo $noodles->uuid ?>">查看产品</a>
    		<a href="../noodles/edit.php?uuid=<?php echo $noodles->uuid ?>&from=<?php echo urlencode($_SERVER['REQUEST_URI']) ?>">修改</a>
    	</div>
    </li>
<?php } ?>
</ul>
</body>
</html>