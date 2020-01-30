package io.frictionlessdata.tableschema.fk;

import io.frictionlessdata.tableschema.exception.ForeignKeyException;
import io.frictionlessdata.tableschema.objectmapper.ObjectMapperSingleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 */
public class ForeignKey {
    private static final String JSON_KEY_FIELDS = "fields";
    private static final String JSON_KEY_REFERENCE = "reference";
    
    private Object fields = null;
    private Reference reference = null;
    
    private boolean strictValidation = false;
    private List<Exception> errors = new ArrayList();
    
    public ForeignKey(){   
    }
    
    public ForeignKey(boolean strict){  
        this();
        this.strictValidation = strict;
    }
    
    
    public ForeignKey(Object fields, Reference reference, boolean strict) throws ForeignKeyException{
        this.fields = fields;
        this.reference = reference;
        this.strictValidation = strict;
        this.validate();
    }
    
    public ForeignKey(String json, boolean strict) throws ForeignKeyException, IOException {
        ObjectMapper mapper = ObjectMapperSingleton.INSTANCE.getMapper();
        JsonNode fkJsonObject = mapper.readTree(json);
        this.strictValidation = strict;
        
        if(fkJsonObject.has(JSON_KEY_FIELDS)){
            this.fields = fkJsonObject.get(JSON_KEY_FIELDS);
        }
        
        if(fkJsonObject.has(JSON_KEY_REFERENCE)){
            JsonNode refJsonObject = fkJsonObject.get(JSON_KEY_REFERENCE);
            this.reference = new Reference(refJsonObject.asText(), strict);
        }
        
        this.validate();
    }
    
    public void setFields(Object fields){
        this.fields = fields;
    }
    
    public <Any> Any getFields(){
        return (Any)this.fields;
    }
    
    public void setReference(Reference reference){
        this.reference = reference;
    }
    
    public Reference getReference(){
        return this.reference;
    }
    
    public final void validate() throws ForeignKeyException{
        ForeignKeyException fke = null;
        
        if(this.fields == null || this.reference == null){
            fke = new ForeignKeyException("A foreign key must have the fields and reference properties.");
            
        }else if(!(this.fields instanceof String || this.fields instanceof ArrayNode)){
            fke = new ForeignKeyException("The foreign key's fields property must be a string or an array.");
            
        }else if(this.fields instanceof ArrayNode && !(this.reference.getFields() instanceof ArrayNode)){
            fke = new ForeignKeyException("The reference's fields property must be an array if the outer fields is an array.");
            
        }else if(this.fields instanceof String && !(this.reference.getFields() instanceof String)){
            fke = new ForeignKeyException("The reference's fields property must be a string if the outer fields is a string.");
            
        }else if(this.fields instanceof ArrayNode && this.reference.getFields() instanceof ArrayNode){
            ArrayNode fkFields = (ArrayNode) this.fields;
            ArrayNode refFields = (ArrayNode) this.reference.getFields();
            
            if(fkFields.size() != refFields.size()){
                fke = new ForeignKeyException("The reference's fields property must be an array of the same length as that of the outer fields' array.");
            }
        }
        
        if(fke != null){
            if(this.strictValidation){
                throw fke;  
            }else{
                this.getErrors().add(fke);
            }           
        }

    }
    
    public String getJson() throws JsonProcessingException, IOException {
        ObjectMapper mapper = ObjectMapperSingleton.INSTANCE.getMapper();
        ObjectNode json = mapper.createObjectNode();
        
        if(this.fields != null){
            if (this.fields instanceof String) {
                json.put(JSON_KEY_FIELDS, (String) this.fields);
            } else {
                json.set(JSON_KEY_FIELDS, (ArrayNode) this.fields);
            }
        }
        
        if(this.reference != null){
            JsonNode ref = mapper.readTree(reference.getJson());
            json.set(JSON_KEY_REFERENCE, ref);
        }
  
        return mapper.writeValueAsString(json);
    }
    
    public List<Exception> getErrors(){
        return this.errors;
    }
    
}
