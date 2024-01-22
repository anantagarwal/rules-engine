package org.example.pojo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.example.EventDeserializer;
import org.example.RuleDeserializer;
import org.example.pojo.Event;
import org.example.pojo.GlobalDefault;
import org.example.pojo.Rule;

import java.util.ArrayList;

public class Rules {

    //@JsonDeserialize(using = EventDeserializer.class)
    private GlobalDefault globalDefault;

    //@JsonDeserialize(using = RuleDeserializer.class)
    public ArrayList<Rule> rules;

    public GlobalDefault getGlobalDefault() {
        return globalDefault;
    }

    public void setGlobalDefault(GlobalDefault globalDefault) {
        this.globalDefault = globalDefault;
    }

    public ArrayList<Rule> getRules() {
        return rules;
    }

    public void setRules(ArrayList<Rule> rules) {
        this.rules = rules;
    }
}
