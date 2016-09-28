package com.scistor.tab.auth.annotations;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.scistor.tab.auth.common.Constants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represent date using default date format.
 *
 * @author Wei Xing
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonFormat(pattern = Constants.DEFAULT_DATE_FORMAT, timezone = Constants.DEFAULT_TIME_ZONE)
public @interface JsonDefaultDateFormat {
}
