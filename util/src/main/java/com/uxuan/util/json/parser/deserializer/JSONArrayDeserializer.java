package com.uxuan.util.json.parser.deserializer;

import java.lang.reflect.Type;

import com.uxuan.util.json.JSONArray;
import com.uxuan.util.json.parser.DefaultJSONParser;
import com.uxuan.util.json.parser.JSONToken;

public class JSONArrayDeserializer implements ObjectDeserializer {
    public final static JSONArrayDeserializer instance = new JSONArrayDeserializer();

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultJSONParser parser, Type clazz, Object fieldName) {
        JSONArray array = new JSONArray();
        parser.parseArray(array);
        return (T) array;
    }

    public int getFastMatchToken() {
        return JSONToken.LBRACKET;
    }
}
