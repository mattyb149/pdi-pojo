package org.pentaho.di.pojo;

import java.lang.reflect.Field;

import org.pentaho.di.core.row.ValueMetaInterface;

public class FieldMetadataBean {

  private Field field = null;

  private ValueMetaInterface valueMeta = null;

  private UIMetadataBean uiMetadata = null;

  private boolean newField = false;

  public FieldMetadataBean( Field field, ValueMetaInterface valueMeta, UIMetadataBean uiMetadata ) {
    this( field, valueMeta, uiMetadata, false );
  }

  public FieldMetadataBean( Field field, ValueMetaInterface valueMeta, UIMetadataBean uiMetadata, boolean newField ) {

    this.field = field;
    this.valueMeta = valueMeta;
    this.uiMetadata = uiMetadata;
    this.newField = newField;
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

  public boolean isNewField() {
    return newField;
  }

}
