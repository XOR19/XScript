package xscript.compiler.tree;

import java.util.ArrayList;
import java.util.List;

import xscript.compiler.tree.XTree.XTag;

public class XTreeSearch extends XTreeChanger {

	private XTag[] types;
	private List<XTree> founds = new ArrayList<XTree>();
	
	public XTreeSearch(XTag...types){
		this.types = types;
	}

	@Override
	protected <T extends XTree> T visitTree(T tree) {
		if(tree!=null){
			for(XTag type:types){
				if(type == tree.getTag()){
					founds.add(tree);
					break;
				}
			}
		}
		return super.visitTree(tree);
	}
	
	public List<XTree> getFounds(){
		return founds;
	}
	
}
