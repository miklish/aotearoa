package com.christoff.aotearoa.bridge;

import com.christoff.aotearoa.intern.gateway.IServiceConfigDataGateway;
import com.christoff.aotearoa.intern.gateway.IServiceValueGateway;
import com.christoff.aotearoa.intern.view.IServicePresenter;

import java.util.Map;

public class ServiceInteractor
{
    private IServiceConfigDataGateway _configGateway;
    private IServiceValueGateway _valueGateway;
    private IServicePresenter _presenter;

    public ServiceInteractor(
        IServiceConfigDataGateway configGateway,
        IServiceValueGateway valueGateway,
        IServicePresenter presenter
    ) {
        _configGateway = configGateway;
        _valueGateway = valueGateway;
        _presenter = presenter;
    }

    public ServiceResponse exec(ServiceRequest request)
    {
        // Load in the diff file
        Map<String,Object> diffMap = _configGateway.get(request.configId);

        // parse the diff file at a high level

        // dispatch sections of the diff file to different methods/classes

        return new ServiceResponse("Success", "SUCCESS");
    }
}
