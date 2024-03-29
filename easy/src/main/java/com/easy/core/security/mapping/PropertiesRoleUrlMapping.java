/**
 * Copyright (c) 2012-2013 All Rights Reserved.
 */
package com.easy.core.security.mapping;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.util.Assert;

import com.easy.core.security.util.SecurityUtil;

/**
 * 从资源文件中加载对应关系
 * 
 * @author wy
 * @version v 0.1 2013-9-25 下午9:54:05 wy Exp $
 */
public class PropertiesRoleUrlMapping implements RoleUrlMapping, InitializingBean {

    protected final Logger                                  log                = LoggerFactory
                                                                                   .getLogger(PropertiesRoleUrlMapping.class);

    /** 加载的资源文件 */
    private List<Resource>                                  resources;

    /** 文件编码 */
    private String                                          fileEncoding;

    /** url对应的权限列表 */
    private final static Map<String, List<ConfigAttribute>> URL_ROLE_MAPPING = new HashMap<String, List<ConfigAttribute>>();

    /**
     * Setter method for property <tt>resources</tt>.
     * 
     * @param resources value to be assigned to property resources
     */
    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    /**
     * Setter method for property <tt>fileEncoding</tt>.
     * 
     * @param fileEncoding value to be assigned to property fileEncoding
     */
    public void setFileEncoding(String fileEncoding) {
        this.fileEncoding = fileEncoding;
    }

    /**
     * @see com.easy.core.security.MapRelation#getAllMapping()
     */
    @Override
    public Map<String, List<ConfigAttribute>> getAllMapping() {
        return URL_ROLE_MAPPING;
    }

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notEmpty(resources, "权限文件不能为空");
        Properties properties = new Properties();
        for (Resource location : resources) {
            log.debug("Loading properties file from " + location);
            InputStream is = null;
            try {
                is = location.getInputStream();
                if (this.fileEncoding != null) {
                    properties.load(new InputStreamReader(is, this.fileEncoding));
                } else {
                    properties.load(is);
                }
            }
            catch (IOException ex) {
                log.warn("Could not load properties from {}: {}", location, ex.getMessage());
            }
            finally {
                IOUtils.closeQuietly(is);
            }
        }
        for (Object object : properties.keySet()) {
            String url = object.toString();
            String permission = properties.getProperty(url);

            String resUrl = SecurityUtil.getUrl(url);
            List<ConfigAttribute> list = URL_ROLE_MAPPING.get(resUrl);
            if (list == null) {
                list = new ArrayList<ConfigAttribute>();
                URL_ROLE_MAPPING.put(resUrl, list);
            }
            for (String p : SecurityUtil.getRoles(permission)) {
                SecurityConfig securityConfig = new SecurityConfig(p);
                if (!list.contains(securityConfig)) {
                    list.add(securityConfig);
                }
            }
        }
        log.debug("权限码初始化完毕");
    }
}
