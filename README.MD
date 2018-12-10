# AOtearoa 1.1.0
## Overview
Aotearoa is a command line tool that allows Application Operations teams generate complex configurations for an application while having little to no knowledge of the application's configuration system beyond the few values they are entrusted to provide (e.g.: production credentials, certificates, private keys, etc...).

## Template, and Value files
AOtearoa starts with template files. A template is a normal text configuration file that contains 'tags' in which values will be inserted.

##### security.yml
```yaml
# Enable JWT verification flag.
enableVerifyJwt: {{jwt-security}}
...
# JWT signature public certificates. kid and certificate path mappings.
jwt:
  certificate:
    '100': {{oauth-certificate}}
  clockSkewInSeconds: 60
...
```

##### datasource.yml
```yaml
PostgresDataSource:
  DriverClassName: org.postgresql.ds.PGSimpleDataSource
  jdbcUrl: jdbc:postgresql://{{database-urls}}/users?ssl=true&sslfactory=com.biz.SSLSocketFactory
...
```

##### secret.yml
```yaml
...
tableauPassword: {{tableau-password}}
...
```

The tags `{{jwt-security}}`, `{oauth-certificate}`, `{{postgres-servers}}`, and `{{tableau-password}}` take their values from a 'values' file:

##### dev-values.yml
```yaml
jwt-security:
  - false
database-urls:  
  - dev-postgres-01.biz.com:9000
  - dev-postgres-02.biz.com:9000
oauth-certificate:
  - oauth-dev.pem
tableau-password:
  - devpassword
```

##### prod-values.yml
```yaml
jwt-security:
  - true
database-urls:  
  - tss-postgres-01.biz.com:7373
  - tss-postgres-02.biz.com:7373
  - tss-postgres-03.biz.com:7390  
oauth-certificate:
  - oauth.pem
tableau-password:
  - password  
```

As you can see in the examples above, you can use different value files for different environments. 

# Metadata files
In addition to templates and values, you must provide a metadata file that describes the values to be injected.

```yaml
variables:
  jwt-security:
    min: 1
    max: 1
    output:
    - copy
    type: string
    prompt-text: Set to true to turn on JWT verifcation, false otherwise
    files:
    - security
    
  database-urls:
    min: 1
    max: inf
    output:
    - comma-separated
    type: string
    prompt-text: Provide a list of one or more urls for the database (primary and failover instances). Include the ports.
    files:
    - datasource    

  oauth-certificate:
    min: 1
    max: 1
    output:
    - copy
    type: string
    prompt-text: Tableau server certificate file name
    files:
    - security

  tableau-password:
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

# Values
There are numerous ways in which values can be specified to the template resolver.

#### Values file
Below see our example production value file.

```yaml
jwt-security:
  - true
database-urls:  
  - tss-postgres-01.biz.com:7373
  - tss-postgres-02.biz.com:7373
  - tss-postgres-03.biz.com:7390
oauth-certificate:
  - oauth.pem
tableau-password:
  - password
```
All values must be entered as elements of a Yaml list (even if there is only a single entry).

##### Transforms
The metadata file allows the input to be transformed. In the current version, 3 types of transforms are supported:

- `copy`
- `comma-separated`
- `encrypt`

The `copy` transform simply pastes the value in as-is.

The `encrypt` transform will encrypt the password using Light4j's default symmetric encryption. E.g.: The tableau password (set as password above) will appear as CRYPT:iR7QkKyNDJ4wTRn9BEXROgm2iAkajmIv2ZR5VOsxMqs= in secret.yml.

The `comma-separated` transform concatenates elements in the list and separates them with commas. E.g.: the list
```yaml
  - value1
  - value2
  - value3
```
will be converted to `value1,value2,value3` prior to being inserted.

#### Command line prompts
If command line prompts are chosen as the method to input values, then on the command line, the user would see the following:  
```
Type \\q to quit
[Enable (true) or disable (false) JWT verification security]: true

Next entry requires between 1 and inf values. Enter \\n to complete.
[Enter the Postgres database urls (including ports)]: 
[Value 1]: tss-postgres-01.biz.com:7373
[Value 2]: tss-postgres-02.biz.com:7373
[Value 3]: tss-postgres-03.biz.com:7390
[Value 4]: \\n

[Enter the name of the OAuth (Auth Server) certificate]: oauth.pem
[Enter the Tableau functional ID username]: my-tableau-username
[Enter the Tableau functional ID password]: password
Resolving templates...done.
```

#### Local web form
Not yet implemented.

# Command line arguments
To run aotearoa, you would run

$ java -jar aotearoa-1.1.0.jar

with the appropriate arguments:

<table border="0">
 <tr>
    <td><span pan style="font-size:30px;">Command</td>
    <td><span style="font-size:30px">Description</b></td>
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
    <td>-p, --prompts</td>
    <td>Use command line prompts to enter values (optional)</td>
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
    <td>-v, --values <String></td>
    <td>Value file (required)</td>
 </tr>
</table>  