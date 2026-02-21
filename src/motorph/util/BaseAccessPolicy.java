package motorph.util;

public abstract class BaseAccessPolicy implements AccessPolicy {

    @Override
    public boolean canManageEmployees() { return false; }

    @Override
    public boolean canComputePayroll() { return false; }

    @Override
    public boolean canApproveLeaves() { return false; }
}