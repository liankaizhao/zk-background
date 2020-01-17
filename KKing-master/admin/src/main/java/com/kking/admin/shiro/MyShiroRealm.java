package com.kking.admin.shiro;

import com.kking.dao.entity.TSysPerm;
import com.kking.dao.entity.TSysRole;
import com.kking.dao.entity.TSysUser;
import com.kking.dao.service.TSysRoleService;
import com.kking.dao.service.TSysUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

public class MyShiroRealm extends AuthorizingRealm {
    @Autowired
    private TSysUserService userService;
    @Autowired
    private TSysRoleService roleService;


    /**
     * 权限认证
     *
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
//        System.out.println("权限配置-->MyShiroRealm.doGetAuthorizationInfo()");
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        TSysUser user = (TSysUser) principals.getPrimaryPrincipal();
        List<TSysRole> roleList = getSysRoles(user);

        for (TSysRole role : roleList) {
            authorizationInfo.addRole(role.getRoleName());
            for (TSysPerm perm : role.getPermList()) {
                if (StringUtils.isNotEmpty(perm.getPermName())) {
                    authorizationInfo.addStringPermission(perm.getPermName());
                }
            }
        }
        return authorizationInfo;
    }


    /**
     * 身份认证，验证用户输入的账号和密码是否正确
     *
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {

        String username = (String) token.getPrincipal();
        //这里可以根据实际情况做缓存，如果不做，Shiro自己也是有时间间隔机制，2分钟内不会重复执行该方法
        TSysUser user = userService.selectOneByProperty("user_name", username);
        if (user == null) {
            throw new AuthenticationException("该用户不存在!");
        }
        //账户冻结
        if (user.getState() == TSysUser.STATE.INVALID) {
            throw new LockedAccountException();
        }
        getSysRoles(user);
        return new SimpleAuthenticationInfo(
                user,
                user.getPassword(),
                ByteSource.Util.bytes(user.getSalt()),
                getName()
        );
    }

    private List<TSysRole> getSysRoles(TSysUser user) {
        List<TSysRole> roleList = user.getRoleList();
        if (roleList == null) {
            roleList = roleService.getUserRoleInfo(user.getId(), TSysPerm.PERM_TYPE.MENU);
            user.setRoleList(roleList);
        }
        return roleList;
    }


    public static void main(String[] args) {
        DecimalFormat decimalFormat = new DecimalFormat("###,###.00");
        System.out.println(decimalFormat.format(Double.parseDouble("0")));
    }
}
