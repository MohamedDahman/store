package at.stores.store.security;

import at.stores.store.util.SecurityUtils;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private Object filterObject;
    private Object returnObject;


    public CustomMethodSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
        setDefaultRolePrefix("");
    }

    public boolean isAllowed(String role) {
        UserPrincipal userPrincipal = (UserPrincipal) this.getPrincipal();
        return SecurityUtils.isAllowed(userPrincipal, role);
    }

    @Override
    public void setFilterObject(Object obj) {
        this.filterObject = obj;

    }

    @Override
    public Object getFilterObject() {
        return this.filterObject;
    }

    @Override
    public void setReturnObject(Object obj) {
        this.returnObject = obj;
    }

    @Override
    public Object getReturnObject() {
        return this.returnObject;
    }

    @Override
    public Object getThis() {
        return this;
    }


}
