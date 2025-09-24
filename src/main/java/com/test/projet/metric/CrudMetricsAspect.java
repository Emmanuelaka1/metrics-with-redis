
package com.test.projet.metric;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CrudMetricsAspect {

    private final MetricsRecorder recorder;

    public CrudMetricsAspect(MetricsRecorder recorder) {
        this.recorder = recorder;
    }

    // Intercepte save/insert/update dans les repositories
    @Around("execution(* com.test.projet..*Repository.save*(..)) || execution(* com.test.projet..*Repository.insert*(..)) || execution(* com.test.projet..*Repository.update*(..))")
    public Object aroundSave(ProceedingJoinPoint pjp) throws Throwable {
        return record(pjp, "UPDATE");
    }

    // Intercepte delete*
    @Around("execution(* com.test.projet..*Repository.delete*(..)) || execution(* com.test.projet..*Repository.deleteById(..))")
    public Object aroundDelete(ProceedingJoinPoint pjp) throws Throwable {
        return record(pjp, "DELETE");
    }

    private Object record(ProceedingJoinPoint pjp, String op) throws Throwable {
        String entity = resolveEntityName(pjp);
        return recorder.timeAndRecord(entity, op, () -> {
            try {
                return pjp.proceed();
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        });
    }

    private String resolveEntityName(ProceedingJoinPoint pjp) {
        String cls = pjp.getTarget().getClass().getSimpleName();
        return cls.replace("Repository", "");
    }
}
