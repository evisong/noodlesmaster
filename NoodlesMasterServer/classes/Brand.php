<?php
#require '../model/abstract_model.php';

class Brand extends AbstractModel {
    public $manufacturer_uuid;
    public $name;
    public $logo;
    
    public function read_list($parent_uuid = NULL) {
        $objs = array();
        $all_objs = parent::read_list();
        if ($parent_uuid == NULL) {
            $objs = $all_objs;
        } else {
            foreach ($all_objs as $obj) {
                if ($obj->$manufacturer_uuid == $parent_uuid) {
                    $objs[] = $obj;
                }
            }
        }
        return $objs;
    }
}
?>