<?php
#require '../model/abstract_model.php';

class Noodles extends AbstractModel {
    public $brand_uuid;
    public $name;
    public $net_weight; // integer
    public $noodles_weight; // integer
	public $step_1_uuid;
	public $step_2_uuid;
	public $step_3_uuid;
	public $step_4_uuid;
	public $soakage_time; // integer, seconds
	public $description;
	public $nutrients; // array
    public $logo;
    
    public function read_list($parent_uuid = NULL) {
        $objs = array();
        $all_objs = parent::read_list();
        if ($parent_uuid == NULL) {
            $objs = $all_objs;
        } else {
            foreach ($all_objs as $obj) {
                if ($obj->brand_uuid == $parent_uuid) {
                    $objs[] = $obj;
                }
            }
        }
        return $objs;
    }
    
    protected function validate_model() {
        $brand_model = new Brand();
        $brand = $brand_model->read($this->brand_uuid);
        if (!$brand) {
            throw new ModelException("对应的品牌不存在");
        }
    }
}
?>