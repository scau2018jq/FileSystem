package autoField;

import java.util.List;

import javafx.scene.control.TextField;

/**
 * 自动提示组件构造器,调用这个方法去创建提示控件
 * @author w
 *
 */
public class AutoCompleteTextFieldBuilder {
	/**
	 * 将textField控件转换为可提示
	 */
	public static final AutoCompleteTextField build(TextField textField, List<String> cacheData) {
		return new AutoCompleteTextField(textField, cacheData);
	}

	public static final AutoCompleteTextField build(TextField textField) {
		return new AutoCompleteTextField(textField);
	}

}
