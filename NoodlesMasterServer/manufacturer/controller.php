<?php 
include '../include/common.php';

import_request_variables("p", "reqvar_");
switch ($reqvar_action) {
    case 'create':
        $manufacturer = new Manufacturer();
        $manufacturer->uuid = generate_uuid();
        $manufacturer->name = $reqvar_name;
        $manufacturer->logo = $reqvar_logo;
        $manufacturer->create();
        break;
    
    case 'update':
        $manufacturer = new Manufacturer();
        $manufacturer->uuid = $reqvar_uuid;
        $manufacturer->name = $reqvar_name;
        $manufacturer->logo = $reqvar_logo;
        $manufacturer->update();
        break;
        
    default:
        ;
        break;
}

redirect_to_from();
?>