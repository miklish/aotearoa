# AO 2
## Metadata

The _metadata.yml file is now optional. Many metadata options can now be specified in the templates themselves, rather than in a separate _metadata file.

Even if a metadatafile is specified, metadata for each token is now optional. Any tokens that are not specified in the metadata file, but are found in a template, will now have metadata created for them using default property values.

If a metadata file is specified _*and*_ metadata is specified in the template, then the metadata file takes precedence for any properties specified in both files for the same token.

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
```

### `min` and `max`
- optional
- only specified in metadata.yml
- default for both is 1

### `output`
- optional
- can be specified in template and metadata.yml
- ${TOKEN_NAME | out:encrypt; def:"the default value"}
- default is `string`
- new option `block#yaml` is available, that will inject an entire block of yaml

### `prompt-text`
- optional
- only specified in metadata.yml
- default "Please enter the value for {{TOKEN_NAME}}"

### `default`
- optional
- can be specified in template and metadata.yml
- there is no default value

### `files`
- deprecated
- only specified in metadata.yml
- no longer used, any value found is ignored

# Development

- Remove the `files` requirement
- Add the defaults as mentioned above
