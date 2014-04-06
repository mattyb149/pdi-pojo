package org.pentaho.di.pojo;

import org.eclipse.swt.widgets.Control;

public class UIMetadataBean {

  private String label = null;

  private Class<? extends Control> control = null;

  public UIMetadataBean() {

  }

  public UIMetadataBean( String label, Class<? extends Control> control ) {
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

}
