<?php
$str = $_GET["result"];
if($str != null) {
  $file = "./result.txt";
  $fh = fopen($file, 'w');
  fwrite($fh, $str);
  fclose($fh);
  echo "Success";
} else {
  echo "Fail";
}
?>
Test
<form method=get>
<input type=text name="result" value="value1">
</form>
