package org.pentaho.di.pojo;

import org.eclipse.swt.widgets.Widget;

public class UIMetadataBean {

  private String label = null;

  private Class<? extends Widget> widget = null;

  public UIMetadataBean() {

  }

  public UIMetadataBean( String label, Class<? extends Widget> widget ) {
    this.label = label;
    this.widget = widget;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel( String label ) {
    this.label = label;
  }

  public Class<? extends Widget> getWidget() {
    return widget;
  }

  public void setWidget( Class<? extends Widget> widget ) {
    this.widget = widget;
  }

}
