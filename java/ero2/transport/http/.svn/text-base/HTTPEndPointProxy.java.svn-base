package ero2.transport.http;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import ero2.resource.rest.ErO2RestMediatorResource;
import ero2.resource.rest.ErO2RestMonitorResource;
import ero2.resource.rest.ErO2RestProfileResource;
import ero2.resource.rest.ErO2RestRegistryResource;

public class HTTPEndPointProxy extends Application {

    @Override
    public Restlet createInboundRoot() {
        Router router = new Router(getContext());
        router.attach("/service", ErO2RestRegistryResource.class);
        router.attach("/monitor", ErO2RestMonitorResource.class);
        router.attach("/mediate", ErO2RestMediatorResource.class);
        router.attach("/user", ErO2RestProfileResource.class);
        return router;
    }
}
