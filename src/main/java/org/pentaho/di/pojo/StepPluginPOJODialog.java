package org.pentaho.di.pojo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

public class StepPluginPOJODialog extends BaseStepDialog implements StepDialogInterface {

  protected StepPluginPOJO input;

  protected Map<FieldMetadataBean, Control> controlMap = null;

  ModifyListener lsMod = new ModifyListener() {
    public void modifyText( ModifyEvent e ) {
      input.setChanged();
    }
  };

  public StepPluginPOJODialog( Shell parent, Object baseStepMeta, TransMeta transMeta, String stepname ) {
    super( parent, (BaseStepMeta) baseStepMeta, transMeta, stepname );
    input = (StepPluginPOJO) baseStepMeta;
    controlMap = new HashMap<FieldMetadataBean, Control>( input.getMetaFields().size() );
  }

  @Override
  public String open() {
    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN );
    props.setLook( shell );
    setShellImage( shell, input );

    changed = input.hasChanged();

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    shell.setLayout( formLayout );

    // Get Dialog title as step plugin friendly name
    // Assume the plugin is annotation-based
    String dialogTitle = input.getClass().getSimpleName();
    Annotation stepAnnotation = input.getClass().getAnnotation( Step.class );
    if ( stepAnnotation != null ) {
      dialogTitle = ( (Step) stepAnnotation ).name();
    }
    shell.setText( dialogTitle );

    int middle = props.getMiddlePct();
    int margin = Const.MARGIN;

    // Filename line
    wlStepname = new Label( shell, SWT.RIGHT );
    wlStepname.setText( "Step name" );
    props.setLook( wlStepname );
    fdlStepname = new FormData();
    fdlStepname.left = new FormAttachment( 0, 0 );
    fdlStepname.right = new FormAttachment( middle, -margin );
    fdlStepname.top = new FormAttachment( 0, margin );
    wlStepname.setLayoutData( fdlStepname );
    wStepname = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    wStepname.setText( stepname );
    props.setLook( wStepname );
    wStepname.addModifyListener( lsMod );
    fdStepname = new FormData();
    fdStepname.left = new FormAttachment( middle, 0 );
    fdStepname.top = new FormAttachment( 0, margin );
    fdStepname.right = new FormAttachment( 100, 0 );
    wStepname.setLayoutData( fdStepname );
    Control lastControl = wStepname;

    // Create common SelectionAdapter and set it for step name control
    lsDef = new SelectionAdapter() {
      public void widgetDefaultSelected( SelectionEvent e ) {
        ok();
      }

      @Override
      public void widgetSelected( SelectionEvent arg0 ) {
        input.setChanged();
      }
    };

    wStepname.addSelectionListener( lsDef );

    // Create UI elements from field metadata
    List<FieldMetadataBean> fields = input.getMetaFields();
    if ( fields != null ) {
      for ( FieldMetadataBean field : fields ) {
        UIMetadataBean ui = field.getUIMetadata();
        Label label = new Label( shell, SWT.RIGHT );
        String labelText = ui.getLabel().trim();
        if ( !labelText.endsWith( ":" ) ) {
          labelText += ": ";
        } else {
          labelText += " ";
        }
        label.setText( labelText );
        props.setLook( label );
        FormData fdlLabel = new FormData();
        fdlLabel.left = new FormAttachment( 0, 0 );
        fdlLabel.right = new FormAttachment( middle, -margin );
        fdlLabel.top = new FormAttachment( lastControl, margin );
        label.setLayoutData( fdlLabel );

        Class<? extends Control> controlClass = ui.getControl();
        Control control = null;

        try {
          if ( TextVar.class.isAssignableFrom( controlClass ) ) {
            control =
                controlClass.getDeclaredConstructor( VariableSpace.class, Composite.class, Integer.TYPE ).newInstance(
                    transMeta, shell, ui.getUIStyle() );
          } else {
            control =
                controlClass.getDeclaredConstructor( Composite.class, Integer.TYPE ).newInstance( shell,
                    ui.getUIStyle() );
          }
          System.out.println( "Assigning widget " + control.getClass().getName() + " to field " + field.getName() );
          controlMap.put( field, control );

        } catch ( Exception e ) {
          new ErrorDialog( shell, "Error displaying dialog",
              "There was an error while attempting to display the dialog", e );
        }

        props.setLook( control );

        FormData fdLimit = new FormData();
        fdLimit.left = new FormAttachment( middle, 0 );
        fdLimit.top = new FormAttachment( lastControl, margin );
        fdLimit.right = new FormAttachment( 100, 0 );
        control.setLayoutData( fdLimit );
        lastControl = control;

      }
    }

    wOK = new Button( shell, SWT.PUSH );
    wOK.setText( "OK" );
    wCancel = new Button( shell, SWT.PUSH );
    wCancel.setText( "Cancel" );

    setButtonPositions( new Button[] { wOK, wCancel }, margin, null );

    // Add listeners
    lsOK = new Listener() {
      public void handleEvent( Event event ) {
        // Check for overridden ok method
        try {
          Method okMethod = input.getClass().getMethod( "ok" );
          okMethod.invoke( input );
        } catch ( Exception e ) {
          ok();
        }
      }
    };

    lsCancel = new Listener() {
      public void handleEvent( Event event ) {
        // Check for overridden cancel method
        try {
          Method cancelMethod = input.getClass().getMethod( "cancel" );
          cancelMethod.invoke( input );
        } catch ( Exception e ) {
          cancel();
        }
      }
    };

    wOK.addListener( SWT.Selection, lsOK );
    wCancel.addListener( SWT.Selection, lsCancel );

    // Detect X or ALT-F4 or something that kills this window...
    shell.addShellListener( new ShellAdapter() {
      public void shellClosed( ShellEvent e ) {
        cancel();
      }
    } );

    lsResize = new Listener() {
      public void handleEvent( Event event ) {
        /*
         * Point size = shell.getSize(); wFields.setSize( size.x - 10, size.y - 50 ); wFields.table.setSize( size.x -
         * 10, size.y - 50 ); wFields.redraw();
         */
      }
    };
    shell.addListener( SWT.Resize, lsResize );

    // Set the shell size, based upon previous time...
    setSize();

    populateWidgets();
    input.setChanged( changed );

    shell.open();
    while ( !shell.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }

    return stepname;
  }

  private void cancel() {
    stepname = null;
    input.setChanged( changed );
    dispose();
  }

  private void ok() {
    if ( Const.isEmpty( wStepname.getText() ) ) {
      return;
    }

    stepname = wStepname.getText(); // return value
    try {
      getInfo( input ); // to put the content on the input structure for real if all is well.
      dispose();
    } catch ( KettleException e ) {
      new ErrorDialog( shell, "Error displaying dialog",
          "There was an error while attempting to dispose of the dialog", e );
    }
  }

  /**
   * Copy information from the meta-data input to the dialog fields.
   */
  public void populateWidgets() {
    if ( isDebug() ) {
      logDebug( "getting fields info..." );
    }

    // Call the bean methods to get the info
    List<FieldMetadataBean> fields = input.getMetaFields();
    if ( fields != null ) {
      for ( FieldMetadataBean fieldBean : fields ) {
        Field field = fieldBean.getField();
        try {
          ValueMetaInterface valueMeta = fieldBean.getValueMeta();
          Object obj = StepPluginUtils.getValueOfFieldFromObject( input, field );
          System.out.println( "StepPluginUtils.getValueOfFieldFromObject(input," + field.getName() + ") = " + obj );

          // Set default value if obj is null
          // TODO use ui.getText() for text widgets
          if ( obj == null ) {
            obj = field.getType().newInstance();
          }

          Control control = controlMap.get( fieldBean );
          // TODO? Replace these hacks to promote shorts and ints with something more robust?
          if ( Integer.class.isAssignableFrom( obj.getClass() ) ) {
            obj = new Long( ( (Integer) obj ).longValue() );
          } else if ( Short.class.isAssignableFrom( obj.getClass() ) ) {
            obj = new Long( ( (Short) obj ).longValue() );
          } else {
            obj = valueMeta.convertData( valueMeta, obj );
          }

          System.out.println( "Setting widget for " + field.getName() + " to "
              + ( obj == null ? "null!" : obj.toString() ) );

          // DateTime widgets have different mutators, handle those specially
          if ( DateTime.class.isAssignableFrom( control.getClass() ) ) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime( valueMeta.getDate( obj ) );
            if ( fieldBean.getUIMetadata().getUIHint().equalsIgnoreCase( "time" ) ) {
              DateTime timeWidget = ( (DateTime) control );
              timeWidget.setHours( calendar.get( Calendar.HOUR ) );
              timeWidget.setMinutes( calendar.get( Calendar.MINUTE ) );
              timeWidget.setSeconds( calendar.get( Calendar.SECOND ) );

            } else {
              // default to date picker
              DateTime dateWidget = ( (DateTime) control );
              dateWidget.setYear( calendar.get( Calendar.YEAR ) );
              dateWidget.setMonth( calendar.get( Calendar.MONTH ) );
              dateWidget.setDay( calendar.get( Calendar.DAY_OF_MONTH ) );

            }
          } else {
            // Set selection if possible
            try {
              Method setSelection = control.getClass().getMethod( "setSelection", boolean.class );
              setSelection.invoke( control, valueMeta.getBoolean( obj ) );
            } catch ( Exception e ) {
              // This control has no setSelection method, keep calm and carry on

              // Set text if possible
              try {
                Method setText = control.getClass().getMethod( "setText", String.class );
                setText.invoke( control, Const.NVL( valueMeta.getString( obj ), "" ) );
              } catch ( Exception e2 ) {
                // This control has no setText method, keep calm and carry on
              }
            }
          }

          // Set tooltip text
          control.setToolTipText( fieldBean.getUIMetadata().getDescription() );

          // Add appropriate listeners now that the fields have been populated
          try {
            Method addModifyListener = control.getClass().getMethod( "addModifyListener", ModifyListener.class );
            addModifyListener.invoke( control, lsMod );
          } catch ( Exception e ) {
            // No-op, the control doesn't listen to modify events
          }
          try {
            Method addSelectionListener =
                control.getClass().getMethod( "addSelectionListener", SelectionListener.class );
            addSelectionListener.invoke( control, lsDef );
          } catch ( Exception e ) {
            // No-op, the control doesn't listen to selection events
          }

        } catch ( Exception e ) {
          logError( "Couldn't get value for field: " + ( field == null ? "null" : field.getName() ), e );
        }

      }
    } else {
      System.out.println( "No fields!!" );
    }

    wStepname.selectAll();
    wStepname.setFocus();
  }

  private void getInfo( StepPluginPOJO meta ) throws KettleException {
    System.out.println( "In getInfo()" );

    // Call the bean methods to get the info
    List<FieldMetadataBean> fields = input.getMetaFields();
    if ( fields != null ) {
      for ( FieldMetadataBean fieldBean : fields ) {
        Field field = fieldBean.getField();
        try {
          ValueMetaInterface valueMeta = fieldBean.getValueMeta();
          String objString = null;

          Control control = controlMap.get( fieldBean );
          if ( control == null ) {
            System.err.println( "No widget for " + field.getName() );
          }

          // Get selection if possible
          try {
            Method getSelection = control.getClass().getMethod( "getSelection" );
            objString = Boolean.toString( (Boolean) getSelection.invoke( control ) );
            System.out.println( field.getName() + ".getSelection() = " + objString );
          } catch ( Exception e ) {
            // This control has no getSelection method, keep calm and carry on
          }

          if ( objString == null ) {
            // Get text if possible, and set tooltip text
            try {
              Method getText = control.getClass().getMethod( "getText" );
              objString = (String) getText.invoke( control );
              System.out.println( "(" + field.getName() + ") " + control.getClass().getSimpleName() + ".getText() = "
                  + objString );
            } catch ( Exception e ) {
              // This control has no getText method, keep calm and carry on
            }
          }

          if ( DateTime.class.isAssignableFrom( control.getClass() ) ) {
            // Get date/time
            try {
              String uiHint = fieldBean.getUIMetadata().getUIHint();
              if ( uiHint.equalsIgnoreCase( "time" ) ) {
                Method getHour = control.getClass().getMethod( "getHours" );
                Method getMinute = control.getClass().getMethod( "getMinutes" );
                Method getSecond = control.getClass().getMethod( "getSeconds" );
                objString =
                    String.format( "%02d:%02d:%02d", getHour.invoke( control ), getMinute.invoke( control ), getSecond
                        .invoke( control ) );
                System.out.println( field.getName() + ".getTime() = " + objString );
              } else {
                Method getYear = control.getClass().getMethod( "getYear" );
                Method getMonth = control.getClass().getMethod( "getMonth" );
                Method getDay = control.getClass().getMethod( "getDay" );
                objString =
                    String.format( "%02d/%02d/%02d 00:00:00.000", getYear.invoke( control ), ( (Integer) getMonth
                        .invoke( control ) ) + 1, getDay.invoke( control ) );
                System.out.println( field.getName() + ".getDate() = " + objString );
              }
            } catch ( Exception e ) {
              // This control has no getter method, keep calm and carry on
              e.printStackTrace( System.err );
            }
          }

          Object obj = valueMeta.convertData( new ValueMetaString(), objString );
          System.out.println( "After convert, obj class = " + obj.getClass().getSimpleName() );

          // TODO API methods, better hack?
          if ( Long.class.isAssignableFrom( obj.getClass() ) && Integer.TYPE.isAssignableFrom( field.getType() ) ) {
            obj = ( (Long) obj ).intValue();
          }
          System.out.println( "Field type = " + field.getType().getSimpleName() + ", value type = "
              + obj.getClass().getSimpleName() );

          System.out.println( "Setting value of " + field.getName() + " to " + obj );
          StepPluginUtils.setValueOfFieldToObject( input, field, obj );
        } catch ( Exception e ) {
          logError( "Couldn't save field: " + ( field == null ? "null" : field.getName() ), e );
        }
      }
    }
  }
}
