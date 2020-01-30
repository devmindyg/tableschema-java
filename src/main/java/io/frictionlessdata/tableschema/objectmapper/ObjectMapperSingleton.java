package io.frictionlessdata.tableschema.objectmapper;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * Singleton to optimize creation of {@link com.fasterxml.jackson.databind.ObjectMapper}
 */
public enum ObjectMapperSingleton {
	INSTANCE;

	private final ObjectMapper mapper = new ObjectMapper();

	public ObjectMapper getMapper() {
		return this.mapper;
	}
}

