package com.app.erp.shared.claims;
import java.util.*;

public final class ClaimsUtil {
    private ClaimsUtil(){}

    public static Integer asInteger(Object claim) {
        if (claim == null) return null;
        if (claim instanceof Integer i) return i;
        if (claim instanceof Number n)  return n.intValue();
        if (claim instanceof String s)  try { return Integer.parseInt(s); } catch (NumberFormatException ignored) {}
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Set<Integer> asIntegerSet(Object claim) {
        if (claim == null) return Set.of();
        if (claim instanceof Collection<?> col) {
            Set<Integer> out = new LinkedHashSet<>();
            for (Object o : col) {
                Integer v = asInteger(o);
                if (v != null) out.add(v);
            }
            return out;
        }
        // También aceptar un único valor suelto
        Integer single = asInteger(claim);
        return single != null ? Set.of(single) : Set.of();
    }
}
