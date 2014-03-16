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
<title><?php echo $brand->name ?> -【泡面管家】泡面大全</title>
<?php include '../include/htmlhead.php'; ?>
</head>
<body>
<?php include '../include/header.php'; ?>
<div id="page-wrap">
    <h1><?php echo $noodles->name ?></h1>
    <a class="edit" href="edit.php?uuid=<?php echo $noodles->uuid ?>&from=<?php echo urlencode($_SERVER['REQUEST_URI']) ?>">修改</a>
    
	<div><label for="name">名称</label><span id="name"><?php echo $noodles->name ?></span></div>
	<div><label for="net_weight">净重</label><span id="net_weight"><?php echo $noodles->net_weight ?> 克</span></div>
	<div><label for="noodles_weight">面饼重</label><span id="noodles_weight"><?php echo $noodles->noodles_weight ?> 克</span></div>
	<div><label for="step_1_uuid">第一步</label><span id="step_1_uuid"><?php echo $noodles->step_1_uuid ?></span></div>
	<div><label for="step_2_uuid">第二步</label><span id="step_2_uuid"><?php echo $noodles->step_2_uuid ?></span></div>
	<div><label for="step_3_uuid">第三步</label><span id="step_3_uuid"><?php echo $noodles->step_3_uuid ?></span></div>
	<div><label for="step_4_uuid">第四步</label><span id="step_4_uuid"><?php echo $noodles->step_4_uuid ?></span></div>
	<div><label for="soakage_time">等待时间</label><span id="soakage_time"><?php echo $noodles->soakage_time ?> 秒</span></div>
	<div><label for="description">描述</label><span id="description"><?php echo $noodles->description ?></span></div>
	<div><label for="nutrients">营养成分</label><span id="nutrients"><?php echo $noodles->nutrients ?></span></div>
	<div><label for="logo">Logo</label><span id="logo"><?php echo $noodles->logo ?></span></div>
</div>
<?php include '../include/footer.php'; ?>
</body>
</html>