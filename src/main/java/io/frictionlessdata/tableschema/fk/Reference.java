package io.frictionlessdata.tableschema.fk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.frictionlessdata.tableschema.exception.ForeignKeyException;
import io.frictionlessdata.tableschema.objectmapper.ObjectMapperSingleton;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 * 
 */
public class Reference {
    private static final String JSON_KEY_DATAPACKAGE = "datapackage";
    private static final String JSON_KEY_RESOURCE = "resource";
    private static final String JSON_KEY_FIELDS = "fields";
    
    private URL datapackage = null;
    private String resource = null;
    private Object fields = null;
    
    public Reference(){
    }
    
    public Reference(String resource, Object fields) throws ForeignKeyException{
        this(resource, fields, false);
    }
    
    public Reference(String resource, Object fields, boolean strict) throws ForeignKeyException{
        this.resource = resource;
        this.fields = fields;
        
        if(strict){
            this.validate();
        }
    }
    
    public Reference(URL datapackage, String resource, Object fields) throws ForeignKeyException{
        this(datapackage, resource, fields, false);
    }
    
    public Reference(URL datapackage, String resource, Object fields, boolean strict) throws ForeignKeyException{
        this.resource = resource;
        this.fields = fields;
        this.datapackage = datapackage;
        
        if(strict){
            this.validate();
        }
    }
    
    public Reference(String json, boolean strict) throws ForeignKeyException, IOException {
		ObjectMapper mapper = ObjectMapperSingleton.INSTANCE.getMapper();
        JsonNode refJsonObject = mapper.readTree(json);

		if(refJsonObject.has(JSON_KEY_DATAPACKAGE)){
            try{
                this.datapackage = new URL(refJsonObject.get(JSON_KEY_DATAPACKAGE).textValue());
                
            }catch(MalformedURLException mue){
                // leave datapackage set to null;
                this.datapackage = null;
            }  
        }
        
        if(refJsonObject.has(JSON_KEY_RESOURCE)){
            this.resource = refJsonObject.get(JSON_KEY_RESOURCE).textValue();
        }
        
        if(refJsonObject.has(JSON_KEY_FIELDS)){
            this.fields = refJsonObject.get(JSON_KEY_FIELDS);   
        }
        
        if(strict){
            this.validate();
        }
    }
    
    public URL getDatapackage(){
        return this.datapackage;
    }
    
    public String getResource(){
        return this.resource;
    }
    
    public void setResource(String resource){
        this.resource = resource;
    }
    
    public <Any> Any getFields(){
        return (Any)this.fields;
    }
    
    public void setFields(Object fields){
        this.fields = fields;
    }
    
    public final void validate() throws ForeignKeyException{
        if(this.resource == null || this.fields == null){
            throw new ForeignKeyException("A foreign key's reference must have the fields and resource properties.");

        }else if(!(this.fields instanceof String  || this.fields instanceof ArrayNode)){
            throw new ForeignKeyException("The foreign key's reference fields property must be a string or an array.");
        }
    }
    
    public String getJson() throws JsonProcessingException {
        ObjectMapper mapper = ObjectMapperSingleton.INSTANCE.getMapper();
		ObjectNode json = mapper.createObjectNode();

        if(this.datapackage != null){
            json.put(JSON_KEY_DATAPACKAGE, this.datapackage.toString());
        }

        if(this.resource != null){
            json.put(JSON_KEY_RESOURCE, this.resource);
        }

        if(this.fields != null){
            if (this.fields instanceof String) {
                json.put(JSON_KEY_FIELDS, (String) this.fields);
            } else {
                json.set("fields", (ArrayNode) this.fields);
            }
        }

		return mapper.writeValueAsString(json);
    }
}
