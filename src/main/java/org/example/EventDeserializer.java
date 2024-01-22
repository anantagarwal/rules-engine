package org.example;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.example.pojo.Event;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EventDeserializer extends StdDeserializer<Event> {

    public EventDeserializer() {
        this(null);
    }

    public EventDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Event deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        Event event = new Event();
        event.setType(node.get("type").asText());

        JsonNode paramsNode = node.get("params");
        if (paramsNode != null) {
            Map<String, Object> params = new HashMap<>();
            paramsNode.fields().forEachRemaining(entry -> params.put(entry.getKey(), entry.getValue().asText()));
            event.setParams(params);
        }

        return event;
    }
}
