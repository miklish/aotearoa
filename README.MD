# AOtearoa
## Overview
Aotearoa is a command line tool that allows Application Operations teams to generate complex configurations for an application while having little to no knowledge of the application's configuration system beyond the few values they are entrusted to provide (e.g.: production credentials, certificates, private keys, etc...).

## Template, and Value files
AOtearoa starts with template files. A template is a normal text configuration file that contains 'tags' in which values will be inserted.

##### security.yml
```yaml
# Enable JWT verification flag.
enableVerifyJwt: {{jwt_security}}
...
# JWT signature public certificates. kid and certificate path mappings.
jwt:
  certificate:
    '100': {{oauth_certificate}}
  clockSkewInSeconds: 60
...
```

##### datasource.yml
```yaml
PostgresDataSource:
  DriverClassName: org.postgresql.ds.PGSimpleDataSource
  jdbcUrl: jdbc:postgresql://{{database_urls}}/users?ssl=true&sslfactory=com.biz.SSLSocketFactory
...
```

##### secret.yml
```yaml
...
tableauPassword: {{tableau_password}}
...
```

The tags `{{jwt_security}}`, `{oauth_certificate}`, `{{postgres_servers}}`, and `{{tableau_password}}` take their values from a 'values' file:

##### dev-values.yml
```yaml
jwt_security:
  - false
database_urls:  
  - dev-postgres-01.biz.com:9000
  - dev-postgres-02.biz.com:9000
oauth_certificate:
  - oauth-dev.pem
tableau_password:
  - devpassword
```

##### prod-values.yml
```yaml
jwt_security:
  - true
database_urls:  
  - tss-postgres-01.biz.com:7373
  - tss-postgres-02.biz.com:7373
  - tss-postgres-03.biz.com:7390  
oauth_certificate:
  - oauth.pem
tableau_password:
  - password  
```
 
As you can see in the examples above, you can use different value files for different environments. 

# Metadata files
In addition to templates and values, you must provide a metadata file that describes the values to be injected.

```yaml
variables:
  jwt_security:
    min: 1
    max: 1
    output:
    - copy
    type: string
    prompt-text: Enable 'true', or disable 'false' JWT verification security
    defaults:
    - true
    files:
    - security
    
  database_urls:
    min: 1
    max: inf
    output:
    - comma-separated
    type: string
    prompt-text: Provide a list of one or more urls for the database
    files:
    - datasource    

  oauth_certificate:
    min: 1
    max: 1
    output:
    - copy
    type: string
    prompt-text: Enter the name of the OAuth (Auth Server) certificate
    defaults:
    - cert.pem
    files:
    - security

  tableau_password:
    min: 1
    max: 1
    output:
    - encrypt
    type: string
    prompt-text: Tableau password (this will be encrypted)
    files:
    - secret
```
  
Note that you specify the template file(s) that each value should be injected into, in the file section of the value's metadata entry. If the file name ends in '.yml', then you may omit the extension (e.g.: a file entry of 'secret' will cause aotearo to scan the file 'secret.yml' in the template folder). However, if you need to inject values into a file that ends in an extension that is different than '.yml', you can put in the full filename (e.g.: you could enter 'logback.xml' if you need to inject values into a template for slf4j). 

##### Defaults
Also note that you can set default values in the metadata yaml. These will show up in prompts and web forms, and will be used in cases where values are not entered in values file.

# Values
There are numerous ways in which values can be specified to the template resolver.

#### Values file
Below see our example production value file.

```yaml
jwt_security:
  - true
database_urls:  
  - tss-postgres-01.biz.com:7373
  - tss-postgres-02.biz.com:7373
  - tss-postgres-03.biz.com:7390
oauth_certificate:
  - oauth.pem
tableau_password:
  - password
```
 
All values must be entered as elements of a Yaml list (even if there is only a single entry).

If a variable is specified in the metadata yaml but has no value in the values file, then this will cause an error -- unless a default value is specified in the metadata yaml. 

##### Transforms
The metadata file allows the input to be transformed. In the current version, 3 types of transforms are supported:

- `copy`
- `comma-separated`
- `encrypt`

The `copy` transform simply pastes the value in as-is.

The `encrypt` transform will encrypt the password using Light4j's default symmetric encryption. E.g.: The tableau password (set as password above) will appear as `CRYPT:iR7QkKyNDJ4wTRn9BEXROgm2iAkajmIv2ZR5VOsxMqs=` in secret.yml.

The `comma-separated` transform concatenates elements in the list and separates them with commas. E.g.: the list
```yaml
  - value1
  - value2
  - value3
```
will be converted to `value1,value2,value3` prior to being inserted.

#### Command line prompts
If command line prompts are chosen as the method to input values, then on the command line, the user would see the following:  
  
<pre><code>
Type \\q to quit. Type \\d to use default (if default exists)
[Enable 'true', or disable 'false' JWT verification security : (true)]: <b><i>\\d</i></b>
 >> value selected is 'true'

Next entry requires between 1 and inf values. Enter \\n to complete.
[Provide a list of one or more urls for the database]: 
[Value 1]: <b><i>tss-postgres-01.biz.com:7373</i></b>
[Value 2]: <b><i>tss-postgres-02.biz.com:7373</i></b>
[Value 3]: <b><i>tss-postgres-03.biz.com:7390</i></b>
[Value 4]: <b><i>\\n</i></b>

[Enter the name of the OAuth (Auth Server) certificate]: <b><i>oauth.pem</i></b>
[Tableau password (this will be encrypted)]: <b><i>password</i></b>
Resolving templates...done.
</pre></code>

As you can see, the prompts that are shown are specified in the metadata file, and the defaults that are shown are also specified in the metadata file.

#### Environment variables
You may also use environment variables as a way to specify variable values. E.g.: prior to running a test, you may wish to temporarily turn off security in your configurations, even though your values file has jwt_security set to 'true':

`$ export jwt_security=false`

#### File values with environment variable overrides

You can also use a value file _and_ environment variables, where environment variables can be used to _*override*_ value file settings to enable you to do testing (e.g.: override security to a 'false' setting, or turn the logging level to 'debug' temporarily) without modifying your production values file.

This will prevent you from having to set environment variables for all your settings, and only specify overrides as environment variables.
 
#### Local web form
Not yet implemented.

# Command line arguments
To run aotearoa, you would run

`$ java -jar aotearoa.jar [arguments...]`

with the appropriate arguments:

<table border="0">
 <tr>
    <td><span pan style="font-size:15px;">Command</td>
    <td><span style="font-size:15px">Description</b></td>
 </tr>
 <tr>
    <td>-h, --help</td>
    <td>Display help/usage information</td>
 </tr>
 <tr>
    <td>-m, --metadata</td>
    <td>Variable metadata file (required)</td>
 </tr> 
 <tr>
    <td>-o, --outputdir</td>
    <td>Output directory (required)</td>
 </tr>
 <tr>
    <td>-v, --values <String></td>
    <td>Value file (optional)</td>
 </tr>  
 <tr>
    <td>-p, --prompts</td>
    <td>Use command line prompts to enter values (optional)</td>
 </tr>
 <tr>
    <td>-e, --env<String></td>
    <td>Get values from environment variables (optional)</td>
 </tr> 
 <tr>
    <td>-s, --server</td>
    <td>Config Server URL (optional)</td>
 </tr>
 <tr>
    <td>-t, --templates</td>
    <td>Template file folder (required)</td>
 </tr>
 <tr>
    <td>-k, --kmetadata <String></td>
    <td>Keystore metadata template (optional)</td>
 </tr>
</table>
 
Some examples are below:
 
##### Use value file
```
$ java -jar aotearoa.jar         \
    -m ./templates/_metadata.yml \ # metadata file
    -t ./templates               \ # template files directory       
    -o ./config-out              \ # output folder
    -v ./templates/_values.yml     # values file
```    
    
##### Use prompts
```
$ java -jar aotearoa.jar         \
    -m ./templates/_metadata.yml \ # metadata file
    -t ./templates               \ # template files directory
    -o ./config-out              \ # output folder
    -p                             # values from prompts
```     

##### Use environment variables
```
$ java -jar aotearoa.jar         \
    -m ./templates/_metadata.yml \ # metadata file
    -t ./templates               \ # template files directory
    -o ./config-out              \ # output folder
    -e                             # values from environment variables
```     

##### Value file with environment variable overrides
```
$ java -jar aotearoa.jar         \
    -m ./templates/_metadata.yml \ # metadata file
    -t ./templates               \ # template files directory
    -o ./config-out              \ # output folder
    -v ./templates/_values.yml   \ # value file...
    -e                             # with environment variable overrides
```     

# Keystores
Aotearoa can also simplify the creation of JKS keystores.

###### Example Keystore YAML:
```yaml
certificates:
  tableau-webserver-cert:                                   # certificate reference name
    format: pem                                             # certificate format
    prompt-text: Tableau webserver public certificate       # prompt text
    filename: {{tableau_cert_filename}}                     # filename of certificate

  postgres-database-cert:                                   # this is an internal reference name for the keystore
    format: pem                                             # certificate format
    prompt-text: Postgres self-signed certificate           # prompt text
    filename: server.pem                                    # filename of certificate

keystores:
  client.truststore:                                        # this will be the output keystore filename
    base-keystore-filename: {{standard_certs}}              # base keystore filename 
    keystore-password: password                             # keystore password
    certificates:                                           # list of certificates to load into keystore
      tableau-webserver-cert: tableau-uat                   # certificate-reference : alias
      postgres-database-cert: postgres-uat
```

The YAML required to build keystores contains two main sections: a `certificates` section and a `keystores` section.

#### Certificates section
The main `certificates` section describes the certificates to be loaded into the keystores that are defined in the main `keystores` section. For each certificate, you will need to provide a cert reference name in order to reference the certificate in the `keystores` section. Beyond that, you also need to provide the cert's format (only pem is currently supported), prompt text, and the cert's file name. The certificate must reside in the template folder. 

#### Keystores section
##### File name
For each keystore, the first item you specify is the keystore file name. This will be the file name of the keystore in output folder.

##### base-keystore-filename (optional)
`base-keystore-filename` is an optional value that can be set if you wish to load certs into an existing keystore rather than create a new keystore from scratch. The existing keystore must reside in the template folder.

##### keystore-password
This is the password to the keystore. If you are creating a new keystore, then this will be the password assigned to the keystore that is created. However, if you have specified that you want to load certs into an existing keystore (by using the `base-keystore-filename` option), then this password must be the password of the existing keystore. 

##### certificates section
This subsection contains the certs you want loaded into the keystore. Each line contains the reference name of a certificate that has been defined in the main certificates section of the yaml, and the alias you want assigned to the certificate within the keystore. The certificate reference name and its alias are separated by a ':' character.

#### Tags are allowed

In the example shown above, you'll notice the existence of tags throughout. Since the keystore yaml file is processed after values are read (either from user prompts, from a value file, web form, config server etc...), these values can be included in the keystore yaml. These values will be injected into the keystore yaml prior to processing of the keystore yaml file. Tags can appear anywhere in the keystore yaml and are totally optional.

#### Example command line
 
###### Build configuration and keystores from templates and value file
```
$ java -jar aotearoa.jar         \
    -m ./templates/_metadata.yml \ # metadata file
    -t ./templates               \ # template files directory       
    -o ./config-out              \ # output folder
    -v ./templates/_values.yml   \ # keystore template file
    -k ./templates/_keystore.yml 
```    
    
