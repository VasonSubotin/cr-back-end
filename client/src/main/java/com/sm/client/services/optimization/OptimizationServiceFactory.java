package com.sm.client.services.optimization;

import com.sm.client.model.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Service
public class OptimizationServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(OptimizationServiceFactory.class);

    @Autowired
    private ApplicationContext applicationContext;

    private Map<PolicyType, OptimizationService> optimizationServiceMap = new ConcurrentHashMap<>();

    private static final String OPTIMIZATION_PACKAGE = "com.sm.client.services.optimization";

    @PostConstruct
    public void init() {
        // scaning all classes in package
        final ClassPathScanningCandidateComponentProvider classPathScanprovider = new ClassPathScanningCandidateComponentProvider(false);
        classPathScanprovider.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*")));


        for (BeanDefinition bean : classPathScanprovider.findCandidateComponents(OPTIMIZATION_PACKAGE)) {
            try {
                Class clazz = Class.forName(bean.getBeanClassName());
                if (!OptimizationService.class.isAssignableFrom(clazz)) {
                    continue;
                }
                for (Annotation annotation : clazz.getAnnotations()) {
                    if ("Service".equals(annotation.annotationType().getSimpleName()) || "Component".equals(annotation.annotationType().getSimpleName())) {
                        OptimizationService optimizationService = null;
                        if (annotation instanceof Service && ((Service) annotation).value() != null && !((Service) annotation).value().isEmpty()) {
                            optimizationService = (OptimizationService) applicationContext.getBean(((Service) annotation).value());
                        } else if (annotation instanceof Component && ((Component) annotation).value() != null && !((Component) annotation).value().isEmpty()) {
                            optimizationService = (OptimizationService) applicationContext.getBean(((Component) annotation).value());
                        } else {
                            optimizationService = (OptimizationService) applicationContext.getBean(clazz);
                        }
                        if (optimizationService != null) {
                            optimizationServiceMap.put(optimizationService.getPolicy(), optimizationService);
                        }
                        break;
                    }
                }
                // we should add all services to every session
            } catch (ClassNotFoundException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public OptimizationService getService(PolicyType policyType) {
        return optimizationServiceMap.get(policyType);
    }
}
