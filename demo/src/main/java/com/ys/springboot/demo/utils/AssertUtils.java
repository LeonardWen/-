package com.ys.springboot.demo.utils;

import com.ys.springboot.demo.exception.ServiceMyException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class AssertUtils extends Assert {

    private static final Logger logger = LoggerFactory.getLogger(AssertUtils.class);

    /**
     *
     * @param object
     * @param message
     */
    public static void isNull(Object object, String message) {
        if (object != null) {
            logger.error(message);
            throw new ServiceMyException(message);
        }
    }

    /**
     *
     * @param object
     * @param message
     */
    public static void isNull(String object, String message) {
        if (StringUtils.isNotBlank(object)) {
            logger.error(message);
            throw new ServiceMyException(message);
        }
    }

    /**
     *
     * @param object
     * @param message
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            logger.error(message);
            throw new ServiceMyException(message);
        }
    }

    /**
     *
     * @param object
     * @param message
     */
    public static void notNull(String object, String message) {
        if (StringUtils.isBlank(object)) {
            logger.error(message);
            throw new ServiceMyException(message);
        }
    }

    /**
     *
     * @param target
     * @param source
     * @param message
     */
    public static void notEquals(Integer target, Integer source, String message) {
        if (target.equals(source)) {
            logger.error(message);
            throw new ServiceMyException(message);
        }
    }

    public static void isEquals(Integer target, Integer source, String message) {
        if (!target.equals(source)) {
            logger.error(message);
            throw new ServiceMyException(message);
        }
    }


    /**
     *
     * @param target
     * @param sources
     * @param message
     */
    public static void isEquals(Integer target, Integer[] sources, String message) {
        for (Integer source : sources) {
            if (target.equals(source)) {
                return;
            }
            logger.error(message);
            throw new ServiceMyException(message);
        }

    }


    /**
     *
     * @param expression
     * @param message
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            logger.error(message);
            throw new ServiceMyException(message);
        }
    }

}
