# AOtearoa 2
# New Release!
Aotearoa 2 is now in testing. AO2 is not fully backwards-compatible with AO1. However, in the updated documentation below, we point out exactly where AO2 differs from AO1 and&mdash;where possible&mdash;provide simple fixes that can be made to your existing scripts so you can leverage AO2, while continuing to use your existing templates, metadata, and values files.

# Overview
AOtearoa is a command line tool that allows Application Operations teams to generate complex configurations for an application while having little to no knowledge of the application's configuration system beyond the few values they are entrusted to provide (e.g.: production credentials, certificates, private keys, etc...).

# Template and Value files
_**Updated for AO2**_

AOtearoa starts with template files. A template is a normal text configuration file that contains 'tokens' in which values will be inserted. Some examples are shown below:

#### security.yml
```yaml
# JWT signature public certificates. kid and certificate path mappings.
jwt:
  clockSkewInSeconds: 60
  certificate:
    '100': ${oauth_certificate}  
...
# Enable JWT verification flag.
enableVerifyJwt: ${jwt_security|true}
...
```

#### datasource.yml
```yaml
PostgresDataSource:
  DriverClassName: org.postgresql.ds.PGSimpleDataSource
  jdbcUrl: jdbc:postgresql://${database_urls|out:comma-separated}/users?ssl=true&sslfactory=com.biz.SSLSocketFactory
...
```

#### secret.yml
```yaml
...
tableauPassword: ${tableau_password|out:encrypt}
...
```

Let's take a look at the tokens in these examples.

`${oauth_certificate}`

This is a straight-forward token. You can specify the replacement value for this in a values file, and (optionally) provide additional instructions to AO2 on how to handle this token in a metadata file (we will describe what values and metadata files are below).

`${jwt_security|true}`

In this case, we are providing both the token name, and providing inline instructions to AO2 on how to handle this token.

The token name is `jwt_security`. The text following the `|` character is a default value. This means if AO2 does not find a value for the token `jwt_security` in the _values.yml file, it will inject the value `true`.

`${database_urls|out:comma-separated}`

As with the previous example, we are providing both the token name, and providing inline instructions to AO2 on how to handle the token.

In this case, we are specifying how we want AO2 to transform the value before injecting it into the template. The `out:` part specifies that this is an output transformation instruction, and the `comma-separated` portion specifies that we want to convert a list of values in a string of comma separated values.

We will go into full detail on all transformation options below.

`${tableau_password|out:encrypt}`

In this example, the token name is `tableau_password`, and we have specified that we want to encrypt our value before injecting it into our template.

## Example _values.yml files

Below we can see two example values files. One for use in development, and one for use in production.

#### **dev-values.yml**
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

#### **prod-values.yml**
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

# Tokens

_**Updated for AO2**_

>**_UPDATE NOTE:_**\
>In AO1, the default regex pattern was `{{my-token}}`. If you have templates using this older default pattern, then you will need to add the `--regex \{\{(.*?)\}\}` option to those scripts.

Tokens are named placeholders that are put in template files for the purpose of being replaced with other values.

In AO2, the simplest tokens have the following form

&nbsp;&nbsp;&nbsp;&nbsp;`${token_name}`

where `token_name` is the name of the token.

Although, by default, AOtearoa 2 will look for tokens of the form `${token_name}`, AO2 can find tokens of any form by allowing you to specify the regex pattern it searches for. For example, to search for tokens of the form `{{my-token}}`, you can use the `regex` option on the command line as follows: `--regex \{\{(.*?)\}\}`.

## Inline Token Properties

_**New for AO2**_

#### Default Values

If AO2 cannot find a value to replace a token with, it will check to see if a default value has been defined. E.g.:

&nbsp;&nbsp;&nbsp;&nbsp;`${token_name|defaultValue}`

or

&nbsp;&nbsp;&nbsp;&nbsp;`${token_name|"defaultValue"}`

Note: You can also specify default values in a metadata file.

#### Output Transformations

You can also specify a transformation to be performed on a value prior to that value being injected into a template.

There are four transformations available:

- `copy`
- `comma-separated`
- `encrypt`
- `decrypt`

(Please see _Transformations_ in the **Metadata** section below, for more details on what each of these transformations does).

You can specify a transformation as follows:

&nbsp;&nbsp;&nbsp;&nbsp;`${token_name|out:<transformation-name>}`

E.g.: To encrypt a value before injecting it, would be:

&nbsp;&nbsp;&nbsp;&nbsp;`${token_name|out:encrypt}`

If no transform is specified, then the `copy` transform will be used (which simply injects a value verbatim).

Again, transforms can also be specified in a metadata file.

## Using multiple inline properties

Multiple properties can be specified in a token. One simply needs to separate them with a semicolon. E.g.:

&nbsp;&nbsp;&nbsp;&nbsp;`${token_name|out:encrypt;defaultVal}`

&nbsp;&nbsp;&nbsp;&nbsp;`${token_name|"defaultVal"; out:decrypt}`


# Metadata files

_**Updated for AO2**_

>**_UPDATE NOTE 1:_**\
>In AO2, a metadata file is no longer required, though it can still be used to provide metadata on tokens. In AO2, if a value exists that has no corresponding metadata, then default metadata information will be created for that value. Inline metadata found in template files can also be used to customize the default-generated metadata.
>
> However, to fully customize the metadata for a token, you may still need to define it in a metadata file (since not all properties available in a metadata file can be specified inline).

>**_UPDATE NOTE 2:_**\
>If you choose to use a metadata file (in order to specify properties that you cannot specify inline), then note that you no longer need to provide a `files` property. In fact, AO2 ignores any `files` values it finds in legacy (AO1) metadata files.
>
>Instead, you must specify the extensions of template files you wish AO2 to automatically check for tokens using the `-x` option on the command line. E.g.: `-x yml -x xml`.

In addition to templates and values, you can provide a metadata file that describes the values to be injected.

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
    
  database_urls:
    min: 1
    max: inf
    output:
    - comma-separated
    type: string
    prompt-text: Provide a list of one or more urls for the database

  oauth_certificate:
    min: 1
    max: 1
    output:
    - copy
    type: string
    prompt-text: Enter the name of the OAuth (Auth Server) certificate
    defaults:
    - cert.pem

  tableau_password:
    min: 1
    max: 1
    output:
    - encrypt
    type: string
    prompt-text: Tableau password (this will be encrypted)
``` 
#### Transforms
The metadata file allows the input to be transformed. In the current version, 3 types of transforms are supported:

- `copy`
- `comma-separated`
- `encrypt`
- `decrypt`

The `copy` transform simply pastes the value in as-is.

The `encrypt` transform will encrypt the password using Light4j's default symmetric encryption. The encryption secret can be set on the command line using the `-y` or `-key` option. If an encryption secret is not specified on the command line, the default Light4j secret will be used. E.g.: The tableau password (set as password above) will appear as `CRYPT:iR7QkKyNDJ4wTRn9BEXROgm2iAkajmIv2ZR5VOsxMqs=` in secret.yml.

The `decrypt` transform will decrypt the password using Light4j's default symmetric encryption. The decryption secret can be set on the command line using the `-y` or `-key` option. If a decryption secret is not specified on the command line, the default Light4j secret will be used to decrypt values.

_Note: The encryption and decryption key are the same, since Light4j's default encryption/decryption is symmetric._

The `comma-separated` transform concatenates elements in the list and separates them with commas. E.g.: the list
```yaml
  - value1
  - value2
  - value3
```
will be converted to `value1,value2,value3` prior to being inserted.

#### Default Token Values
Also note that you can set default values for tokens in the metadata yaml. These will show up in prompts and web forms, and will be used in cases where values are not entered in values file.

#### Default Metadata
If there is no metadata entry for a particular value or token, the AO2 will create one. E.g.: The default metadata for a token called `MY_TOKEN` would be:

````yaml
  MY_TOKEN:
    min: 1
    max: 1
    output:
    - copy
    type: string
    prompt-text: Please enter a value for MY_TOKEN
````

# Order of Usage for Metadata

AO2 will

 1. Use metadata for a token from a metadata file, if it exists.
 2. Otherwise, use metadata defined inline for a token, if it exists.
 3. Otherwise, generate default metadata

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

#### Default Values from Metadata
The value injected into a token can come from the default value specified in a metadata file, or as an inline token property.  

#### Command line prompts
If command line prompts are chosen as the method to input values, then on the command line, the user would see the following:  
  
<pre><code>
Type \\q to quit. Enter a blank line to use default (if default exists)
[Enable 'true', or disable 'false' JWT verification security : (true)]: <b>&#x23CE;</b>
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

&nbsp;&nbsp;&nbsp;&nbsp;`$ export jwt_security=false`

#### File values with environment variable overrides

You can also use a value file _and_ environment variables, where environment variables can be used to _*override*_ value file settings to enable you to do testing (e.g.: override security to a 'false' setting, or turn the logging level to 'debug' temporarily) without modifying your production values file.

This will prevent you from having to set environment variables for all your settings, and only specify overrides as environment variables.
 
#### Local web form
Not yet implemented.

# Log Level Output
You can select the verbosity of output that AOtearoa generates. There are 4 levels that exist: DEBUG, WARN, INFO, and QUIET. DEBUG messages are low level operational messages, whereas INFO messages are higher level status messages.

On the most verbose extreme, a log level of DEBUG will output messages of all levels. On the other end, a log level of QUIET will supress all output except for the ERROR messages that are displayed if AOtearoa is forced to exit.

The table below shows what types of message are outputted for each log level. 
  
<table border="0">
 <tr>
    <td><span pan style="font-size:15px;font-weight:bold">Log Level</td>
    <td><span style="font-size:15px;font-weight:bold">Debug</b></td>
    <td><span style="font-size:15px;font-weight:bold">Warn</b></td>
    <td><span style="font-size:15px;font-weight:bold">Info</b></td>
 </tr>
 <tr>
    <td>Debug (d)</td>
    <td>Y</td>
    <td>Y</td>
    <td>Y</td>
 </tr>
 <tr>
    <td>Warn (w)</td>
    <td>N</td>
    <td>Y</td>
    <td>Y</td>
 </tr>
 <tr>
    <td>Info (i)</td>
    <td>N</td>
    <td>N</td>
    <td>Y</td>
 </tr>
 <tr>
    <td>Quiet (q)</td>
    <td>N</td>
    <td>N</td>
    <td>N</td>
 </tr> 
 </table>
 
The command line switch for log levels is `-l` (or equivalently `--loglevel`), and the switch values are listed below:
 
<table border="0">
 <tr>
    <td><span pan style="font-size:15px;font-weight:bold">Log Level</td>
    <td><span pan style="font-size:15px;font-weight:bold">Value</td>
 </tr>
 <tr>
    <td>Debug</td>
    <td>d</td>
 </tr>
 <tr>
    <td>Warn</td>
    <td>w</td>
 </tr>
 <tr>
    <td>Info</td>
    <td>i</td>
 </tr>
 <tr>
    <td>Quiet</td>
    <td>q (or no value)</td>
 </tr>
 </table>
  
The default log level is DEBUG (this is used when no switch is used). QUIET can be specified by either `-l q`, `--loglevel q`, or by simply `-l` (with no value) or `--loglevel` (with no value). 

# Command line arguments
To run AOtearoa, you would run

&nbsp;&nbsp;&nbsp;&nbsp;`$ java -jar aotearoa.jar [arguments...]`

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
    <td>Variable metadata file (optional)</td>
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
    <td>-r, --regex</td>
    <td>regex pattern (optional - default is '\{\{(.*?)\}\}' )</td>
 </tr>
 <tr>
    <td>-t, --templates</td>
    <td>Template file folder (required)</td>
 </tr>
 <tr>
    <td>-x, --ext</td>
    <td>Template file extensions (required)</td>
 </tr> 
 <tr>
    <td>-k, --kmetadata <String></td>
    <td>Keystore metadata template (optional)</td>
 </tr>
 <tr>
    <td>-y, --key <String></td>
    <td>Symmetric encryption/decryption secret (optional)</td>
 </tr>
 <tr>
    <td>-l, --loglevel <String></td>
    <td>Sets the log level (optional)</td>
 </tr>   
</table>
 
Some examples are below:
 
#### Use value file with log level set to QUIET
```
$ java -jar aotearoa.jar         \
    -m ./templates/_metadata.yml \ # metadata file
    -t ./templates               \ # template files directory       
    -o ./config-out              \ # output folder
    -v ./templates/_values.yml   \ # values file
    -l
```

#### Use value file and set the symmetric encryption/decryption secret
```
$ java -jar aotearoa.jar            \
    -m    ./templates/_metadata.yml \ # metadata file
    -t    ./templates               \ # template files directory       
    -o    ./config-out              \ # output folder
    -v    ./templates/_values.yml   \ # values file
    --key the_secret                  # the symmetric encryption/decryption secret
```    
    
#### Use prompts
```
$ java -jar aotearoa.jar         \
    -t ./templates               \ # template files directory
    -m ./templates/_metadata.yml \ # metadata file
    -o ./config-out              \ # output folder
    -p                             # values from prompts
```     

#### Use environment variables with log level set to WARN
```
$ java -jar aotearoa.jar         \
    -t ./templates               \ # template files directory
    -m ./templates/_metadata.yml \ # metadata file
    -o ./config-out              \ # output folder
    -e                           \ # values from environment variables
    -loglevel w
```     

#### Value file with environment variable overrides
```
$ java -jar aotearoa.jar         \
    -t ./templates               \ # template files directory
    -m ./templates/_metadata.yml \ # metadata file
    -o ./config-out              \ # output folder    
    -v ./templates/_values.yml   \ # value file...
    -e                             # with environment variable overrides
```     

# Keystores
AOtearoa can also simplify the creation of JKS keystores.

###### Example Keystore YAML:
```yaml
certificates:
  tableau-webserver-cert:                                   # certificate reference name
    format: pem                                             # certificate format
    prompt-text: Tableau webserver public certificate       # prompt text
    filename: ${tableau_cert_filename}                     # filename of certificate

  postgres-database-cert:                                   # this is an internal reference name for the keystore
    format: pem                                             # certificate format
    prompt-text: Postgres self-signed certificate           # prompt text
    filename: server.pem                                    # filename of certificate

keystores:
  client.truststore:                                        # this will be the output keystore filename
    base-keystore-filename: ${standard_certs}              # base keystore filename 
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
This subsection contains the (zero or more) certs you want loaded into the keystore. If you do not enter any certs in this section, AOtearoa will create an empty keystore for you.

For each cert that you do include, each line must contain the reference name of a certificate that has been defined in the main certificates section of the yaml, and the alias you want assigned to the certificate within the keystore. The certificate reference name and its alias are separated by a ':' character.

#### tokens are allowed

In the example shown above, you'll notice the existence of tokens throughout. Since the keystore yaml file is processed after values are read (either from user prompts, from a value file, web form, config server etc...), these values can be included in the keystore yaml. These values will be injected into the keystore yaml prior to processing of the keystore yaml file. Tokens can appear anywhere in the keystore yaml and are totally optional.

#### Example command line
 
###### Build configuration and keystores from templates and value file
```
$ java -jar aotearoa.jar         \
    -t ./templates               \ # template files directory      
    -m ./templates/_metadata.yml \ # metadata file 
    -k ./templates/_keystore.yml \ # keystore template file
    -v ./templates/_values.yml   \ # values file        
    -o ./config-out                # output folder
```    
    
# Combining Files

While in the examples provided, we separate the metadata and keystore files, it is actually possible to combine these files into one. For example, if you were to combine metadata and keystore configuration into a single file called _combined.yml, then you could use the following command line.

```
$ java -jar aotearoa.jar         \
    -t ./templates               \ # template files directory
    -v ./templates/_values.yml   \ # values file          
    -o ./config-out              \ # output folder
    -m ./templates/_combined.yml \ # metadata file
    -k ./templates/_combined.yml   # keystore template file      
```    

Note: Value files cannot be combined into a single file with metadata and keystore configuration.
