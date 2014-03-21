package xscript.runtime.nativemethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import xscript.runtime.nativemethod.XNativeClass.XParamSpecial;
import xscript.runtime.nativemethod.XNativeClass.XParamSpecial.XParamTypes;
import xscript.runtime.nativemethod.XNativeClass.XType;

public class XParamScanner {

	private int pos;
	private boolean needVM;
	private boolean needThread;
	private boolean needME;
	private boolean needGenerics;
	private boolean needThis;
	private boolean needUserdata;
	private String desk;
	
	public XParamScanner(){
		
	}
	
	public void reset(){
		pos = 0;
		needVM = false;
		needThread = false;
		needME = false;
		needGenerics = false;
		needThis = false;
		needUserdata = false;
		desk = null;
	}
	
	public void scann(Method method){
		reset();
		Annotation[][] paramsAnnotations = method.getParameterAnnotations();
		Class<?>[] params = method.getParameterTypes();
		lookForSpecial(paramsAnnotations);
		for(; pos<params.length; pos++){
			if(desk==null){
				desk = "(";
			}else{
				desk += ", ";
			}
			desk += getClassName(paramsAnnotations, params);
		}
		if(desk==null){
			desk = "(";
		}
		desk += ")"+getClassName(method.getAnnotation(XType.class), method.getReturnType());
	}

	private String getClassName(Annotation[][] paramsAnnotations, Class<?>[] params){
		return getClassName(getAnnotation(paramsAnnotations[pos], XType.class), params[pos]);
	}
	
	public static String getClassName(XType type, Class<?> param){
		if(type==null){
			if(param==Boolean.class || param==boolean.class){
				return "bool";
			}else if(param==Character.class || param==char.class){
				return "char";
			}else if(param==Byte.class || param==byte.class){
				return "byte";
			}else if(param==Short.class || param==short.class){
				return "short";
			}else if(param==Integer.class || param==int.class){
				return "int";
			}else if(param==Long.class || param==long.class){
				return "long";
			}else if(param==Float.class || param==float.class){
				return "float";
			}else if(param==Double.class || param==double.class){
				return "double";
			}else if(param==void.class){
				return "void";
			}else if(param==String.class){
				return "xscript.lang.String";
			}
			return "xscript.lang.Object";
		}else{
			return type.value();
		}
	}
	
	private void lookForSpecial(Annotation[][] paramsAnnotations){
		XParamSpecial special = getAnnotation(paramsAnnotations[pos], XParamSpecial.class);
		if(special!=null && special.value()==XParamTypes.VM){
			needVM=true;
			pos++;
			special = getAnnotation(paramsAnnotations[pos], XParamSpecial.class);
		}
		if(special!=null && special.value()==XParamTypes.THREAD){
			needThread=true;
			pos++;
			special = getAnnotation(paramsAnnotations[pos], XParamSpecial.class);
		}
		if(special!=null && special.value()==XParamTypes.ME){
			needME=true;
			pos++;
			special = getAnnotation(paramsAnnotations[pos], XParamSpecial.class);
		}
		if(special!=null && special.value()==XParamTypes.GENERICS){
			needGenerics=true;
			pos++;
			special = getAnnotation(paramsAnnotations[pos], XParamSpecial.class);
		}
		if(special!=null && special.value()==XParamTypes.THIS){
			needThis=true;
			pos++;
			special = getAnnotation(paramsAnnotations[pos], XParamSpecial.class);
		}
		if(special!=null && special.value()==XParamTypes.USERDATA){
			needUserdata=true;
			pos++;
		}
	}
	
	@SuppressWarnings("unchecked")
	private <A extends Annotation> A getAnnotation(Annotation[] annotations, Class<A> annotation){
		for(Annotation a:annotations){
			if(annotation == a.annotationType()){
				return (A)a;
			}
		}
		return null;
	}
	
	public String getDesk() {
		return desk;
	}

	public boolean isNeedVM() {
		return needVM;
	}

	public boolean isNeedThread() {
		return needThread;
	}

	public boolean isNeedME() {
		return needME;
	}

	public boolean isNeedGenerics() {
		return needGenerics;
	}

	public boolean isNeedThis() {
		return needThis;
	}
	
	public boolean isNeedUserdata() {
		return needUserdata;
	}
	
}
