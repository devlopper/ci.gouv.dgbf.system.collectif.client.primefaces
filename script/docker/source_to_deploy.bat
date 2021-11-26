cd ..
cd maven
call install.bat
call package.bat

cd..
cd docker
call build.bat
call push_and_deploy.bat