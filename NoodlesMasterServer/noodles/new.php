<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>添加产品</title>
<link rel="stylesheet" href="../include/style.css" type="text/css" />
</head>
<body>
<h1>添加产品</h1>
<form action="controller.php?from=<?php echo $_GET['from'] ?>" method="post">
	<div><label for="name">名称</label><input id="name" type="text" name="name" /></div>
	<div><label for="net_weight">净重</label><input id="net_weight" type="text" name="net_weight" /></div>
	<div><label for="noodles_weight">面饼重</label><input id="noodles_weight" type="text" name="noodles_weight" /></div>
	<div><label for="step_1_uuid">第一步</label><input id="step_1_uuid" type="text" name="step_1_uuid" /></div>
	<div><label for="step_2_uuid">第二步</label><input id="step_2_uuid" type="text" name="step_2_uuid" /></div>
	<div><label for="step_3_uuid">第三步</label><input id="step_3_uuid" type="text" name="step_3_uuid" /></div>
	<div><label for="step_4_uuid">第四步</label><input id="step_4_uuid" type="text" name="step_4_uuid" /></div>
	<div><label for="soakage_time">等待时间</label><input id="soakage_time" type="text" name="soakage_time" /></div>
	<div><label for="description">描述</label><input id="description" type="text" name="description" /></div>
	<div><label for="nutrients">营养成分</label><input id="nutrients" type="text" name="nutrients" /></div>
	<div><label for="logo">Logo</label><input id="logo" type="text" name="logo" /></div>
	<div>
    	<label></label>
    	<input type="submit" value="创建" />
    	<input type="hidden" name="brand_uuid" value="<?php echo $_GET['brand_uuid'] ?>" />
    	<input type="hidden" name="action" value="create" />
	</div>
</form>
</body>
</html>