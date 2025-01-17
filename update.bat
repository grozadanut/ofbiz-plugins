set OFBIZ_PLUGINS_PATH=C:\Users\Danut\git\ofbiz-plugins
set OFBIZ_FRAMEWORK_PATH=C:\Users\Danut\git\ofbiz-framework

xcopy %OFBIZ_PLUGINS_PATH%\assetmaint %OFBIZ_FRAMEWORK_PATH%\plugins\assetmaint\ /E /Y
xcopy %OFBIZ_PLUGINS_PATH%\ecommerce %OFBIZ_FRAMEWORK_PATH%\plugins\ecommerce\ /E /Y
xcopy %OFBIZ_PLUGINS_PATH%\lucene %OFBIZ_FRAMEWORK_PATH%\plugins\lucene\ /E /Y
xcopy %OFBIZ_PLUGINS_PATH%\msggateway %OFBIZ_FRAMEWORK_PATH%\plugins\msggateway\ /E /Y
xcopy %OFBIZ_PLUGINS_PATH%\myportal %OFBIZ_FRAMEWORK_PATH%\plugins\myportal\ /E /Y
xcopy %OFBIZ_PLUGINS_PATH%\passport %OFBIZ_FRAMEWORK_PATH%\plugins\passport\ /E /Y
xcopy %OFBIZ_PLUGINS_PATH%\pricat %OFBIZ_FRAMEWORK_PATH%\plugins\pricat\ /E /Y
del /S /Q %OFBIZ_FRAMEWORK_PATH%\plugins\pricat\webapp\pricatdemo
rmdir /S /Q %OFBIZ_FRAMEWORK_PATH%\plugins\pricat\webapp\pricatdemo
xcopy %OFBIZ_PLUGINS_PATH%\projectmgr %OFBIZ_FRAMEWORK_PATH%\plugins\projectmgr\ /E /Y
xcopy %OFBIZ_PLUGINS_PATH%\rest-api %OFBIZ_FRAMEWORK_PATH%\plugins\rest-api\ /E /Y
xcopy %OFBIZ_PLUGINS_PATH%\bi %OFBIZ_FRAMEWORK_PATH%\plugins\bi\ /E /Y
xcopy %OFBIZ_PLUGINS_PATH%\colibri-legacy %OFBIZ_FRAMEWORK_PATH%\plugins\colibri-legacy\ /E /Y