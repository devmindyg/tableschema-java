package io.frictionlessdata.tableschema.fk;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import io.frictionlessdata.tableschema.exception.ForeignKeyException;
import io.frictionlessdata.tableschema.objectmapper.ObjectMapperSingleton;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.Assert;

public class ForeignKeyTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testValidStringFields() throws ForeignKeyException {
        Reference ref = new Reference("aResource", "refField", true);
        ForeignKey fk = new ForeignKey("fkField", ref, true);

        // Validation set to strict=true and no exception has been thrown.
        // Test passes.
        Assert.assertNotNull(fk);
    }

    @Test
    public void testValidArrayFields() throws ForeignKeyException {
        String refFields = "[\"refField1\", \"refField2\"]";
        Reference ref = new Reference("aResource", refFields, true);
        String fkFields = "[\"fkField1\", \"fkField2\"]";
        ForeignKey fk = new ForeignKey(fkFields, ref, true);

        // Validation set to strict=true and no exception has been thrown.
        // Test passes.
        Assert.assertNotNull(fk);
    }

    @Test
    public void testNullFields() throws ForeignKeyException {
        Reference ref = new Reference("aResource", "aField", true);

        exception.expectMessage("A foreign key must have the fields and reference properties.");
        ForeignKey fk = new ForeignKey(null, ref, true);
    }

    @Test
    public void testNullReference() throws ForeignKeyException{
        ForeignKey fk = new ForeignKey(true);
        fk.setFields("aField");

        exception.expectMessage("A foreign key must have the fields and reference properties.");
        fk.validate();
    }

    @Test
    public void testNullFieldsAndReference() throws ForeignKeyException{
        ForeignKey fk = new ForeignKey(true);
        exception.expectMessage("A foreign key must have the fields and reference properties.");
        fk.validate();
    }

    @Test
    public void testFieldsNotStringOrArray() throws ForeignKeyException{
        Reference ref = new Reference("aResource", "aField", true);

        exception.expectMessage("The foreign key's fields property must be a string or an array.");
        ForeignKey fk = new ForeignKey(25, ref, true);
    }

    @Test
    public void testFkFieldsIsStringAndRefFieldsIsArray() throws ForeignKeyException{
        ObjectMapper mapper = ObjectMapperSingleton.INSTANCE.getMapper();
        ArrayNode refFields = mapper.createArrayNode();
        refFields.add("field1");
        refFields.add("field2");
        refFields.add("field3");

        Reference ref = new Reference("aResource", refFields, true);

        exception.expectMessage("The reference's fields property must be a string if the outer fields is a string.");
        ForeignKey fk = new ForeignKey("aStringField", ref, true);
    }

    @Test
    public void testFkFieldsIsArrayAndRefFieldsIsString() throws ForeignKeyException{
        Reference ref = new Reference("aResource", "aStringField", true);
        ObjectMapper mapper = ObjectMapperSingleton.INSTANCE.getMapper(); 
        ArrayNode fkFields = mapper.createArrayNode();
        fkFields.add("field1");
        fkFields.add("field2");
        fkFields.add("field3");

        exception.expectMessage("The reference's fields property must be an array if the outer fields is an array.");
        ForeignKey fk = new ForeignKey(fkFields, ref, true);
    }

    @Test
    public void testFkAndRefFieldsDifferentSizeArray() throws ForeignKeyException{
        ObjectMapper mapper = ObjectMapperSingleton.INSTANCE.getMapper();
        ArrayNode refFields = mapper.createArrayNode();
        refFields.add("refField1");
        refFields.add("refField2");
        refFields.add("refField3");

        Reference ref = new Reference("aResource", refFields, true);

        ArrayNode fkFields = mapper.createArrayNode();
        fkFields.add("field1");
        fkFields.add("field2");

        exception.expectMessage("The reference's fields property must be an array of the same length as that of the outer fields' array.");
        ForeignKey fk = new ForeignKey(fkFields, ref, true);
    }
}
