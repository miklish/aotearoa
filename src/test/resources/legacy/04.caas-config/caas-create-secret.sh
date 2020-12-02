oc project ${OS_PROJECT}

oc delete secret ${ROUTER_INSTANCE}-config-secret
oc create secret generic ${ROUTER_INSTANCE}-config-secret --from-file=../05.final-service-out
