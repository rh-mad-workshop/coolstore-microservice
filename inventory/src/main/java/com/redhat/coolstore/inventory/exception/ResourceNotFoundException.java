package com.redhat.coolstore.inventory.exception;

public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -6998741953524619552L;

	public ResourceNotFoundException(String message) {
        super(message);
    }

}
