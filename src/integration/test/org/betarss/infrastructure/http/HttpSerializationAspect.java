package org.betarss.infrastructure.http;

import org.aspectj.lang.annotation.Aspect;

@Aspect
public class HttpSerializationAspect {

	//	@Pointcut("this(org.betarss.infrastrucuture.HttpClient)")
	//	public Object serialize(ProceedingJoinPoint pjp) throws Throwable {
	//        // start stopwatch
	//        Object retVal = pjp.proceed();
	//        // stop stopwatch
	//        return retVal;
	//	}
	//
	//	@Around("this(org.betarss.infrastrucuture.HttpClient)")
	//	public void deserialize((ProceedingJoinPoint pjp) throws Throwable {
	//        // start stopwatch
	//        Object retVal = pjp.proceed();
	//        // stop stopwatch
	//        return retVal;) {
	//
	//	}

}
