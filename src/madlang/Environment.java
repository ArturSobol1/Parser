package madlang;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

class Environment {

    static class Slot {
        final VarType type;
        Object value;

        Slot(VarType type, Object value) {
            this.type = type;
            this.value = value;
        }
    }

    private final Deque<Map<String, Slot>> scopes = new ArrayDeque<>();

    Environment() {
        pushScope();
    }

    void pushScope() {
        scopes.push(new HashMap<>());
    }

    void popScope() {
        scopes.pop();
    }

    void define(String name, VarType type, Object value) {
        scopes.peek().put(name, new Slot(type, value));
    }

    Slot getSlot(String name) {
        for (Map<String, Slot> scope : scopes) {
            Slot slot = scope.get(name);
            if (slot != null) return slot;
        }
        return null;
    }

    Slot assignSlot(String name, Object value) {
        for (Map<String, Slot> scope : scopes) {
            if (scope.containsKey(name)) {
                Slot slot = scope.get(name);
                slot.value = value;
                return slot;
            }
        }
        return null;
    }
}