package isocline.clockwork.object;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.*;





@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)


public @interface WorkInfo {

    int step() default 1;

    boolean async() default false;

    String id() default "1";

    boolean start() default false;

    boolean end() default false;

    String next() default "x";

    String startAfter() default "x";



}
