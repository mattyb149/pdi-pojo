package org.pentaho.di.pojo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaBase;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.ui.core.widget.TextVar;

public class StepPluginUtils {

  private static final String[] SWT_CONTROL_PREFIX = { "org.pentaho.di.ui.core.widget.", "org.eclipse.swt.widgets." };

  // Mapping of Java primitive types to ValueMeta primitive (built-in) types.
  // The default is ValueMetaString, this map is for exceptions.
  @SuppressWarnings( "serial" )
  private static final HashMap<String, String> JAVA_2_VALUEMETA_MAP = new HashMap<String, String>() {
    {
      put( "boolean", ValueMetaFactory.getValueMetaName( ValueMetaInterface.TYPE_BOOLEAN ) );
      put( "Boolean", ValueMetaFactory.getValueMetaName( ValueMetaInterface.TYPE_BOOLEAN ) );
      put( "Date", ValueMetaFactory.getValueMetaName( ValueMetaInterface.TYPE_DATE ) );
      put( "Time", ValueMetaFactory.getValueMetaName( ValueMetaInterface.TYPE_TIMESTAMP ) );
      put( "InetAddress", ValueMetaFactory.getValueMetaName( ValueMetaInterface.TYPE_INET ) );
      put( "short", ValueMetaFactory.getValueMetaName( ValueMetaInterface.TYPE_INTEGER ) );
      put( "Short", ValueMetaFactory.getValueMetaName( ValueMetaInterface.TYPE_INTEGER ) );
      put( "int", ValueMetaFactory.getValueMetaName( ValueMetaInterface.TYPE_INTEGER ) );
      put( "Integer", ValueMetaFactory.getValueMetaName( ValueMetaInterface.TYPE_INTEGER ) );
      put( "long", ValueMetaFactory.getValueMetaName( ValueMetaInterface.TYPE_INTEGER ) );
      put( "BigInteger", ValueMetaFactory.getValueMetaName( ValueMetaInterface.TYPE_BIGNUMBER ) );
      put( "float", ValueMetaFactory.getValueMetaName( ValueMetaInterface.TYPE_BIGNUMBER ) );
      put( "Float", ValueMetaFactory.getValueMetaName( ValueMetaInterface.TYPE_NUMBER ) );
      put( "double", ValueMetaFactory.getValueMetaName( ValueMetaInterface.TYPE_NUMBER ) );
      put( "Double", ValueMetaFactory.getValueMetaName( ValueMetaInterface.TYPE_NUMBER ) );
      // TODO more?
    }
  };

  // Mapping of Java primitive types/classes to Controls. The default is TextVar, this map is for exceptions
  @SuppressWarnings( "serial" )
  private static final HashMap<String, String> JAVA_2_WIDGET_MAP = new HashMap<String, String>() {
    {
      put( "boolean", "Checkbox" );
      put( "Boolean", "Checkbox" );
      put( "Date", "Date" );
    }
  };

  @SuppressWarnings( "serial" )
  private static final HashMap<String, String> SWT_WIDGET_MAP = new HashMap<String, String>() {
    {
      put( "Checkbox", "Button" );
      put( "Radio", "Button" );
      put( "Button", "Button" );
      put( "Date", "DateTime" );
      put( "Time", "DateTime" );
      put( "TextVar", "TextVar" );
    }
  };

  @SuppressWarnings( "serial" )
  private static final HashMap<String, Integer> SWT_STYLE_MAP = new HashMap<String, Integer>() {
    {
      put( "Checkbox", SWT.CHECK );
      put( "Radio", SWT.RADIO );
      put( "Button", SWT.PUSH | SWT.CENTER );
      put( "Date", SWT.CALENDAR );
      put( "Time", SWT.TIME );
      put( "TextVar", SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    }
  };

  public static FieldMetadataBean generateFieldMetadata( Field field ) {
    FieldMetadataBean fieldMetadata = null;
    try {
      fieldMetadata = new FieldMetadataBean( field, getValueMetaForField( field ), getUIForField( field ) );
      // dumpFieldMetadata( fieldMetadata );
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
      } else {
        // Determine ValueMeta type based on Java type
        String type = JAVA_2_VALUEMETA_MAP.get( field.getType().getSimpleName() );
        if ( Const.isEmpty( type ) ) {
          // Default to ValueMetaString
          type = ValueMetaFactory.getValueMetaName( ValueMetaInterface.TYPE_STRING );
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

        // Set label
        String label = ( (org.pentaho.di.pojo.annotation.UI) uiAnno ).label();
        if ( Const.isEmpty( label ) ) {
          label = getLabelFromName( field.getName() );
        }
        ui.setLabel( label );

        // Set text
        String text = Const.NVL( ( (org.pentaho.di.pojo.annotation.UI) uiAnno ).value(), "" );
        /*
         * if ( Const.isEmpty( text ) ) { text = label; }
         */
        ui.setValue( text );

        // Set tooltip text (aka description)
        String description = ( (org.pentaho.di.pojo.annotation.UI) uiAnno ).description();
        if ( Const.isEmpty( description ) ) {
          description = label;
        }
        ui.setDescription( description );

        String uiType = ( (org.pentaho.di.pojo.annotation.UI) uiAnno ).hint();
        String widgetType = "TextVar";
        if ( Const.isEmpty( uiType ) ) {
          // System.out.println( "Looking for Widget for " + field.getType().getSimpleName() );
          uiType = JAVA_2_WIDGET_MAP.get( field.getType().getSimpleName() );
          if ( !Const.isEmpty( uiType ) ) {
            widgetType = SWT_WIDGET_MAP.get( uiType );
          }
        } else {
          // Save off hint
          ui.setUIHint( uiType );
          widgetType = SWT_WIDGET_MAP.get( uiType );
        }

        // Map Type to control
        Class<? extends Control> controlClass = null;
        for ( String prefix : SWT_CONTROL_PREFIX ) {
          String controlClassName = prefix + widgetType;

          try {
            controlClass = (Class<? extends Control>) Class.forName( controlClassName );
            ui.setControl( controlClass );
            // System.out.println( "Found widget: " + controlClass.getName() );
            System.out.println( String.format( "Applying style: %x for type %s", SWT_STYLE_MAP.get( uiType ), uiType ) );
            ui.setUIStyle( SWT_STYLE_MAP.get( uiType ) );
            break;
          } catch ( Exception e ) {
            // Couldn't find the control, try again with a new prefix
          }
        }
        if ( controlClass == null ) {
          // TODO
          System.err.println( "Couldn't find any Control matching " + widgetType );
        }
      } else {
        // Set defaults
        ui.setLabel( getLabelFromName( field.getName() ) );
        ui.setControl( TextVar.class );
        ui.setUIStyle( SWT.SINGLE | SWT.LEFT | SWT.BORDER );
      }
    } catch ( Exception e ) {
      // TODO
      e.printStackTrace( System.err );
    }

    // Set description to label
    if ( ui != null ) {
      if ( Const.isEmpty( ui.getValue() ) ) {
        ui.setValue( ui.getLabel() );
      }
      if ( Const.isEmpty( ui.getDescription() ) ) {
        ui.setDescription( ui.getLabel() );
      }
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
    System.out.println( "Style = " + ui.getUIStyle() );
  }

  public static String getLabelFromName( String name ) {

    // Divide name by capital letters and capitalize the first letter
    return StringUtils.capitalize( name.replaceAll( "([A-Z])", " $1" ).toLowerCase() ).trim();

  }

  public static Object getValueOfFieldFromObject( Object obj, Field field ) throws NoSuchFieldException {
    // Try the getter method first
    try {
      Method getterMethod = obj.getClass().getDeclaredMethod( "get" + StringUtils.capitalize( field.getName() ) );
      Object retObj = getterMethod.invoke( obj );
      System.out.println( "Returning " + retObj + " for call to " + getterMethod.getName() );
      return retObj;
    } catch ( Exception e ) {
      // No getter method (or its not the right one), keep calm and carry on
    }

    // Try to change accessibility just in case
    try {
      field.setAccessible( true );
    } catch ( Exception e ) {
      // Couldn't change the accessibility, keep calm and carry on
    }

    // Try direct access to the field
    try {
      return field.get( obj );
    } catch ( Exception e ) {
      throw new NoSuchFieldException( e.getMessage() );
    }
  }

  public static void setValueOfFieldToObject( Object obj, Field field, Object value ) throws KettleException {
    System.out.println( "Attempting to set field " + field.getName() + " to " + value );
    // Try the setter method first
    try {
      Method setterMethod =
          obj.getClass().getDeclaredMethod( "set" + StringUtils.capitalize( field.getName() ), value.getClass() );
      setterMethod.invoke( obj, value );
      return;
    } catch ( NoSuchMethodException nsme ) {
      // No setter method, keep calm and carry on
    } catch ( Exception e ) {
      System.err.println( "Couldn't set field " + field.getName() + " to " + value );
      e.printStackTrace();
    }

    // Try to change accessibility just in case
    try {
      field.setAccessible( true );
    } catch ( Exception e ) {
      // Couldn't set accessibility, keep calm and carry on
    }

    // Try direct access to the field
    try {
      field.set( obj, value );
    } catch ( Exception e ) {
      throw new KettleException( "Can't set field " + field.getName() + " to " + value, e );
    }
  }

  public static HashMap<String, FieldMetadataBean> getFieldMetadataBeansAsMap( Collection<FieldMetadataBean> beans ) {
    HashMap<String, FieldMetadataBean> fieldMap = null;

    if ( beans != null ) {
      fieldMap = new HashMap<String, FieldMetadataBean>( beans.size() );
      for ( FieldMetadataBean bean : beans ) {
        fieldMap.put( bean.getName(), bean );
      }
    }
    return fieldMap;

  }

  public static String getValueAsString( FieldMetadataBean fieldBean, Object value ) {
    // TODO more special formatted types?
    if ( value instanceof Date ) {
      SimpleDateFormat sdf = new SimpleDateFormat(ValueMetaBase.DEFAULT_DATE_FORMAT_MASK);
      return sdf.format((Date) value);
    }
    return value.toString();
  }
}
