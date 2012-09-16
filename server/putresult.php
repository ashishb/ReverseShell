<?php
$str = $_GET["result"];
if($str != null) {
  $file = "./result.txt";
  $fh = fopen($file, 'w');
  fwrite($fh, $str);
  fclose($fh);
  echo "Success";
} else {
  echo "Fail<form method=get>result <input type=text name=\"result\" value=\"value1\"><input type=Submit></form>";
}
?>
