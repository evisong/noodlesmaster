<?php 
include '../include/common.php';

import_request_variables("p", "reqvar_");
switch ($reqvar_action) {
    case 'create':
        $noodles = new Noodles();
        $noodles->uuid = generate_uuid();
        $noodles->brand_uuid = $reqvar_brand_uuid;
        $noodles->name = $reqvar_name;
        $noodles->net_weight = $reqvar_net_weight;
        $noodles->noodles_weight = $reqvar_noodles_weight;
        $noodles->step_1_uuid = $reqvar_step_1_uuid;
        $noodles->step_2_uuid = $reqvar_step_2_uuid;
        $noodles->step_3_uuid = $reqvar_step_3_uuid;
        $noodles->step_4_uuid = $reqvar_step_4_uuid;
        $noodles->soakage_time = $reqvar_soakage_time;
        $noodles->description = $reqvar_description;
        $noodles->nutrients = $reqvar_nutrients; 
        $noodles->logo = $reqvar_logo;
        $noodles->create();
        break;
    
    case 'update':
        $noodles = new Noodles();
        $noodles->uuid = $reqvar_uuid;
        $noodles->brand_uuid = $reqvar_brand_uuid;
        $noodles->name = $reqvar_name;
        $noodles->net_weight = $reqvar_net_weight;
        $noodles->noodles_weight = $reqvar_noodles_weight;
        $noodles->step_1_uuid = $reqvar_step_1_uuid;
        $noodles->step_2_uuid = $reqvar_step_2_uuid;
        $noodles->step_3_uuid = $reqvar_step_3_uuid;
        $noodles->step_4_uuid = $reqvar_step_4_uuid;
        $noodles->soakage_time = $reqvar_soakage_time;
        $noodles->description = $reqvar_description;
        $noodles->nutrients = $reqvar_nutrients; 
        $noodles->logo = $reqvar_logo;
        $noodles->update();
        break;
        
    default:
        ;
        break;
}

redirect_to_from();
?>