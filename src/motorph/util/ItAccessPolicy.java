package motorph.util;

import java.util.Set;

/*
 * Access policy for IT users.
 * IT staff can access the dashboard and manage user accounts.
 */
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