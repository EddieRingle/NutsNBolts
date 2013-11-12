package io.github.eddieringle.android.libs.nutsnbolts.app;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WhenDone {

    public Class<? extends WorkDoneEvent> value();
}
