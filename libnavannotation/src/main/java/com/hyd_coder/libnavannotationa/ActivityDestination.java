package com.hyd_coder.libnavannotationa;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface ActivityDestination {

    String pageUrl();

    boolean needLogin() default false;

    boolean asStarter() default false;
}
