package com.christoff.aotearoa.intern.gateway.metadata;

import com.christoff.aotearoa.intern.gateway.view.IPresenter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MetadataMerge
{
    private IPresenter _presenter;

    public MetadataMerge(IPresenter presenter) {
        _presenter = presenter;
    }

    public Map<String, Metadata> merge(
        Map<String,Map<String,List<Metadata>>> templateMetadataLists,
        Map<String,Metadata> metadata,
        Map<String,Metadata> valueMetadata
    ) {
        /**
         * 1. Then select data from Metadata file
         * 2. Select Template metadata first
         * 3. Then select Value metadata
         */
        Map<String,Metadata> mergedTemplateMetadata = mergeTemplateMetadata(templateMetadataLists);
        // merge Template Metadata --> Metadata
        Map<String,Metadata> merged = metadataMergeMaps(metadata, mergedTemplateMetadata);
        // merge Value Metadata --> (Template Metadata --> Metadata)
        merged = metadataMergeMaps(merged, valueMetadata);

        return merged;
    }

    public Map<String, Metadata> mergeTemplateMetadata(Map<String, Map<String, List<Metadata>>> templatesMetadataMap)
        throws MetadataException
    {
        Map<String, Metadata> mergedMap = new HashMap<>();

        // loop through each template file
        for (Map.Entry<String, Map<String, List<Metadata>>> templateMapEntry : templatesMetadataMap.entrySet())
        {
            // merge
            String templateName = templateMapEntry.getKey();
            Map<String, List<Metadata>> templateMap = templateMapEntry.getValue();

            if(templateMap.size() == 0)
                _presenter.noTokensFoundInTemplate(templateName);
            else
                _presenter.mergingTokenFromTemplate(templateName);

            // loop through each token in the template
            for(List<Metadata> metadataList : templateMap.values())
            {
                for(Metadata newMetadata : metadataList)
                    metadataMergeOne(mergedMap, newMetadata);
            }
        }

        return mergedMap;
    }

    /***
     * Merges a metadata object into a metadata map, as long as there is no conflict
     *
     * @param map
     * @param newMetadata
     * @throws MetadataException
     */
    private void metadataMergeOne(Map<String, Metadata> map, Metadata newMetadata)
        throws MetadataException
    {
        // check if we already read this metadata
        String tokenName = newMetadata.getName();
        if(map.containsKey(tokenName))
        {
            // check if there is a conflict
            Metadata oldMetadata = map.get(tokenName);
            if(!newMetadata.equals(oldMetadata))
                throw new MetadataException("Conflicting metadata for token " + tokenName);

            _presenter.tokenAlreadyExists(tokenName);
        }
        else {
            _presenter.tokenAdded(tokenName);
            map.put(tokenName, newMetadata);
        }
    }

    /***
     * Merges two Maps of Metadata together, while checking for conflicts
     *
     * @param base
     * @param merge
     *
     * @throws MetadataException
     */
    private Map<String, Metadata> metadataMergeMaps(Map<String, Metadata> base, Map<String, Metadata> merge)
        throws MetadataException
    {
        for(Metadata mergeValue : merge.values())
            if(!base.containsKey(mergeValue.getName()))
                base.put(mergeValue.getName(), mergeValue);

        return base;
    }
}
