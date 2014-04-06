package org.pentaho.di.pojo.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface UI {
  String label() default "";
  String hint() default "";
}
