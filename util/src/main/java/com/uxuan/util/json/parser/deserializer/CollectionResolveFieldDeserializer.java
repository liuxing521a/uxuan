package com.uxuan.util.json.parser.deserializer;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import com.uxuan.util.json.parser.DefaultJSONParser;

@SuppressWarnings("rawtypes")
public final class CollectionResolveFieldDeserializer extends FieldDeserializer {

    private final Collection collection;

    public CollectionResolveFieldDeserializer(DefaultJSONParser parser, Collection collection){
        super(null, null);
        this.collection = collection;
    }

    @SuppressWarnings("unchecked")
    public void setValue(Object object, Object value) {
        collection.add(value);
    }

    public void parseField(DefaultJSONParser parser, Object object, Type objectType, Map<String, Object> fieldValues) {

    }

}
