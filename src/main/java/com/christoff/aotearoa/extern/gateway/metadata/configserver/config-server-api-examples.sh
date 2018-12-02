#!/usr/bin/env bash

# Initial server by add secret key for the service
curl -X POST \
  https://localhost:8443/api/json \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{"host":"lightapi.net","service":"config","action":"initialize-server","version":"0.1.0","data":{"key":"light-config"}}'

# Test server
curl -X POST \
  https://localhost:8443/api/json \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{"host":"lightapi.net","service":"config","action":"create-service","version":"0.1.0", \
       "data":{"serviceId":"config-service-1.1.0001","encryptionAlgorithm":"AES", \
               "templateRepository":"git@github.com:networknt/light-config-server.git", \
               "serviceOwner":"Google","version":"1.1.1","profile":"DEV/DIT","refreshed":false}}'

# Test create config server value: key-value pair:
curl -X POST \
  https://localhost:8443/api/json \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{"host":"lightapi.net","service":"config","action":"create-service-value","version":"0.1.0", \
       "data":{"configServiceId":"00000165904d7f75-0242ac1200030000","key":"server/serviceId","value":"1222222"}}'

# Test config server values: key-value pair array:
curl -X POST \
  https://localhost:8443/api/json \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{"host":"lightapi.net","service":"config","action":"create-service-values","version":"0.1.0", \
       "data":{"configServiceId":"00000165904d7f75-0242ac1200030000","values":[ \
           {"key":"server/enableHttps","value":"true"}, {"key":"server/httpsPort","value":"true"}]}} \
'

# If the config value in to be encrpted in the database create the config server secret values:
curl -X POST \
  https://localhost:8443/api/json \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -H 'Postman-Token: b9803960-e5b2-42c7-afc1-5d071bc57d96' \
  -d '{"host":"lightapi.net","service":"config","action":"create-service-secrets","version":"0.1.0", \
       "data":{"configServiceId":"00000165680f7e16-0242ac1200060000","values":[ \
           {"key":"server/buildNumber","value":"cibcBuild"}, {"key":"server/truststoreName","value":"keytab:112"}]}}'

# Update service
curl -X POST \
  https://localhost:8443/api/json \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{"host":"lightapi.net","service":"config","action":"update-service","version":"0.1.0", \
       "data":{"serviceId":"config-service-1.1.0001","encryptionAlgorithm":"AES", \
               "templateRepository":"https://github.com/chenyan71/light-config-template.git", \
               "serviceOwner":"networknt","version":"1.1.1","profile":"DEV/DIT","refreshed":false}}'

# Update config value
curl -X POST \
  https://localhost:8443/api/json \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{"host":"lightapi.net","service":"config","action":"update-service-value","version":"0.1.0", \
       "data":{"configServiceId":"00000165904d7f75-0242ac1200030000","key":"server/serviceId","value":"fass-22222"}}'

# update config values (list of key-value pairs):
curl -X POST \
  https://localhost:8443/api/json \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{"host":"lightapi.net","service":"config","action":"update-service-values","version":"0.1.0", \
       "data":{"configServiceId":"00000165904d7f75-0242ac1200030000","values":[ \
          {"key":"server/enableHttps","value":"false"}, {"key":"server/httpsPort","value":"false"}]}}'

# update config secret values (list of key-value pairs):
curl -X POST \
  https://localhost:8443/api/json \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -H 'Postman-Token: 278c94c0-59e6-43c6-852e-381b30f6fdf4' \
  -d '{"host":"lightapi.net","service":"config","action":"update-service-secrets","version":"0.1.0", \
       "data":{"configServiceId":"00000165680f7e16-0242ac1200060000","values":[ \
          {"key":"server/buildNumber","value":"cibc-gow-1.1.2"}, {"key":"server/truststoreName","value":"keycache:112"}]}}'

# Query service
curl -X POST \
  https://localhost:8443/api/json \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{"host":"lightapi.net","service":"config","action":"query-service","version":"0.1.0", \
       "data":{"serviceId":"config-service-1.1.0001","profile":"DEV/DIT","version":"1.1.1"}}'

# query config value by specified service id and key:
curl -X POST \
  https://localhost:8443/api/json \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{"host":"lightapi.net","service":"config","action":"query-service-value","version":"0.1.0",    \
       "data":{"configServiceId":"00000165904d7f75-0242ac1200030000","key":"server/truststoreName"}}'

# query config values (config value key-pair list for specified service):
curl -X POST \
  https://localhost:8443/api/json \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{"host":"lightapi.net","service":"config","action":"query-service-values","version":"0.1.0", \
       "data":{"configServiceId":"00000165904d7f75-0242ac1200030000"}}'

# delete service
POST /api/json HTTP/1.1
Host: localhost:8443
Content-Type: application/json
Cache-Control: no-cache
{"host":"lightapi.net","service":"config","action":"delete-service","version":"0.1.0", \
 "data":{"serviceId":"config-service-1.1,1","encryptionAlgorithm":"AES", \
         "templateRepository":"git@github.com:networknt/light-config-server.git","version":"1.1.1", \
         "profile":"DEV/DIT","refreshed":false}}'

# delete config value by specified service id and key:
curl -X POST \
  https://localhost:8443/api/json \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{"host":"lightapi.net","service":"config","action":"delete-service-value","version":"0.1.0", \
       "data":{"configServiceId":"00000165680f7e16-0242ac1200060000","key":"server/buildNumber"}}'

# delete config values (delete ALL config values for specified service):
curl -X POST \
  https://localhost:8443/api/json \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{"host":"lightapi.net","service":"config","action":"delete-service-values","version":"0.1.0", \
       "data":{"configServiceId":"00000165680f7e16-0242ac1200060000"}}'

# retrieve config values (config.zip file):
curl -X POST \
  https://localhost:8443/api/json \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{"host":"lightapi.net","service":"config","action":"retrieve-config","version":"0.1.0", \
       "data":{"serviceId":"config-service-1.1.0001","profile":"DEV/DIT","version":"1.1.1"}}'















