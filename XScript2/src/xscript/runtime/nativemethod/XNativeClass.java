package xscript.runtime.nativemethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface XNativeClass {

	public String value();
	
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface XNativeMethod{
		
		public String value() default "";
		
	}
	
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface XNativeField{
		
		public String value() default "";
		
	}
	
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface XNativeFactory{}
	
	@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface XType{
		
		public String value();
		
	}
	
	@Target({ElementType.PARAMETER})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface XParamSpecial{
		
		public XParamTypes value();
		
		public enum XParamTypes{
			
			VM, THREAD, ME, GENERICS, THIS
			
		}
		
	}
	
}
