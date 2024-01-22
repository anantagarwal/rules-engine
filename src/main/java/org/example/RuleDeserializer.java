package org.example;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.example.pojo.Conditions;
import org.example.pojo.Event;
import org.example.pojo.Rule;

import java.io.IOException;

public class RuleDeserializer extends StdDeserializer<Rule> {

    public RuleDeserializer() {
        this(null);
    }

    public RuleDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Rule deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        Rule rule = new Rule();

        // Handle the case when "priority" is null or missing
        JsonNode priorityNode = node.get("priority");
        if (priorityNode != null && priorityNode.isNumber()) {
            rule.setPriority(priorityNode.asInt());
        } else {
            // Set a default priority or handle the error as needed
            rule.setPriority(0);
        }

        JsonNode conditionsNode = node.get("conditions");
        if (conditionsNode != null) {
            Conditions conditions = jp.getCodec().treeToValue(conditionsNode, Conditions.class);
            rule.setConditions(conditions);
        }

        JsonNode eventNode = node.get("event");
        if (eventNode != null) {
            Event event = jp.getCodec().treeToValue(eventNode, Event.class);
            rule.setEvent(event);
        }

        return rule;
    }
}
