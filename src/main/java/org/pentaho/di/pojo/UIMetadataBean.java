package org.pentaho.di.pojo;

import org.eclipse.swt.widgets.Control;

public class UIMetadataBean {

  private String label = null;

  private Class<? extends Control> control = null;
  
  private int uiStyle = 0;
  
  private String text = null;
  
  private String description = null;

  public UIMetadataBean() {

  }

  public UIMetadataBean( String label, Class<? extends Control> control, int uiStyle, String description ) {
    this.label = label;
    this.control = control;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel( String label ) {
    this.label = label;
  }

  public Class<? extends Control> getControl() {
    return control;
  }

  public void setControl( Class<? extends Control> control ) {
    this.control = control;
  }

  public int getUIStyle() {
    return uiStyle;
  }

  public void setUIStyle( int uiStyle ) {
    this.uiStyle = uiStyle;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription( String description ) {
    this.description = description;
  }

  public String getText() {
    return text;
  }

  public void setText( String text ) {
    this.text = text;
  }

}
