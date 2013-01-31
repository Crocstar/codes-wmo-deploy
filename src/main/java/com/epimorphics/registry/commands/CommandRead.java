/******************************************************************
 * File:        CommandRead.java
 * Created by:  Dave Reynolds
 * Created on:  22 Jan 2013
 *
 * (c) Copyright 2013, Epimorphics Limited
 *
 *****************************************************************/

package com.epimorphics.registry.commands;

import static com.epimorphics.registry.webapi.Parameters.VIEW;
import static com.epimorphics.registry.webapi.Parameters.WITH_VERSION;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.epimorphics.registry.core.Command;
import com.epimorphics.registry.core.Description;
import com.epimorphics.registry.core.RegisterItem;
import com.epimorphics.registry.core.Registry;
import com.epimorphics.registry.webapi.Parameters;
import com.hp.hpl.jena.rdf.model.Model;
import com.sun.jersey.api.NotFoundException;


public class CommandRead extends Command {

    public CommandRead(Operation operation, String target,
            MultivaluedMap<String, String> parameters, Registry registry) {
        super(operation, target, parameters, registry);
    }

    @Override
    public Response execute() {
        Description d = null;

        if (lastSegment.startsWith("_")) {
            // An item
            if (lastSegment.contains(":")) {
                // An explicit item version
                d = store.getVersion(target);
                if (d != null) {
                    store.getEntity(d.asRegisterItem());
                }
            } else {
                //  plain item
                if (hasParamValue(VIEW, WITH_VERSION)) {
                    d = store.getItemWithVersion(target, true, false);
                } else {
                    d = store.getItem(target, true, false) ;
                }
            }
        } else {
            // An entity
            if ( Parameters.WITH_METADATA.equals( parameters.getFirst(Parameters.VIEW) ) ) {
                // Entity with metadata
                d = store.getItem(parent +"/_" + lastSegment, true, false);
            } else {
                // plain entity
                d = store.getDescription(target, false);
            }
        }

        // TODO Handle case of it being a register and getting members

        if (d == null) {
            throw new NotFoundException();
        }

        // Include any entity in the response
        if (d instanceof RegisterItem) {
            RegisterItem ri = d.asRegisterItem();
            Model m = ri.getRoot().getModel();
            if (ri.getEntity() != null) {
                m.add( ri.getEntity().getModel() );
            }
        }

        URI uri;
        try {
            uri = new URI( d.getRoot().getURI() );
        } catch (URISyntaxException e) {
            throw new WebApplicationException(e);
        }
        return Response.ok().location(uri).entity( d.getRoot().getModel() ).build();
    }

}
