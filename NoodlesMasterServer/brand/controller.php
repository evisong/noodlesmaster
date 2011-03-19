<?php 
include '../include/common.php';

import_request_variables("p", "reqvar_");
switch ($reqvar_action) {
    case 'create':
        $brand = new Brand();
        $brand->uuid = generate_uuid();
        $brand->manufacturer_uuid = $reqvar_manufacturer_uuid;
        $brand->name = $reqvar_name;
        $brand->logo = $reqvar_logo;
        $brand->create();
        break;
    
    case 'update':
        $brand = new Brand();
        $brand->uuid = $reqvar_uuid;
        $brand->manufacturer_uuid = $reqvar_manufacturer_uuid;
        $brand->name = $reqvar_name;
        $brand->logo = $reqvar_logo;
        $brand->update();
        break;
        
    default:
        ;
        break;
}

redirect_to_from();
?>