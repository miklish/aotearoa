# AOtearoa
## Overview
Aotearoa is a command line tool that allows Application Operations teams generate complex configurations for an application while having little to no knowledge of the application's configuration system beyond the few values they are entrusted to provide (e.g.: production credentials, certificates, private keys, etc...).

## Template, and Value files
AOtearoa starts with template files. A template is a normal text configuration file that contains 'tags' in which values will be inserted.

##### security.yml
```yaml
# Enable JWT verification flag.
enableVerifyJwt: {{jwt-security}}

# Enable JWT scope verification. Only valid when enableVerifyJwt is true.
enableVerifyScope: true

# User for test only. should be always be false on official environment.
enableMockJwt: false

# JWT signature public certificates. kid and certificate path mappings.
jwt:
  certificate:
    '100': {{oauth-certificate}}
  clockSkewInSeconds: 60
  
oauth-password: {{auth-password}}
```

The tags `{jwt-security}` and `{oauth-certificate}` take their values from a 'values' file:

##### dev-values.yml
```yaml
jwt-security:
  - false
oauth-certificate:
  - dev-auth-server-cert.pem
auth-password:
  - devpassword
```

##### prod-values.yml
```yaml
jwt-security:
  - true
oauth-certificate:
  - prod-auth-server-cert.pem
auth-password:
  - prodpassword  
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

  oauth-certificate:
    min: 1
    max: 1
    output:
    - copy
    type: string
    prompt-text: OAuth server certificate file name
    files:
    - security

  auth-password:
    min: 1
    max: 1
    output:
    - encrypt
    type: string
    prompt-text: OAuth password (this will be encrypted)
    files:
    - security
```
