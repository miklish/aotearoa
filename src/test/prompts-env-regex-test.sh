#!/usr/bin/env bash

java -cp ../../target/ao-jar-with-dependencies.jar com.christoff.aotearoa.bridge.ValueInjectCLIStart \
--metadata /Users/christof/localrepo/aotearoa/src/main/resources/config2/_metadata.yml \
--templates /Users/christof/localrepo/aotearoa/src/main/resources/config2/ \
--outputdir /Users/christof/localrepo/aotearoa/src/main/resources/config2-out/ \
--prompts \
--regex "\\$\{(.*?)\}"    # For Shell
#--regex "\$\{(.*?)\}"     # For IntelliJ
