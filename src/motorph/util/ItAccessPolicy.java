package motorph.util;

import java.util.Set;

public class ItAccessPolicy extends BaseAccessPolicy {

    private static final Set<String> ALLOWED = Set.of(
            "DASHBOARD",
            "USER_ACCOUNTS"
    );

    @Override
    public boolean canOpenScreen(String screen) {
        return ALLOWED.contains(screen);
    }
}