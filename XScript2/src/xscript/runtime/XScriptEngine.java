package xscript.runtime;

import java.io.Reader;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

public class XScriptEngine implements ScriptEngine {

	private XScriptEngineFactory factory;
	
	public XScriptEngine(XScriptEngineFactory factory) {
		this.factory = factory;
	}

	@Override
	public Bindings createBindings() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object eval(String arg0) throws ScriptException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object eval(Reader arg0) throws ScriptException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object eval(String arg0, ScriptContext arg1) throws ScriptException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object eval(Reader arg0, ScriptContext arg1) throws ScriptException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object eval(String arg0, Bindings arg1) throws ScriptException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object eval(Reader arg0, Bindings arg1) throws ScriptException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get(String arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Bindings getBindings(int arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ScriptContext getContext() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ScriptEngineFactory getFactory() {
		return factory;
	}

	@Override
	public void put(String arg0, Object arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setBindings(Bindings arg0, int arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setContext(ScriptContext arg0) {
		throw new UnsupportedOperationException();
	}
	
}
