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
<title>修改产品 -【泡面管家】泡面大全</title>
<?php include '../include/htmlhead.php'; ?>
</head>
<body>
<?php include '../include/header.php'; ?>
<div id="page-wrap">
<h1>修改产品</h1>
<a class="cancel" href="<?php echo urldecode($_GET['from']) ?>">撤销</a>
<form action="controller.php?from=<?php echo $_GET['from'] ?>" method="post">
	<div><label for="name">名称</label><input id="name" type="text" name="name" value="<?php echo $noodles->name ?>" /></div>
	<div><label for="net_weight">净重</label><input id="net_weight" type="text" name="net_weight" value="<?php echo $noodles->net_weight ?>" /></div>
	<div><label for="noodles_weight">面饼重</label><input id="noodles_weight" type="text" name="noodles_weight" value="<?php echo $noodles->noodles_weight ?>" /></div>
	<div><label for="step_1_uuid">第一步</label><input id="step_1_uuid" type="text" name="step_1_uuid" value="<?php echo $noodles->step_1_uuid ?>" /></div>
	<div><label for="step_2_uuid">第二步</label><input id="step_2_uuid" type="text" name="step_2_uuid" value="<?php echo $noodles->step_2_uuid ?>" /></div>
	<div><label for="step_3_uuid">第三步</label><input id="step_3_uuid" type="text" name="step_3_uuid" value="<?php echo $noodles->step_3_uuid ?>" /></div>
	<div><label for="step_4_uuid">第四步</label><input id="step_4_uuid" type="text" name="step_4_uuid" value="<?php echo $noodles->step_4_uuid ?>" /></div>
	<div><label for="soakage_time">等待时间</label><input id="soakage_time" type="text" name="soakage_time" value="<?php echo $noodles->soakage_time ?>" /></div>
	<div><label for="description">描述</label><input id="description" type="text" name="description" value="<?php echo $noodles->description ?>" /></div>
	<div><label for="nutrients">营养成分</label><input id="nutrients" type="text" name="nutrients" value="<?php echo $noodles->nutrients ?>" /></div>
	<div><label for="logo">Logo</label><input id="logo" type="text" name="logo" value="<?php echo $noodles->logo ?>" /></div>
	<div>
		<label></label>
    	<input type="submit" value="修改" />
    	<input type="reset" value="重置" />
    	<input type="hidden" name="uuid" value="<?php echo $noodles->uuid ?>" />
    	<input type="hidden" name="brand_uuid" value="<?php echo $noodles->brand_uuid ?>" />
    	<input type="hidden" name="action" value="update" />
	</div>
</form>
</div>
<?php include '../include/footer.php'; ?>
</body>
</html>