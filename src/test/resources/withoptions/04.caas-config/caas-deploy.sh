oc apply -f caas-config.yaml

# trigger rolling update of pods
oc patch deployment ${ROUTER_INSTANCE} --patch="{\"spec\":{\"template\":{\"metadata\":{\"labels\":{\"date\":\"`date +'%s'`\"}}}}}"
