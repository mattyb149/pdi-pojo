package org.pentaho.di.pojo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.eclipse.swt.widgets.Widget;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.ui.core.widget.TextVar;

public class StepPluginUtils {

  private static final String[] SWT_WIDGET_PREFIX = { "org.pentaho.di.ui.core.widget.", "org.eclipse.swt.widgets." };

  public static FieldMetadataBean generateFieldMetadata( Field field ) {
    FieldMetadataBean fieldMetadata = null;
    try {
      fieldMetadata = new FieldMetadataBean();
      fieldMetadata.setName( field.getName() );
      fieldMetadata.setValueMeta( getValueMetaForField( field ) );
      fieldMetadata.setUIMetadata( getUIForField( field ) );
      dumpFieldMetadata(fieldMetadata);
    } catch ( Exception e ) {
      //TODO
      e.printStackTrace(System.err);
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
      //TODO
      e.printStackTrace(System.err);
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
          label = field.getName();
        }
        ui.setLabel( label );

        String type = ( (org.pentaho.di.pojo.annotation.UI) uiAnno ).hint();
        if ( Const.isEmpty( type ) ) {
          type = "TextVar";
        }
        // Map Type to widget
        Class<? extends Widget> widgetClass = null;
        for ( String prefix : SWT_WIDGET_PREFIX ) {
          String widgetClassName = prefix + type;

          try {
            widgetClass = (Class<? extends Widget>) Class.forName( widgetClassName );
            ui.setWidget( widgetClass );
            break;
          } catch ( Exception e ) {
            // Couldn't find the widget, try again with a new prefix
          }
        }
        if ( widgetClass == null ) {
          // TODO
          System.err.println("Couldn't find any Widget matching "+type);
        }
      }
      else {
        // Set defaults
        ui.setLabel(field.getName());
        ui.setWidget( TextVar.class );
      }
    } catch ( Exception e ) {
      //TODO
      e.printStackTrace(System.err);
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
    System.out.println( "Widget type = " + ui.getWidget().getSimpleName() );
  }

}
