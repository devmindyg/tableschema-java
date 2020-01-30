package io.frictionlessdata.tableschema.fk;

import io.frictionlessdata.tableschema.exception.ForeignKeyException;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 */
public class ReferenceTest {

    @Test
    public void testValidStringFieldsReference() throws ForeignKeyException{
        Reference ref = new Reference("resource", "field");

        // Validation set to strict=true and no exception has been thrown.
        // Test passes.
        Assertions.assertNotNull(ref);
    }

    @Test
    public void testValidArrayFieldsReference() throws ForeignKeyException, IOException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode fields = (ArrayNode) objectMapper.readTree("[]");
        fields.add("field1");
        fields.add("field2");

        Reference ref = new Reference("resource", fields);

        // Validation set to strict=true and no exception has been thrown.
        // Test passes.
        Assertions.assertNotNull(ref);
    }

    @Test
    public void testNullFields() {
	ForeignKeyException thrown = Assertions.assertThrows(ForeignKeyException.class,
		() -> { Reference ref = new Reference(null, "resource", true); });
        Assertions.assertEquals("A foreign key's reference must have the fields and resource properties.", thrown.getMessage());
    }

    @Test
    public void testNullResource() throws ForeignKeyException{
        ForeignKeyException thrown = Assertions.assertThrows(ForeignKeyException.class,
		() -> {
			Reference ref = new Reference();
			ref.setFields("aField");
			ref.validate();
		});

	Assertions.assertEquals("A foreign key's reference must have the fields and resource properties.", thrown.getMessage());
    }

    @Test
    public void testNullFieldsAndResource() throws ForeignKeyException{
	ForeignKeyException thrown = Assertions.assertThrows(ForeignKeyException.class,
		() -> {
			Reference ref = new Reference();
			ref.validate();
		});


	Assertions.assertEquals("A foreign key's reference must have the fields and resource properties.", thrown.getMessage());
    }

    @Test
    public void testInvalidFieldsType() throws ForeignKeyException{
	ForeignKeyException thrown = Assertions.assertThrows(ForeignKeyException.class,
		() -> {
			Reference ref = new Reference("resource", 123, true);
		});

	Assertions.assertEquals("The foreign key's reference fields property must be a string or an array.", thrown.getMessage());
    }
}
