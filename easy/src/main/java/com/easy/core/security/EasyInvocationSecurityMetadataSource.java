/**
 * Copyright (c) 2012-2013 All Rights Reserved.
 */
package com.easy.core.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.AntUrlPathMatcher;
import org.springframework.security.web.util.UrlMatcher;

import com.easy.core.security.mapping.RoleUrlMapping;

/**
 * 资源源数据定义，即定义某一资源可以被哪些角色访问
 * 
 * @author wy
 * @version v 0.1 2013-9-24 下午11:04:28 wy Exp $
 */
public class EasyInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    //URL匹配 *匹配
    private UrlMatcher     urlMatcher = new AntUrlPathMatcher();

    /** 关联关系 */
    private RoleUrlMapping roleUlrMapping;

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {

        if ((object == null) || !this.supports(object.getClass())) {
            throw new IllegalArgumentException("Object must be a FilterInvocation");
        }

        String url = ((FilterInvocation) object).getRequestUrl();

        Iterator<String> ite = roleUlrMapping.getAllMapping().keySet().iterator();
        List<ConfigAttribute> list = new ArrayList<ConfigAttribute>();
        while (ite.hasNext()) {
            String resURL = ite.next();
            if (urlMatcher.pathMatchesUrl(resURL, url)) {
                list.addAll(roleUlrMapping.getAllMapping().get(resURL));
            }
        }
        return list;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Set<ConfigAttribute> allAttributes = new HashSet<ConfigAttribute>();
        for (String key : roleUlrMapping.getAllMapping().keySet()) {
            allAttributes.addAll(roleUlrMapping.getAllMapping().get(key));
        }
        return allAttributes;
    }

    /**
     * Setter method for property <tt>urlMatcher</tt>.
     * 
     * @param urlMatcher value to be assigned to property urlMatcher
     */
    public void setUrlMatcher(UrlMatcher urlMatcher) {
        this.urlMatcher = urlMatcher;
    }

    /**
     * Setter method for property <tt>roleUlrMapping</tt>.
     * 
     * @param roleUlrMapping value to be assigned to property roleUlrMapping
     */
    public void setRoleUrlMapping(RoleUrlMapping roleUlrMapping) {
        this.roleUlrMapping = roleUlrMapping;
    }

}
