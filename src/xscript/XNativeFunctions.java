package xscript;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xscript.object.XFunction;
import xscript.object.XFunctionData;
import xscript.object.XRuntime;
import xscript.values.XValue;
import xscript.values.XValueNull;

class XNativeFunctions {

	private static final Map<String, XFunctionData> functions;
	
	static{
		Map<String, XFunctionData> map = new HashMap<String, XFunctionData>();
		map.put("__builtin__.__getModule", new XFunctionData(new __builtin__.__getModule(), "module"));
		map.put("__builtin__.__importParent", new XFunctionData(new __builtin__.__importParent(), "module"));
		map.put("__builtin__.__importModule", new XFunctionData(new __builtin__.__importModule(), "module"));
		map.put("__builtin__.__initModule", new XFunctionData(new __builtin__.__initModule(), "module"));
		map.put("__builtin__.__fillStackTrace", new XFunctionData(new __builtin__.__fillStackTrace()));
		map.put("__builtin__.__print", new XFunctionData(new __builtin__.__print(), "string"));
		map.put("__builtin__.__pollInput", new XFunctionData(new __builtin__.__pollInput()));
		map.put("__builtin__.__sleep", new XFunctionData(new __builtin__.__sleep(), "time"));
		functions = Collections.unmodifiableMap(map);
	}

	static Map<String, XFunctionData> getFunctions(){
		return functions;
	}
	
	private static final class __builtin__{
	
		private __builtin__(){}
		
		private static class __getModule implements XFunction{
	
			@Override
			public XValue invoke(XRuntime runtime, XExec exec, XValue thiz, XValue[] params, List<XValue> list, Map<String, XValue> map) throws Throwable {
				return runtime.getModule(XUtils.getString(runtime, params[0]));
			}
			
		}
		
		private static class __importParent implements XFunction{
	
			@Override
			public XValue invoke(XRuntime runtime, XExec exec, XValue thiz, XValue[] params, List<XValue> list, Map<String, XValue> map) throws Throwable {
				String module = XUtils.getString(runtime, params[0]);
				int i = module.lastIndexOf('.');
				if(module.endsWith(".__init__")){
					i = module.lastIndexOf('.', i-1);
				}
				if(i==-1){
					return XValueNull.NULL;
				}else{
					module = module.substring(0, i)+".__init__";
					XValue __import = runtime.getBuiltinModule().getRaw(runtime, "__import");
					XValue m = runtime.alloc(module);
					List<XValue> l = new ArrayList<XValue>();
					l.add(m);
					exec.call(__import, null, l, null);
					return NO_PUSH;
				}
			}
			
		}
		
		private static class __importModule implements XFunction{
	
			@Override
			public XValue invoke(XRuntime runtime, XExec exec, XValue thiz, XValue[] params, List<XValue> list, Map<String, XValue> map) throws Throwable {
				String module = XUtils.getString(runtime, params[0]);
				return runtime.tryImportModule(module);
			}
			
		}
		
		private static class __initModule implements XFunction{
	
			@Override
			public XValue invoke(XRuntime runtime, XExec exec, XValue thiz, XValue[] params, List<XValue> list, Map<String, XValue> map) throws Throwable {
				XValue method = runtime.alloc(runtime.getBaseType(XUtils.FUNC), "<init>", new String[0], -1, -1, -1, XValueNull.NULL, params[0], XValueNull.NULL, 0, new XClosure[0]);
				exec.call(method, null, Collections.<XValue>emptyList(), null);
				return NO_PUSH;
			}
			
		}
		
		private static class __fillStackTrace implements XFunction{
			
			@Override
			public XValue invoke(XRuntime runtime, XExec exec, XValue thiz, XValue[] params, List<XValue> list, Map<String, XValue> map) throws Throwable {
				return XUtils.convertStackTrace(runtime, exec.getStackTrace(true));
			}
			
		}
		
		private static class __print implements XFunction{
			
			@Override
			public XValue invoke(XRuntime runtime, XExec exec, XValue thiz, XValue[] params, List<XValue> list, Map<String, XValue> map) throws Throwable {
				String s = XUtils.getString(runtime, params[0]);
				runtime.getOut().println(s);
				return XValueNull.NULL;
			}
			
		}
		
		private static class __pollInput implements XFunction{
			
			@Override
			public XValue invoke(XRuntime runtime, XExec exec, XValue thiz, XValue[] params, List<XValue> list, Map<String, XValue> map) throws Throwable {
				byte[] buffer = new byte[1024];
				int num = runtime.getIn().read(buffer);
				String in = new String(buffer, 0, num);
				XValue ret = runtime.alloc(in);
				return ret;
			}
			
		}

		private static class __sleep implements XFunction{
			
			@Override
			public XValue invoke(XRuntime runtime, XExec exec, XValue thiz, XValue[] params, List<XValue> list, Map<String, XValue> map) throws Throwable {
				exec.setWait(params[0].getInt());
				return NO_PUSH;
			}
			
		}
	
	}
	
}
