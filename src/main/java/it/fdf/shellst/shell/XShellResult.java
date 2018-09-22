package it.fdf.shellst.shell;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class XShellResult implements Consumer<String> {

	List<String> _lines = new ArrayList<>();
	private boolean _isSuccess;
	
	@Override
	public void accept(String aLine) {
		_lines.add(aLine);
	}

	public void setFailure() {
		_isSuccess = false;
	}

	public void setSuccess() {
		_isSuccess = true;
	}
	
	public boolean isSuccess() {
		return _isSuccess;
	}
	
	public boolean isFailure() {
		return !isSuccess();
	}
	
	public void print() {
		System.out.println(asString());
	}
	
	public String asString() {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < _lines.size(); i++) {
			result.append(_lines.get(i));
			if (i < _lines.size()-1) result.append("\n");
		}
		return result.toString();
	}
}
	

