package org.pentaho.di.pojo;

import java.lang.reflect.Field;

import org.pentaho.di.core.row.ValueMetaInterface;

public class FieldMetadataBean {
  
  private Field field = null;
  
  private ValueMetaInterface valueMeta = null;
  
  private UIMetadataBean uiMetadata = null;
  
  
  public FieldMetadataBean(Field field, ValueMetaInterface valueMeta, UIMetadataBean uiMetadata) {
    
    this.field = field;
    this.valueMeta = valueMeta;
    this.uiMetadata = uiMetadata;
  }
  
  public Field getField() {
    return field;
  }

  public String getName() {
    return field.getName();
  }

  public ValueMetaInterface getValueMeta() {
    return valueMeta;
  }

  public UIMetadataBean getUIMetadata() {
    return uiMetadata;
  }

  
}
