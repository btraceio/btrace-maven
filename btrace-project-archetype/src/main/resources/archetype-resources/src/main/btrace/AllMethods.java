package ${groupId}.${artifactId};

import com.sun.btrace.annotations.*;
import static com.sun.btrace.BTraceUtils.*;

@BTrace public class AllMethods {
    @OnMethod(
        clazz="/javax\\.swing\\..*/",
        method="/.*/"
    )
    public static void m(@Self Object o, @ProbeClassName String probeClass, @ProbeMethodName String probeMethod) {
        println("this = " + o);
        print("entered " + probeClass);
        println("." + probeMethod);
    }
}

