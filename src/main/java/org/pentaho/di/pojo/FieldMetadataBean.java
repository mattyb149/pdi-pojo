package org.pentaho.di.pojo;

import org.pentaho.di.core.row.ValueMetaInterface;

public class FieldMetadataBean {
  
  private String name = null;
  
  private ValueMetaInterface valueMeta = null;
  
  private UIMetadataBean uiMetadata = null;
  
  
  public FieldMetadataBean() {
    
  }
  
  public FieldMetadataBean(String name, ValueMetaInterface valueMeta, Object defaultValue, UIMetadataBean uiMetadata) {
    setName(name);
    setValueMeta(valueMeta);
    setUIMetadata(uiMetadata);
  }
  
  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public ValueMetaInterface getValueMeta() {
    return valueMeta;
  }

  public void setValueMeta( ValueMetaInterface valueMeta ) {
    this.valueMeta = valueMeta;
  }

  public UIMetadataBean getUIMetadata() {
    return uiMetadata;
  }

  public void setUIMetadata( UIMetadataBean uIMetadata ) {
    this.uiMetadata = uIMetadata;
  }

}
