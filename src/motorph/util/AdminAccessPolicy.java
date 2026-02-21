package motorph.util;

public class AdminAccessPolicy extends HrAccessPolicy {
    // Admin can do everything HR can do.
    // If you want extra admin-only screens later, override canOpenScreen().
}