package org.example.pojo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.example.ConditionDeserializer;

import java.util.List;

public class Conditions {
    private List<Condition> all;

    //@JsonDeserialize(contentUsing = ConditionDeserializer.class)
    public List<Condition> getAll() {
        return all;
    }

    public void setAll(List<Condition> all) {
        this.all = all;
    }
}
