package org.pentaho.di.pojo;

import java.awt.Composite;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

public class StepPluginPOJODialog extends BaseStepDialog implements StepDialogInterface {

  protected StepPluginPOJO input;

  protected Map<Widget, FieldMetadataBean> widgetMap = null;

  public StepPluginPOJODialog( Shell parent, Object baseStepMeta, TransMeta transMeta, String stepname ) {
    super( parent, (BaseStepMeta) baseStepMeta, transMeta, stepname );
    input = (StepPluginPOJO) baseStepMeta;
    widgetMap = new HashMap<Widget, FieldMetadataBean>( input.getMetaFields().size() );
  }

  @Override
  public String open() {
    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN );
    props.setLook( shell );
    setShellImage( shell, input );

    ModifyListener lsMod = new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        input.setChanged();
      }
    };
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

    // Create UI elements from field metadata
    List<FieldMetadataBean> fields = input.getMetaFields();
    if ( fields != null ) {
      for ( FieldMetadataBean field : fields ) {
        UIMetadataBean ui = field.getUIMetadata();
        Label label = new Label( shell, SWT.RIGHT );
        label.setText( ui.getLabel() );
        props.setLook( label );
        FormData fdlLabel = new FormData();
        fdlLabel.left = new FormAttachment( 0, 0 );
        fdlLabel.right = new FormAttachment( middle, -margin );
        fdlLabel.top = new FormAttachment( lastControl, margin );
        label.setLayoutData( fdlLabel );

        // TODO Need logic here for custom controls
        Class<? extends Control> controlClass = ui.getControl();
        Control control = null;

        try {
          if ( TextVar.class.isAssignableFrom( controlClass ) ) {
            control =
                controlClass.getConstructor( VariableSpace.class, Composite.class, Integer.TYPE ).newInstance(
                    transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
          } else {
            control =
                controlClass.getConstructor( Composite.class, Integer.TYPE ).newInstance( shell,
                    SWT.SINGLE | SWT.LEFT | SWT.BORDER );
          }
        } catch ( Exception e ) {
          new ErrorDialog( shell, "Error displaying dialog",
              "There was an error while attempting to display the dialog", e );
        }

        props.setLook( control );

        // TODO add appropriate listeners
        try {
          Method addModifyListener = controlClass.getMethod( "addModifyListener", ModifyListener.class );
          addModifyListener.invoke( control, lsMod );
        } catch ( Exception e ) {
          // No-op, the control doesn't listen to modify events
        }
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
      public void handleEvent( Event e ) {
        ok();
      }
    };

    lsCancel = new Listener() {
      public void handleEvent( Event e ) {
        cancel();
      }
    };

    wOK.addListener( SWT.Selection, lsOK );
    wPreview.addListener( SWT.Selection, lsPreview );
    wCancel.addListener( SWT.Selection, lsCancel );

    lsDef = new SelectionAdapter() {
      public void widgetDefaultSelected( SelectionEvent e ) {
        ok();
      }
    };

    wStepname.addSelectionListener( lsDef );
    // TODO add the others

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

    getData();
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
      new ErrorDialog( shell, "Error displaying dialog", "There was an error while attempting to display the dialog", e );
    }
  }

  /**
   * Copy information from the meta-data input to the dialog fields.
   */
  public void getData() {
    if ( isDebug() ) {
      logDebug( "getting fields info..." );
    }

    /*wLimit.setText( input.getRowLimit() );
    wNeverEnding.setSelection( input.isNeverEnding() );
    wInterval.setText( Const.NVL( input.getIntervalInMs(), "" ) );
    wRowTimeField.setText( Const.NVL( input.getRowTimeField(), "" ) );
    wLastTimeField.setText( Const.NVL( input.getLastTimeField(), "" ) );

    for ( int i = 0; i < input.getFieldName().length; i++ ) {
      if ( input.getFieldName()[i] != null ) {
        TableItem item = wFields.table.getItem( i );
        int col = 1;
        item.setText( col++, input.getFieldName()[i] );

        String type = input.getFieldType()[i];
        String format = input.getFieldFormat()[i];
        String length = input.getFieldLength()[i] < 0 ? "" : ( "" + input.getFieldLength()[i] );
        String prec = input.getFieldPrecision()[i] < 0 ? "" : ( "" + input.getFieldPrecision()[i] );

        String curr = input.getCurrency()[i];
        String group = input.getGroup()[i];
        String decim = input.getDecimal()[i];
        String def = input.getValue()[i];

        item.setText( col++, Const.NVL( type, "" ) );
        item.setText( col++, Const.NVL( format, "" ) );
        item.setText( col++, Const.NVL( length, "" ) );
        item.setText( col++, Const.NVL( prec, "" ) );
        item.setText( col++, Const.NVL( curr, "" ) );
        item.setText( col++, Const.NVL( decim, "" ) );
        item.setText( col++, Const.NVL( group, "" ) );
        item.setText( col++, Const.NVL( def, "" ) );
        item.setText( col++, input.isSetEmptyString()[i] ? BaseMessages.getString( PKG, "System.Combo.Yes" )
            : BaseMessages.getString( PKG, "System.Combo.No" ) );

      }
    }

    wFields.setRowNums();
    wFields.optWidth( true );*/

    wStepname.selectAll();
    wStepname.setFocus();
  }

  private void getInfo( StepPluginPOJO meta ) throws KettleException {
    /*meta.setRowLimit( wLimit.getText() );
    meta.setNeverEnding( wNeverEnding.getSelection() );
    meta.setIntervalInMs( wInterval.getText() );
    meta.setRowTimeField( wRowTimeField.getText() );
    meta.setLastTimeField( wLastTimeField.getText() );

    int nrfields = wFields.nrNonEmpty();

    meta.allocate( nrfields );

    // CHECKSTYLE:Indentation:OFF
    for ( int i = 0; i < nrfields; i++ ) {
      TableItem item = wFields.getNonEmpty( i );

      meta.getFieldName()[i] = item.getText( 1 );

      meta.getFieldFormat()[i] = item.getText( 3 );
      String slength = item.getText( 4 );
      String sprec = item.getText( 5 );
      meta.getCurrency()[i] = item.getText( 6 );
      meta.getDecimal()[i] = item.getText( 7 );
      meta.getGroup()[i] = item.getText( 8 );
      meta.isSetEmptyString()[i] =
          BaseMessages.getString( PKG, "System.Combo.Yes" ).equalsIgnoreCase( item.getText( 10 ) );

      meta.getValue()[i] = meta.isSetEmptyString()[i] ? "" : item.getText( 9 );
      meta.getFieldType()[i] = meta.isSetEmptyString()[i] ? "String" : item.getText( 2 );
      meta.getFieldLength()[i] = Const.toInt( slength, -1 );
      meta.getFieldPrecision()[i] = Const.toInt( sprec, -1 );
    }

    // Performs checks...
    
    */
  }
}
