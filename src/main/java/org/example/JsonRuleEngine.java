package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.pojo.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiPredicate;

public class JsonRuleEngine {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final Map<String, BiPredicate<Object, Object>> operators;

    static {
        operators = Map.of(
                "equal", Object::equals,
                "notEqual", (a, b) -> !a.equals(b),
                "greaterThan", (a, b) -> compare(a, b) > 0,
                "greaterThanOrEqual", (a, b) -> compare(a, b) >= 0,
                "lessThan", (a, b) -> compare(a, b) < 0,
                "lessThanOrEqual", (a, b) -> compare(a, b) <= 0,
                "in", JsonRuleEngine::inOperator,
                "dateEqual", JsonRuleEngine::dateEqualOperator,
                "dateBefore", JsonRuleEngine::dateBeforeOperator,
                "dateAfter", JsonRuleEngine::dateAfterOperator
        );
    }

    private List<Rule> rules;
    private GlobalDefault globalDefault;

    public JsonRuleEngine(Rules rules) {
        this.rules = rules.getRules();
        this.globalDefault = rules.getGlobalDefault();
    }

    public static void main(String[] args) {
        String rulesJson = """
                {
                  "rules": [
                    {
                      "priority": 1,
                      "conditions": {
                        "all": [
                          {
                            "fact": "age",
                            "operator": "greaterThan",
                            "value": 18
                          },
                          {
                            "fact": "location",
                            "operator": "in",
                            "value": ["US", "CA"]
                          },
                          {
                            "fact": "dob",
                            "operator": "dateBefore",
                            "value": "1990-01-01"
                          }
                        ]
                      },
                      "event": {
                        "type": "eligible",
                        "params": {
                          "message": "User is eligible",
                          "value": "some value"
                        }
                      }
                    },
                    {
                      "priority": 2,
                      "conditions": {
                        "all": [
                          {
                            "fact": "age",
                            "operator": "greaterThan",
                            "value": 30
                          },
                          {
                            "fact": "location",
                            "operator": "in",
                            "value": ["AB", "CD"]
                          }
                        ]
                      },
                      "event": {
                        "type": "notEligible",
                        "params": {
                          "message": "User is not eligible",
                          "value": "default value"
                        }
                      }
                    }
                  ],
                  "globalDefault": {
                    "type": "defaultEvent",
                    "params": {
                      "message": "No matching rule found. Using global default value",
                      "value": "global default value"
                    }
                  }
                }""";

        try {
            Rules rules = objectMapper.readValue(rulesJson, Rules.class);

            JsonRuleEngine ruleEngine = new JsonRuleEngine(rules);

            Map<String, Object> inputData = new HashMap<>();
            inputData.put("age", 31);
            inputData.put("location", "AB");
            inputData.put("dob", "1985-05-10");

            Map<String, Object> result = ruleEngine.evaluateRules(inputData);

            if (result != null) {
                System.out.println("Event message: " + result.get("message"));
                System.out.println("Event value: " + result.get("value"));
            } else {
                System.out.println("No matching rule found. Using global default value.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> evaluateRules(Map<String, Object> inputData) {
        if (rules != null) {
            rules.sort(Comparator.comparingInt(Rule::getPriority));

            for (Rule rule : rules) {
                if (evaluateRule(rule.getConditions(), inputData)) {
                    return rule.getEvent().getParams();
                }
            }
        }

        return (globalDefault != null) ? globalDefault.getParams() : null;
    }

    private boolean evaluateRule(Conditions conditions, Map<String, Object> inputData) {
        if (conditions != null && conditions.getAll() != null) {
            for (Condition condition : conditions.getAll()) {
                if (!evaluateCondition(condition, inputData)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean evaluateCondition(Condition condition, Map<String, Object> inputData) {
        String fact = condition.getFact();
        String operator = condition.getOperator();
        Object value = condition.getValue();

        if (inputData.containsKey(fact)) {
            Object factValue = inputData.get(fact);

            if (operators.containsKey(operator)) {
                return operators.get(operator).test(factValue, value);
            }
        }

        return false;
    }

    private static int compare(Object a, Object b) {
        return ((Comparable<Object>) a).compareTo(b);
    }

    private static boolean inOperator(Object a, Object b) {
        if (a instanceof Collection && b instanceof Collection) {
            Collection<?> collectionA = (Collection<?>) a;
            Collection<?> collectionB = (Collection<?>) b;
            return collectionA.stream().anyMatch(collectionB::contains);
        } else if (a instanceof Collection) {
            Collection<?> collectionA = (Collection<?>) a;
            return collectionA.contains(b);
        } else if (b instanceof Collection) {
            Collection<?> collectionB = (Collection<?>) b;
            return collectionB.contains(a);
        }
        return false;
    }

    private static boolean dateBeforeOperator(Object a, Object b) {
        return compareDates(a, b) < 0;
    }

    private static boolean dateEqualOperator(Object a, Object b) {
        return compareDates(a, b) == 0;
    }

    private static boolean dateAfterOperator(Object a, Object b) {
        return compareDates(a, b) > 0;
    }

    private static int compareDates(Object a, Object b) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date dateA = (a instanceof Date) ? (Date) a : dateFormat.parse(a.toString());
            Date dateB = (b instanceof Date) ? (Date) b : dateFormat.parse(b.toString());
            return dateA.compareTo(dateB);
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing dates", e);
        }
    }


}

