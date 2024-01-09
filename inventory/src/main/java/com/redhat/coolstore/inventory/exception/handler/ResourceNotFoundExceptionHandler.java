package com.redhat.coolstore.inventory.exception.handler;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import com.redhat.coolstore.inventory.exception.ResourceNotFoundException;

@Provider
public class ResourceNotFoundExceptionHandler implements ExceptionMapper<ResourceNotFoundException>{

	public Response toResponse(ResourceNotFoundException ex) {
		
		return Response.status(Status.NOT_FOUND).entity(Status.NOT_FOUND.getStatusCode() + " - " + ex.getMessage()).build();
	}

}
