# AOtearoa
## Overview
Aotearoa is a command line tool that allows Application Operations teams generate complex configurations for an application while having little to no knowledge of the application's configuration system beyond the few values they are entrusted to provide (e.g.: production credentials, certificates, private keys, etc...).

## Template files
AOtearoa starts with template files. A template is a normal text configuration file that contains 'tags' in place 

Creation of yaml or json files requires:
* configuration templates
* values to inject into the templates

Configuration templates are simply regular configuration files with 'tags'. These tags are replaced with actual values. A template whose tags have been replaced with actual values is said to be 'resolved'.

Below is an example of a configuration template:


In addition to resolving configuration files, the tool can also create keystores from certificates. To create 
