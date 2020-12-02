echo Logging into OpenShift...
oc login ${OS_URL} --username=${OS_USERNAME}
echo Switching to project ${OS_PROJECT}
oc project ${OS_PROJECT}
