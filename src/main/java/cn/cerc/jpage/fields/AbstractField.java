package cn.cerc.jpage.fields;

import cn.cerc.jdb.core.Record;
import cn.cerc.jpage.common.BuildText;
import cn.cerc.jpage.common.BuildUrl;
import cn.cerc.jpage.common.DataView;
import cn.cerc.jpage.common.Expender;
import cn.cerc.jpage.common.HtmlText;
import cn.cerc.jpage.common.IField;
import cn.cerc.jpage.core.Component;
import cn.cerc.jpage.core.HtmlWriter;
import cn.cerc.jpage.form.Title;
import cn.cerc.jpage.grid.extjs.Column;
import cn.cerc.jui.vcl.columns.IColumn;
import net.sf.json.JSONObject;

public abstract class AbstractField extends Component implements IField, IColumn {
	private String name;
	private String shortName;
	private String align;
	private int width;
	// 数据库相关
	protected String field;
	// 自定义取值
	protected BuildText buildText;
	// 手机专用样式
	private String CSSClass_phone;
	// value
	private String value;
	// 只读否
	private boolean readonly;
	// 焦点否
	protected boolean autofocus;
	//
	protected boolean required;
	//
	protected String placeholder;
	// 正则过滤
	protected String pattern;
	//
	protected boolean hidden;
	// 角色
	protected String role;
	//
	protected String dialog;
	// 栏位说明
	private HtmlText mark;
	//
	private BuildUrl buildUrl;
	//
	protected DataView dataView;
	//
	private Expender expender;

	private boolean visible = true;
	
	protected String oninput;

	protected String onclick;
	// 由extGrid调用
	private Column column;

	public AbstractField(Component owner, String name, int width) {
		super(owner);
		if (owner != null) {
			if ((owner instanceof DataView)) {
				// throw new RuntimeException("owner not is DataView");
				this.dataView = (DataView) owner;
				dataView.addField(this);
				this.setReadonly(dataView.isReadonly());
			}
		}
		this.name = name;
		this.width = width;
	}

	@Deprecated
	public AbstractField(Component owner, String name, String field, int width) {
		this(owner, name, width);
		this.setField(field);
	}

	public HtmlText getMark() {
		return mark;
	}

	public AbstractField setMark(HtmlText mark) {
		this.mark = mark;
		return this;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public int getWidth() {
		if (this.getExpender() != null)
			return 0;
		return width;
	}

	public String getShortName() {
		if (this.shortName != null)
			return this.shortName;
		return this.getName();
	}

	public AbstractField setWidth(int width) {
		this.width = width;
		return this;
	}

	public AbstractField setShortName(String shortName) {
		this.shortName = shortName;
		return this;
	}

	public AbstractField setAlign(String align) {
		this.align = align;
		return this;
	}

	public AbstractField setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public String getAlign() {
		return align;
	}

	public String getName() {
		return name;
	}

	@Override
	public String getField() {
		return field;
	}

	public AbstractField setField(String field) {
		this.field = field;
		if (this.getId() == null || this.getId().startsWith("component"))
			this.setId(field);
		return this;
	}

	public abstract String getText(Record ds);

	/**
	 * 
	 * @param rs
	 *            当前记录集
	 * @return 返回输出文本
	 */
	protected String getDefaultText(Record rs) {
		if (rs == null)
			return null;
		if (buildText != null) {
			HtmlWriter html = new HtmlWriter();
			buildText.outputText(rs, html);
			return html.toString();
		}
		return rs.getString(getField());
	}

	public BuildText getBuildText() {
		return buildText;
	}

	public AbstractField createText(BuildText buildText) {
		this.buildText = buildText;
		return this;
	}

	public String getCSSClass_phone() {
		return CSSClass_phone;
	}

	public void setCSSClass_phone(String cSSClass_phone) {
		CSSClass_phone = cSSClass_phone;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public AbstractField setReadonly(boolean readonly) {
		this.readonly = readonly;
		return this;
	}

	public String getValue() {
		return value;
	}

	public AbstractField setValue(String value) {
		this.value = value;
		return this;
	}

	public boolean isAutofocus() {
		return autofocus;
	}

	public AbstractField setAutofocus(boolean autofocus) {
		this.autofocus = autofocus;
		return this;
	}

	public boolean isRequired() {
		return required;
	}

	public AbstractField setRequired(boolean required) {
		this.required = required;
		return this;
	}

	public String getPlaceholder() {
		return placeholder;
	}

	public AbstractField setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
		return this;
	}

	public String getPattern() {
		return pattern;
	}

	public AbstractField setPattern(String pattern) {
		this.pattern = pattern;
		return this;
	}

	public boolean isHidden() {
		return hidden;
	}

	public AbstractField setHidden(boolean hidden) {
		this.hidden = hidden;
		return this;
	}

	@Override
	public void output(HtmlWriter html) {
		Record dataSet = dataView != null ? dataView.getRecord() : null;
		if (this.hidden) {
			outputInput(html, dataSet);
		} else {
			html.println("<label for=\"%s\">%s</label>", this.getId(), this.getName() + "：");
			outputInput(html, dataSet);
			if (this.dialog != null) {
				html.print("<span>");
				html.print("<a href=\"javascript:%s('%s')\">", this.dialog, this.getId());
				html.print("<img src=\"images/select-pic.png\">");
				html.print("</a>");
				html.println("</span>");
			} else {
				html.println("<span></span>");
			}
		}
	}

	protected void outputInput(HtmlWriter html, Record dataSet) {
		if (this.hidden) {
			html.print("<input");
			html.print(" type=\"hidden\"");
			html.print(" name=\"%s\"", this.getId());
			html.print(" id=\"%s\"", this.getId());
			String value = this.getText(dataSet);
			if (value != null)
				html.print(" value=\"%s\"", value);
			html.println("/>");
		} else {
			html.print("<input");
			html.print(" type=\"text\"");
			html.print(" name=\"%s\"", this.getId());
			html.print(" id=\"%s\"", this.getId());
			String value = this.getText(dataSet);
			if (value != null)
				html.print(" value=\"%s\"", value);
			if (this.getValue() != null) {
				html.print(" value=\"%s\"", this.getValue());
			}
			if (this.isReadonly())
				html.print(" readonly=\"readonly\"");
			if (this.autofocus)
				html.print(" autofocus");
			if (this.required)
				html.print(" required");
			if (this.placeholder != null)
				html.print(" placeholder=\"%s\"", this.placeholder);
			if (this.pattern != null)
				html.print(" pattern=\"%s\"", this.pattern);
			if (this.CSSClass_phone != null)
				html.print(" class=\"%s\"", this.CSSClass_phone);
			if (this.oninput != null)
				html.print(" oninput=\"%s\"", this.oninput);
			if (this.onclick != null)
				html.print(" onclick=\"%s\"", this.onclick);
			html.println("/>");
		}
	}

	public String getDialog() {
		return dialog;
	}

	public AbstractField setDialog(String dialog) {
		this.dialog = dialog;
		return this;
	}

	public void createUrl(BuildUrl build) {
		this.buildUrl = build;
	}

	public DataView getDataView() {
		return dataView;
	}

	@Override
	public String getString() {
		if (dataView == null)
			throw new RuntimeException("owner is null.");
		if (dataView.getRecord() == null)
			throw new RuntimeException("owner.dataSet is null.");
		return dataView.getRecord().getString(this.getField());
	}

	public BuildUrl getBuildUrl() {
		return buildUrl;
	}

	public Title createTitle() {
		Title title = new Title();
		title.setName(this.getField());
		return title;
	}
	
	@Deprecated
	public Expender getExpender() {
		return expender;
	}

	@Deprecated
	public AbstractField setExpender(Expender expender) {
		this.expender = expender;
		return this;
	}

	public void updateField() {
		if (dataView != null) {
			String field = this.getId();
			if (field != null && !"".equals(field))
				dataView.updateValue(this.getId(), this.getField());
		}
	}

	public void setDataView(DataView dataView) {
		this.dataView = dataView;
	}

	public String getOninput() {
		return oninput;
	}

	public AbstractField setOninput(String oninput) {
		this.oninput = oninput;
		return this;
	}

	public String getOnclick() {
		return onclick;
	}

	public AbstractField setOnclick(String onclick) {
		this.onclick = onclick;
		return this;
	}

	public Column getColumn() {
		if (column == null) {
			column = new Column(this);
			column.setText(this.getName());
			column.setDataIndex(this.getField());
			column.setLocked(false);
			column.setSortable(true);
			if (!this.isReadonly()) {
				Editor editor = new Editor("textfield");
				column.setEditor(JSONObject.fromObject(editor).toString().replace("\"", "'"));
			}
		}
		return column;
	}

	public void setColumn(Column column) {
		this.column = column;
	}

	@Override
	public String getTitle() {
		return this.getName();
	}

	@Override
	public String format(Object value) {
		if (value instanceof Record)
			return this.getText((Record) value);
		else
			throw new RuntimeException("不支持的数据类型：" + value.getClass().getName());
	}

	public class Editor {
		private String xtype;

		public Editor(String xtype) {
			super();
			this.xtype = xtype;
		}

		public String getXtype() {
			return xtype;
		}

		public void setXtype(String xtype) {
			this.xtype = xtype;
		}
	}

	public boolean isVisible() {
		return visible;
	}

	public AbstractField setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}
}
