package com.christoff.aotearoa.intern.gateway.values;

import com.christoff.aotearoa.intern.gateway.metadata.Metadata;

import java.util.List;
import java.util.Map;

public interface IValueGateway
{
    /***
     * Returns the value list for a matadata variable
     *
     * @param vm The metadata object you want value(s) for
     * @return A list of variable values
     */
    List<Object> get(Metadata vm);

    /***
     * Check if a value for this metadata variable exists
     *
     * Will not throw exceptions
     */
    boolean exists(Metadata vm);

    /***
     * Provide all metadata to gateway so that it may present forms etc... to illicit values from users
     * @param allVarMetadata All variable metadata object keyed by tag name
     */
    void setMetadata(Map<String, Metadata> allVarMetadata);
}
