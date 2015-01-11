package xscript.compiler.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.LinkedList;

class ArgReader {

	private final String[] args;
	
	private int index;
	
	private LinkedList<FileArgReader> fileArgReader = new LinkedList<FileArgReader>();
	
	public ArgReader(String[] args) {
		this.args = args;
	}
	
	public boolean hasNext(){
		if(fileArgReader.isEmpty()){
			return args.length>index;
		}else{
			return true;
		}
	}
	
	public String next() throws IOException{
		String next;
		while(true){
			if(fileArgReader.isEmpty()){
				if(args.length>index){
					next = args[index++];
				}else{
					return null;
				}
			}else{
				FileArgReader far = fileArgReader.getFirst();
				next = far.next();
				if(!far.hasNext()){
					far.close();
					fileArgReader.removeFirst();
				}
			}
			if(next.length()>1){
				if(next.charAt(0)=='@'){
					next = next.substring(1);
					if(next.charAt(0)=='@'){
						return next;
					}else{
						FileArgReader far = new FileArgReader(next);
						if(far.hasNext())
							fileArgReader.addFirst(far);
					}
				}else{
					return next;
				}
			}else{
				return next;
			}
		}
	}

	private static class FileArgReader {
		
		private final Reader r;
		
		private final StreamTokenizer st;
		
		FileArgReader(String file) throws IOException{
			r = new BufferedReader(new FileReader(file));
	        st = new StreamTokenizer(r);
	        st.resetSyntax();
	        st.wordChars(' ', 255);
	        st.whitespaceChars(0, ' ');
	        st.commentChar('#');
	        st.quoteChar('"');
	        st.quoteChar('\'');
	        st.nextToken();
		}
		
		boolean hasNext(){
			return st.ttype == StreamTokenizer.TT_EOF;
		}
		
        String next() throws IOException{
        	if(hasNext()){
        		String ret = st.sval;
        		st.nextToken();
        		return ret;
        	}
        	return null;
        }
        
        void close() throws IOException{
        	r.close();
        }
        
	}

}
