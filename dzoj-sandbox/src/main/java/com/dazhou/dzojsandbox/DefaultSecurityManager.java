package com.dazhou.dzojsandbox;

import java.security.Permission;

/**
 * @author dazhou
 * @title
 * @create 2023-08-19 0:58
 */
public class DefaultSecurityManager extends SecurityManager{
    // 检查所有的权限
    @Override
    public void checkPermission(Permission perm) {
        System.out.println("默认不做任何限制");
        System.out.println(perm);
        // super.checkPermission(perm);
    }
}
