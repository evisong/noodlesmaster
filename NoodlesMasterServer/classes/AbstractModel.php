<?php
abstract class AbstractModel {

    const DATA_ROOT = "../data/";
    const FILE_SUFFIX = ".txt";

    public $uuid;
    public $last_update;
    public $last_update_user;
    
    public function read_list($parent_uuid = NULL) {
        $objs = array();
        $data_path = $this->get_data_path();
        
        $d = dir($data_path);
        while (false !== ($entry = $d->read())) {
            if (stripos($entry, self::FILE_SUFFIX) !== FALSE) {
                $content = file_get_contents($data_path.$entry);
                $obj = unserialize($content);
                if ($obj) {
                    $objs[] = $obj;
                }
            }
        }
        $d->close();
        
        return $objs;
    }
    
    public function read($obj_uuid) {
        $obj = NULL;
        $target_path = $this->get_data_path().$obj_uuid.self::FILE_SUFFIX;
        
        if (file_exists($target_path)) {
            $content = file_get_contents($target_path);
            $obj = unserialize($content);
        }
        
        return $obj;
    }
    
    public function create() {
        $this->validate_model();
        
        $target_path = $this->get_data_path().$this->uuid.self::FILE_SUFFIX;
        
        if (!file_exists($target_path)) {
            $this->set_last_update();
            $file = fopen($target_path, 'w');
            fwrite($file, serialize($this));
            fclose($file);
        }
    }
    
    public function update() {
        $this->validate_model();
        
        $target_path = $this->get_data_path().$this->uuid.self::FILE_SUFFIX;
        
        if (!file_exists($target_path)) {
            throw new ModelException("所更新的对象不存在");
        } else {
            $this->set_last_update();
            $file = fopen($target_path, 'w');
            fwrite($file, serialize($this));
            fclose($file);
        }
    }
    
    protected abstract function validate_model();
    
    public function to_json() {
        return "";
    }
    
    private function get_data_path() {
        return self::DATA_ROOT.DIRECTORY_SEPARATOR.strtolower(get_class($this)).DIRECTORY_SEPARATOR;
    }
    
    private function set_last_update() {
        $this->last_update = time();
        //$username = $_SESSION["user"]["name"];
        //$username = $username ? $username : "admin";
        $this->last_update_user = "admin";
    }
}
?>