@echo off
FOR /f "skip=1" %%x in ('wmic os get localdatetime') do if not defined MyDate set MyDate=%%x
FOR /f %%x in ('wmic path win32_localtime get /format:list ^| findstr "="') do set %%x
SET fmonth=00%Month%
SET fday=00%Day%
SET mydate=%Year%%fmonth:~-2%%fday:~-2%
SET timestamp=v0.0.0-%mydate%%mytime%
echo Publishing with time stamp : %timestamp% >> push_outputs.txt
docker tag mic-collectif 10.3.4.18:5000/mic-collectif:%timestamp% >> push_outputs.txt
docker push 10.3.4.18:5000/mic-collectif:%timestamp% >> push_outputs.txt