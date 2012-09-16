<?php
$str = $_GET['command'];
if($str != null) {
  $file = "./command.txt";
  $fh = fopen($file, 'w');
  fwrite($fh, $str);
  fclose($fh);
  echo "command set to ".htmlspecialchars($str);
} else {
  echo "<form method=\"get\">Command: <input type=\"text\" name=\"command\" size=100><input type=Submit></form>";
}
?>
