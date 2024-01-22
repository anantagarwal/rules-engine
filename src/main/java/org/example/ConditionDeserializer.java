package org.example;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.example.pojo.Condition;

import java.io.IOException;

public class ConditionDeserializer extends StdDeserializer<Condition> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public ConditionDeserializer() {
        this(null);
    }

    public ConditionDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Condition deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        Condition condition = new Condition();
        condition.setFact(node.get("fact").asText());
        condition.setOperator(node.get("operator").asText());
        condition.setValue(objectMapper.treeToValue(node.get("value"), Object.class));

        return condition;
    }
}

