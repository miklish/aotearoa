package com.christoff.aotearoa.intern.gateway.values;

import java.util.List;

/***
 * Facilitates access to config values.
 *
 * Config value keys have the following format:
 *
 *      sourcefile/key
 *
 * Local vs Config-Server Issues:
 *
 *
 *   - diff-val files can support lists of values, while config server only supports only a
 *     single string per value
 *
 *     solution
 *     - should not be an issue, as we only send transformed values (e.g.: once transformed, string are
 *       converted to a single string, in the current version. Specifically, ITransform's only method is
 *       String transform(List<String) val)).
 *     - The way we do this is to always pass variable values wrapped in a VariableMetadata object, so that
 *       implementation of 'save' method can decide the format in which to send the value
 *
 *   - encrypted values:
 *     - if a variable is configured to be encrypted, then
 *       a) if the resolved configs are stored locally, then we must encrypt them
 *       b) if the resolved config values are sent to config server, then we must
 *          1) indicate to config server that they are to be encrypted
 *          2) but send the values in plain text (since they will be encrypted on the server side)
 *
 *     solution
 *     - we must simply ensure that the implementation of any 'save' methods does not send transformed
 *       strings if the transform happens to be 'encrypt'
 *     - therefore, save functions should save values wrapped inside an accompanying VariableMetadata object
 *
 * Additional compatability issue:
 *
 *   - The local system needs to deal with the actual config files, whereas when dealing with config-server,
 *     we only deal with it on a value-by-value level
 *
 *     solution
 *     - since the config values keys reference the config file they come from, then we can perhaps use the same
 *       interface for both.
 *     - have read/update methods
 *     - keep track of changes to each of the values in a map
 *     - push changes to web or file system when save() called
 *
 */
public interface IValueGateway
{
    public List<Object> get(String configValueId);
}
