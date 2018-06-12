package com.loan_manage.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
/**
 * Created by wanzezhong on 2017/9/21.
 */
public class GomeSearchJsonSerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
//        jgen.writeString("");
        jgen.writeObject(new JsonNullDef());
    }

}

class JsonNullDef{

    private List<String> def = new ArrayList<>();

    public List<String> getDef() {
        return def;
    }

    public void setDef(List<String> def) {
        this.def = def;
    }


}
