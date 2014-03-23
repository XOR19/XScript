package xscript.runtime.threads;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import xscript.runtime.XRuntimeException;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XInputStreamSave;
import xscript.runtime.clazz.XOutputStreamSave;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.method.XMethod;

public class XThreadProvider {

	private XVirtualMachine virtualMachine;
	private List<XThread> threads = new ArrayList<XThread>();
	private List<XThread> interrupts = new ArrayList<XThread>();
	private List<XInterruptTerminatedListener> interruptTerminatedListeners = new ArrayList<XInterruptTerminatedListener>();
	private List<XThreadErroredListener> threadErroredListeners = new ArrayList<XThreadErroredListener>();
	private int nextThreadIDName=1;
	private int nextThreadID=1;
	private int activeThreadID;
	private boolean newImportantInterrupt;
	
	public XThreadProvider(XVirtualMachine virtualMachine){
		this.virtualMachine = virtualMachine;
	}
	
	public XThreadProvider(XVirtualMachine virtualMachine, XInputStreamSave dis) throws IOException {
		this.virtualMachine = virtualMachine;
		threads.clear();
		interrupts.clear();
		int s = dis.readInt();
		for(int i=0; i<s; i++){
			threads.add(new XThread(virtualMachine, dis));
		}
		s = dis.readInt();
		for(int i=0; i<s; i++){
			interrupts.add(new XThread(virtualMachine, dis));
		}
		nextThreadIDName = dis.readInt();
		nextThreadID = dis.readInt();
		activeThreadID = dis.readInt();
		newImportantInterrupt = false;
	}

	public void save(XOutputStreamSave dos) throws IOException {
		dos.writeInt(threads.size());
		for(XThread thread:threads){
			thread.save(dos);
		}
		dos.writeInt(interrupts.size());
		for(XThread interrupt:interrupts){
			interrupt.save(dos);
		}
		dos.writeInt(nextThreadIDName);
		dos.writeInt(nextThreadID);
		dos.writeInt(activeThreadID);
	}
	
	public void markVisible() {
		for(XThread thread:threads){
			thread.markVisible();
		}
		for(XThread thread:interrupts){
			thread.markVisible();
		}
	}

	private XThread getNextInterrupt(){
		int index = 0;
		XThread interrupt;
		do{
			if(index>=interrupts.size())
				return null;
			interrupt = interrupts.get(index);
			interrupt.sleepUpdate();
			if(interrupt.getThreadState()==XThreadState.ERRORED || interrupt.getThreadState()==XThreadState.TERMINATED){
				interrupts.remove(index);
				for(XInterruptTerminatedListener interruptTerminatedListener:interruptTerminatedListeners){
					interruptTerminatedListener.onInterruptTerminated(virtualMachine, interrupt);
				}
				if(interrupt.getThreadState()==XThreadState.ERRORED){
					for(XThreadErroredListener threadErroredListener:threadErroredListeners){
						threadErroredListener.onThreadErrored(virtualMachine, interrupt);
					}
				}
				continue;
			}
			index++;
		}while(interrupt.getThreadState()!=XThreadState.RUNNING);
		return interrupt;
	}
	
	private XThread getNextThread(){
		if(threads.isEmpty()){
			return null;
		}
		XThread thread = threads.get(activeThreadID);
		thread.sleepUpdate();
		if(thread.getThreadState()!=XThreadState.RUNNING){
			if(thread.getThreadState()==XThreadState.ERRORED || thread.getThreadState()==XThreadState.TERMINATED){
				threads.remove(activeThreadID);
				if(thread.getThreadState()==XThreadState.ERRORED){
					for(XThreadErroredListener threadErroredListener:threadErroredListeners){
						threadErroredListener.onThreadErrored(virtualMachine, thread);
					}
				}
				activeThreadID--;
			}
			int startThread = activeThreadID;
			do{
				activeThreadID++;
				if(threads.isEmpty()){
					break;
				}
				if(activeThreadID>=threads.size()){
					activeThreadID = 0;
				}
				if(activeThreadID==startThread)
					return null;
				thread = threads.get(activeThreadID);
				thread.sleepUpdate();
				if(thread.getThreadState()==XThreadState.ERRORED || thread.getThreadState()==XThreadState.TERMINATED){
					threads.remove(activeThreadID);
					if(activeThreadID<startThread){
						startThread--;
					}
					activeThreadID--;
				}
			}while(thread.getThreadState()==XThreadState.RUNNING);
		}
		activeThreadID++;
		if(activeThreadID>=threads.size()){
			activeThreadID = 0;
		}
		return thread;
	}
	
	public int run(int numInstructions, int numBlocks){
		while(numBlocks>0){
			newImportantInterrupt = false;
			XThread current = getNextInterrupt();
			if(current==null){
				current = getNextThread();
			}
			if(current==null){
				return numBlocks;
			}
			current.run(numInstructions);
			numBlocks--;
		}
		return 0;
	}
	
	public XThread start(String name, XMethod method, XGenericClass[] generics, long[] params) {
		XThread thread = new XThread(virtualMachine, name, method, generics, params, nextThreadID++);
		threads.add(thread);
		return thread;
	}
	
	public XThread interrupt(String name, byte[] userData, XMethod method, XGenericClass[] generics, long[] params){
		XThread interrupt = new XThread(virtualMachine, name, method, generics, params, nextThreadID++);
		interrupts.add(interrupt);
		interrupt.setUserData(userData);
		return interrupt;
	}
	
	public XThread importantInterrupt(String name, XMethod method, XGenericClass[] generics, long[] params){
		XThread interrupt = new XThread(virtualMachine, name, method, generics, params, nextThreadID++);
		interrupts.add(0, interrupt);
		newImportantInterrupt = true;
		return interrupt;
	}
	
	public String getNextDefaultThreadName() {
		return "Thread-"+nextThreadIDName++;
	}
	
	public boolean isNewImportantInterrupt(){
		return newImportantInterrupt;
	}

	public XThread getThread(int id) {
		for(XThread thread:threads){
			if(thread.getID()==id){
				return thread;
			}
		}
		for(XThread thread:interrupts){
			if(thread.getID()==id){
				return thread;
			}
		}
		throw new XRuntimeException("Unknown Thread");
	}
	
	public void registerInterruptTerminatedListener(XInterruptTerminatedListener interruptTerminatedListener){
		if(!interruptTerminatedListeners.contains(interruptTerminatedListener)){
			interruptTerminatedListeners.add(interruptTerminatedListener);
		}
	}
	
	public void registerThreadErroredListener(XThreadErroredListener threadErroredListener){
		if(!threadErroredListeners.contains(threadErroredListener)){
			threadErroredListeners.add(threadErroredListener);
		}
	}
}
