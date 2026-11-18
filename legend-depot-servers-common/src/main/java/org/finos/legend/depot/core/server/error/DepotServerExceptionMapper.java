//  Copyright 2021 Goldman Sachs
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

package org.finos.legend.depot.core.server.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

public class DepotServerExceptionMapper extends BaseExceptionMapper<LegendDepotServerException>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DepotServerExceptionMapper.class);

    public DepotServerExceptionMapper(boolean includeStackTrace)
    {
        super(includeStackTrace);
    }

    public DepotServerExceptionMapper()
    {
        this(false);
    }

    @Override
    public Response toResponse(LegendDepotServerException exception)
    {
        Response.Status status = exception.getStatus();
        switch (status.getFamily())
        {
            case CLIENT_ERROR:
            {
                // we do not return a stack trace for client errors, regardless of the value of includeStackTrace
                return buildResponse(status, ExtendedErrorMessage.fromLegendDepotServerException(exception, false));
            }
            case SERVER_ERROR:
            {
                return buildResponse(status, ExtendedErrorMessage.fromLegendDepotServerException(exception, this.includeStackTrace));
            }
            case REDIRECTION:
            {
                // TODO consider cases other than 301, 302, 303, and 307
                URI redirectLocation = getRedirectLocation(exception);
                if (redirectLocation != null)
                {
                    return Response.status(status)
                            .location(redirectLocation)
                            .build();
                }

                // Could not get a redirect location from an exception indicating there should be a redirect
                // Send back an internal server error instead
                LOGGER.warn("Could not get redirect URI from exception with status: {}", status);
                return buildResponse(Response.Status.INTERNAL_SERVER_ERROR, ExtendedErrorMessage.fromLegendDepotServerException(exception, this.includeStackTrace));
            }
            default:
            {
                // Exception with non-error HTTP status
                // Send back an internal server error instead
                LOGGER.warn("Exception with non-error HTTP status: {}", status);
                return buildResponse(Response.Status.INTERNAL_SERVER_ERROR, ExtendedErrorMessage.fromLegendDepotServerException(exception, this.includeStackTrace));
            }
        }
    }

    private static URI getRedirectLocation(LegendDepotServerException exception)
    {
        String message = exception.getMessage();
        if (message == null)
        {
            LOGGER.warn("A LegendSDLCServerException with status {} should have the redirect location as its message, found null", exception.getStatus());
            return null;
        }

        try
        {
            return new URI(message);
        }
        catch (URISyntaxException e)
        {
            LOGGER.warn("Invalid redirect location for LegendDepotServerException with status {}: {}", exception.getStatus(), message);
            return null;
        }
    }

}
