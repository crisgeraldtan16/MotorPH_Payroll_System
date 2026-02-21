package motorph.util;

import java.util.Set;

public class ItAccessPolicy extends BaseAccessPolicy {

    private static final Set<String> ALLOWED = Set.of(
            "DASHBOARD"
    );

    @Override
    public boolean canOpenScreen(String screen) {
        return ALLOWED.contains(screen);
    }

    // IT usually doesn’t do payroll/HR actions in your project scope
}