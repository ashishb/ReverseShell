<?php
header('Content-Disposition: attachment; filename="the_command.exe"');
$file = "./command.txt";
// First read and then truncate the file.
$fh = fopen($file, 'r');
$data = fread($fh, filesize($file));
echo $data."\n";
fclose($fh);
$fh = fopen($file, 'w');
fclose($fh);
?>
