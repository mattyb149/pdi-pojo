package org.pentaho.di.pojo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Control;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.ui.core.widget.TextVar;

public class StepPluginUtils {

  private static final String[] SWT_CONTROL_PREFIX = { "org.pentaho.di.ui.core.widget.", "org.eclipse.swt.widgets." };

  // Mapping of Java primitive types/classes to Controls. The default is TextVar, this map is for exceptions
  @SuppressWarnings( "serial" )
  private static final HashMap<String, String> JAVA_2_WIDGET_MAP = new HashMap<String, String>() {
    {
      put( "boolean", "Checkbox" );
      put( "Boolean", "Checkbox" );
    }
  };

  // TODO add button styles
  @SuppressWarnings( "serial" )
  private static final HashMap<String, String> SWT_WIDGET_MAP = new HashMap<String, String>() {
    {
      put( "Checkbox", "Button" );
      put( "Radio", "Button" );
      put( "Button", "Button" );
      put( "TextVar", "TextVar" );
    }
  };

  public static FieldMetadataBean generateFieldMetadata( Field field ) {
    FieldMetadataBean fieldMetadata = null;
    try {
      fieldMetadata = new FieldMetadataBean( field, getValueMetaForField( field ), getUIForField( field ) );
      dumpFieldMetadata( fieldMetadata );
    } catch ( Exception e ) {
      // TODO
      e.printStackTrace( System.err );
    }
    return fieldMetadata;
  }

  public static ValueMetaInterface getValueMetaForField( Field field ) {
    ValueMetaInterface valueMeta = new ValueMetaString();
    try {
      Annotation valueMetaAnno = field.getAnnotation( org.pentaho.di.pojo.annotation.ValueMeta.class );
      if ( valueMetaAnno != null ) {
        String type = ( (org.pentaho.di.pojo.annotation.ValueMeta) valueMetaAnno ).type();
        if ( Const.isEmpty( type ) ) {
          type = "String";
        }
        valueMeta = ValueMetaFactory.createValueMeta( type, ValueMeta.getType( type ) );
      }
    } catch ( Exception e ) {
      // TODO
      e.printStackTrace( System.err );
    }

    return valueMeta;
  }

  @SuppressWarnings( "unchecked" )
  public static UIMetadataBean getUIForField( Field field ) {
    UIMetadataBean ui = new UIMetadataBean();
    try {
      Annotation uiAnno = field.getAnnotation( org.pentaho.di.pojo.annotation.UI.class );
      if ( uiAnno != null ) {

        String label = ( (org.pentaho.di.pojo.annotation.UI) uiAnno ).label();
        if ( Const.isEmpty( label ) ) {
          label = getLabelFromName( field.getName() );
        }
        ui.setLabel( label );

        String type = ( (org.pentaho.di.pojo.annotation.UI) uiAnno ).hint();
        if ( Const.isEmpty( type ) ) {
          System.out.println( "Looking for Widget for " + field.getType().getSimpleName() );
          String typeVal = JAVA_2_WIDGET_MAP.get( field.getType().getSimpleName() );
          if ( Const.isEmpty( typeVal ) ) {
            type = "TextVar";
          } else {
            type = SWT_WIDGET_MAP.get( typeVal );
          }
        } else {
          type = SWT_WIDGET_MAP.get( type );
        }

        // Map Type to control
        Class<? extends Control> controlClass = null;
        for ( String prefix : SWT_CONTROL_PREFIX ) {
          String controlClassName = prefix + type;

          try {
            controlClass = (Class<? extends Control>) Class.forName( controlClassName );
            ui.setControl( controlClass );
            System.out.println( "Found widget: " + controlClass.getName() );
            break;
          } catch ( Exception e ) {
            // Couldn't find the control, try again with a new prefix
          }
        }
        if ( controlClass == null ) {
          // TODO
          System.err.println( "Couldn't find any Control matching " + type );
        }
      } else {
        // Set defaults
        ui.setLabel( getLabelFromName( field.getName() ) );
        ui.setControl( TextVar.class );
      }
    } catch ( Exception e ) {
      // TODO
      e.printStackTrace( System.err );
    }
    return ui;
  }

  public static void dumpFieldMetadata( FieldMetadataBean fieldMetadata ) {
    System.out.println( "Name = " + fieldMetadata.getName() );
    System.out.println( "ValueMeta type = " + fieldMetadata.getValueMeta().getTypeDesc() );
    dumpUIMetadata( fieldMetadata.getUIMetadata() );
  }

  public static void dumpUIMetadata( UIMetadataBean ui ) {
    System.out.println( "Label = " + ui.getLabel() );
    System.out.println( "Control type = " + ui.getControl().getSimpleName() );
  }

  public static String getLabelFromName( String name ) {

    // Divide name by capital letters and capitalize the first letter
    return StringUtils.capitalize( name.replaceAll( "([A-Z])", " $1" ).toLowerCase() ).trim();

  }

}
