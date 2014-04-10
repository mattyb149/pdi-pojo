package org.pentaho.di.pojo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.ResultFile;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.pojo.annotation.ExcludeMeta;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.BaseStepData.StepExecutionStatus;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.RowListener;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInjectionMetaEntry;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepListener;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInjectionInterface;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

public abstract class StepPluginPOJO extends BaseStepMeta implements StepMetaInterface, StepInterface {

  protected MoreAccessibleBaseStep baseStep = null;
  protected StepDataInterface stepDataInterface = null;
  protected List<FieldMetadataBean> metaFields = null;

  public StepPluginPOJO() {
    generateMetaFields();
  }

  @Override
  public StepInterface getStep( StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
      TransMeta transMeta, Trans trans ) {
    // if ( baseStep == null ) {
    baseStep = new MoreAccessibleBaseStep( stepMeta, stepDataInterface, copyNr, transMeta, trans );
    // }
    return this;
  }

  @Override
  public StepDataInterface getStepData() {
    /*
     * if ( stepDataInterface == null ) { stepDataInterface = new BaseStepData() { }; } return stepDataInterface;
     */
    return new BaseStepData() {
    };
  }

  public List<FieldMetadataBean> getMetaFields() {
    return metaFields;
  }

  public void setMetaFields( List<FieldMetadataBean> metaFields ) {
    this.metaFields = metaFields;
  }

  protected void generateMetaFields() {

    // Look for @ExcludeMeta, include all others
    Field[] fields = this.getClass().getDeclaredFields();
    if ( fields != null ) {
      metaFields = new ArrayList<FieldMetadataBean>( fields.length );

      for ( Field field : fields ) {
        if ( field.getAnnotation( ExcludeMeta.class ) == null ) {
          FieldMetadataBean fieldMetadata = StepPluginUtils.generateFieldMetadata( field );
          if ( fieldMetadata != null ) {
            metaFields.add( fieldMetadata );
            System.out.println( "Added meta field: " + field.getName() );
          }
        } else {
          System.out.println( "Excluded field: " + field.getName() );
        }
      }
    }
  }

  // ******************
  // Delegate methods
  // ******************

  public void addRowListener( RowListener arg0 ) {
    baseStep.addRowListener( arg0 );
  }

  public void addStepListener( StepListener arg0 ) {
    baseStep.addStepListener( arg0 );
  }

  public void batchComplete() throws KettleException {
    baseStep.batchComplete();
  }

  public boolean canProcessOneRow() {
    return baseStep.canProcessOneRow();
  }

  public void cleanup() {
    baseStep.cleanup();
  }

  public void copyVariablesFrom( VariableSpace arg0 ) {
    baseStep.copyVariablesFrom( arg0 );
  }

  public void dispose( StepMetaInterface arg0, StepDataInterface arg1 ) {
    baseStep.dispose( arg0, arg1 );
  }

  public String environmentSubstitute( String arg0 ) {
    return baseStep.environmentSubstitute( arg0 );
  }

  public String[] environmentSubstitute( String[] arg0 ) {
    return baseStep.environmentSubstitute( arg0 );
  }

  public String fieldSubstitute( String arg0, RowMetaInterface arg1, Object[] arg2 ) throws KettleValueException {
    return baseStep.fieldSubstitute( arg0, arg1, arg2 );
  }

  public boolean getBooleanValueOfVariable( String arg0, boolean arg1 ) {
    return baseStep.getBooleanValueOfVariable( arg0, arg1 );
  }

  public int getCopy() {
    return baseStep.getCopy();
  }

  public int getCurrentInputRowSetNr() {
    return baseStep.getCurrentInputRowSetNr();
  }

  public int getCurrentOutputRowSetNr() {
    return baseStep.getCurrentOutputRowSetNr();
  }

  public long getErrors() {
    return baseStep.getErrors();
  }

  public List<RowSet> getInputRowSets() {
    return baseStep.getInputRowSets();
  }

  public long getLinesInput() {
    return baseStep.getLinesInput();
  }

  public long getLinesOutput() {
    return baseStep.getLinesOutput();
  }

  public long getLinesRead() {
    return baseStep.getLinesRead();
  }

  public long getLinesRejected() {
    return baseStep.getLinesRejected();
  }

  public long getLinesUpdated() {
    return baseStep.getLinesUpdated();
  }

  public long getLinesWritten() {
    return baseStep.getLinesWritten();
  }

  public LogChannelInterface getLogChannel() {
    return baseStep.getLogChannel();
  }

  public IMetaStore getMetaStore() {
    return baseStep.getMetaStore();
  }

  public List<RowSet> getOutputRowSets() {
    return baseStep.getOutputRowSets();
  }

  public VariableSpace getParentVariableSpace() {
    return baseStep.getParentVariableSpace();
  }

  public String getPartitionID() {
    return baseStep.getPartitionID();
  }

  public long getProcessed() {
    return baseStep.getProcessed();
  }

  public Repository getRepository() {
    return baseStep.getRepository();
  }

  public Map<String, ResultFile> getResultFiles() {
    return baseStep.getResultFiles();
  }

  public Object[] getRow() throws KettleException {
    return baseStep.getRow();
  }

  public List<RowListener> getRowListeners() {
    return baseStep.getRowListeners();
  }

  public long getRuntime() {
    return baseStep.getRuntime();
  }

  public StepExecutionStatus getStatus() {
    return baseStep.getStatus();
  }

  public String getStepID() {
    return baseStep.getStepID();
  }

  public StepMeta getStepMeta() {
    return baseStep.getStepMeta();
  }

  public String getStepname() {
    return baseStep.getStepname();
  }

  public Trans getTrans() {
    return baseStep.getTrans();
  }

  public String getVariable( String arg0, String arg1 ) {
    return baseStep.getVariable( arg0, arg1 );
  }

  public String getVariable( String arg0 ) {
    return baseStep.getVariable( arg0 );
  }

  public void identifyErrorOutput() {
    baseStep.identifyErrorOutput();
  }

  public boolean init( StepMetaInterface arg0, StepDataInterface arg1 ) {
    return baseStep.init( arg0, arg1 );
  }

  public void initBeforeStart() throws KettleStepException {
    baseStep.initBeforeStart();
  }

  public void initializeVariablesFrom( VariableSpace arg0 ) {
    baseStep.initializeVariablesFrom( arg0 );
  }

  public void injectVariables( Map<String, String> arg0 ) {
    baseStep.injectVariables( arg0 );
  }

  public boolean isMapping() {
    return baseStep.isMapping();
  }

  public boolean isPartitioned() {
    return baseStep.isPartitioned();
  }

  public boolean isPaused() {
    return baseStep.isPaused();
  }

  public boolean isRunning() {
    return baseStep.isRunning();
  }

  public boolean isStopped() {
    return baseStep.isStopped();
  }

  public boolean isUsingThreadPriorityManagment() {
    return baseStep.isUsingThreadPriorityManagment();
  }

  public String[] listVariables() {
    return baseStep.listVariables();
  }

  public void markStart() {
    baseStep.markStart();
  }

  public void markStop() {
    baseStep.markStop();
  }

  public void pauseRunning() {
    baseStep.pauseRunning();
  }

  public abstract boolean processRow( StepMetaInterface arg0, StepDataInterface arg1 ) throws KettleException;

  public void putRow( RowMetaInterface arg0, Object[] arg1 ) throws KettleException {
    baseStep.putRow( arg0, arg1 );
  }

  public void removeRowListener( RowListener arg0 ) {
    baseStep.removeRowListener( arg0 );
  }

  public void resumeRunning() {
    baseStep.resumeRunning();
  }

  public int rowsetInputSize() {
    return baseStep.rowsetInputSize();
  }

  public int rowsetOutputSize() {
    return baseStep.rowsetOutputSize();
  }

  public void setCurrentInputRowSetNr( int arg0 ) {
    baseStep.setCurrentInputRowSetNr( arg0 );
  }

  public void setCurrentOutputRowSetNr( int arg0 ) {
    baseStep.setCurrentOutputRowSetNr( arg0 );
  }

  public void setErrors( long arg0 ) {
    baseStep.setErrors( arg0 );
  }

  public void setLinesRejected( long arg0 ) {
    baseStep.setLinesRejected( arg0 );
  }

  public void setMetaStore( IMetaStore arg0 ) {
    baseStep.setMetaStore( arg0 );
  }

  public void setOutputDone() {
    baseStep.setOutputDone();
  }

  public void setParentVariableSpace( VariableSpace arg0 ) {
    baseStep.setParentVariableSpace( arg0 );
  }

  public void setPartitionID( String arg0 ) {
    baseStep.setPartitionID( arg0 );
  }

  public void setPartitioned( boolean arg0 ) {
    baseStep.setPartitioned( arg0 );
  }

  public void setRepartitioning( int arg0 ) {
    baseStep.setRepartitioning( arg0 );
  }

  public void setRepository( Repository arg0 ) {
    baseStep.setRepository( arg0 );
  }

  public void setRunning( boolean arg0 ) {
    baseStep.setRunning( arg0 );
  }

  public void setStopped( boolean arg0 ) {
    baseStep.setStopped( arg0 );
  }

  public void setUsingThreadPriorityManagment( boolean arg0 ) {
    baseStep.setUsingThreadPriorityManagment( arg0 );
  }

  public void setVariable( String arg0, String arg1 ) {
    baseStep.setVariable( arg0, arg1 );
  }

  public void shareVariablesWith( VariableSpace arg0 ) {
    baseStep.shareVariablesWith( arg0 );
  }

  public void stopAll() {
    baseStep.stopAll();
  }

  public void stopRunning( StepMetaInterface arg0, StepDataInterface arg1 ) throws KettleException {
    baseStep.stopRunning( arg0, arg1 );
  }

  public boolean isDisposed() {
    return stepDataInterface.isDisposed();
  }

  public boolean isEmpty() {
    return stepDataInterface.isEmpty();
  }

  public boolean isFinished() {
    return stepDataInterface.isFinished();
  }

  public boolean isIdle() {
    return stepDataInterface.isIdle();
  }

  public boolean isInitialising() {
    return stepDataInterface.isInitialising();
  }

  public void setStatus( StepExecutionStatus arg0 ) {
    stepDataInterface.setStatus( arg0 );
  }

  // TODO ?
  public void check( List<CheckResultInterface> arg0, TransMeta arg1, StepMeta arg2, RowMetaInterface arg3,
      String[] arg4, String[] arg5, RowMetaInterface arg6, VariableSpace arg7, Repository arg8, IMetaStore arg9 ) {
    super.check( arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9 );
  }

  public Object clone() {
    // TODO
    Object retval = super.clone();
    return retval;
  }

  public String getDialogClassName() {
    return StepPluginPOJODialog.class.getName();
  }

  public void getFields( RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep,
      VariableSpace space, Repository repo, IMetaStore metaStore ) throws KettleStepException {
    // TODO
    super.getFields( inputRowMeta, name, info, nextStep, space, repo, metaStore );
  }

  // TODO
  public StepMetaInjectionInterface getStepMetaInjectionInterface() {
    return super.getStepMetaInjectionInterface();
  }

  @Override
  public String getXML() throws KettleException {
    StringBuffer retval = new StringBuffer( 300 );

    retval.append( "    <fields>" ).append( Const.CR );
    List<FieldMetadataBean> fieldBeans = getMetaFields();
    if ( fieldBeans != null ) {
      for ( FieldMetadataBean fieldBean : fieldBeans ) {
        try {
          Field field = fieldBean.getField();
          Object value = StepPluginUtils.getValueOfFieldFromObject( this, field );

          retval.append( "      <field>" ).append( Const.CR );
          retval.append( "        " ).append( XMLHandler.addTagValue( "name", fieldBean.getName() ) );
          retval.append( "        " ).append( XMLHandler.addTagValue( "value", value == null ? "" : value.toString() ) );
          retval.append( "      </field>" ).append( Const.CR );
        } catch ( Exception e ) {
          this.logError( "Couldn't determine the value of field " + fieldBean.getName(), e );
        }
      }
    }
    retval.append( "    </fields>" ).append( Const.CR );

    return retval.toString();
  }

  public void loadXML( Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore ) throws KettleXMLException {

    List<FieldMetadataBean> fieldBeans = getMetaFields();
    Node fields = XMLHandler.getSubNode( stepnode, "fields" );
    int nrfields = XMLHandler.countNodes( fields, "field" );
    HashMap<String, FieldMetadataBean> fieldMap = StepPluginUtils.getFieldMetadataBeansAsMap( fieldBeans );
    if ( fieldMap != null ) {
      for ( int i = 0; i < nrfields; i++ ) {

        Node fnode = XMLHandler.getSubNodeByNr( fields, "field", i );
        final String name = XMLHandler.getTagValue( fnode, "name" );
        FieldMetadataBean fieldBean = fieldMap.get( name );
        if ( fieldBean != null ) {
          try {
            StepPluginUtils
                .setValueOfFieldToObject( this, fieldBean.getField(), XMLHandler.getTagValue( fnode, "name" ) );
          } catch ( NoSuchFieldException e ) {
            throw new KettleXMLException( e );
          }
        } else {
          this.logBasic( "No such field: " + name + ", ignoring..." );
        }
      }
    }
  }

  public void readRep( Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases )
    throws KettleException {
    // TODO
    super.readRep( rep, metaStore, id_step, databases );
  }

  public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step )
    throws KettleException {
    // TODO
    super.saveRep( rep, metaStore, id_transformation, id_step );
  }

  public boolean supportsErrorHandling() {
    return super.supportsErrorHandling();
  }

  /**
   * @return the rowMeta
   */
  public RowMetaInterface getInputRowMeta() {
    return baseStep.getInputRowMeta();
  }

  /**
   * Checks if is basic.
   * 
   * @return true, if is basic
   */
  public boolean isBasic() {
    return baseStep.isBasic();
  }

  /**
   * Checks if is detailed.
   * 
   * @return true, if is detailed
   */
  public boolean isDetailed() {
    return baseStep.isDetailed();
  }

  /**
   * Checks if is debug.
   * 
   * @return true, if is debug
   */
  public boolean isDebug() {
    return baseStep.isDebug();
  }

  /**
   * Checks if is row level.
   * 
   * @return true, if is row level
   */
  public boolean isRowLevel() {
    return baseStep.isRowLevel();
  }

  /**
   * Log minimal.
   * 
   * @param message
   *          the message
   */
  public void logMinimal( String message ) {
    baseStep.logMinimal( message );
  }

  /**
   * Log minimal.
   * 
   * @param message
   *          the message
   * @param arguments
   *          the arguments
   */
  public void logMinimal( String message, Object... arguments ) {
    baseStep.logMinimal( message, arguments );
  }

  /**
   * Log basic.
   * 
   * @param message
   *          the message
   */
  public void logBasic( String message ) {
    baseStep.logBasic( message );
  }

  /**
   * Log basic.
   * 
   * @param message
   *          the message
   * @param arguments
   *          the arguments
   */
  public void logBasic( String message, Object... arguments ) {
    baseStep.logBasic( message, arguments );
  }

  /**
   * Log detailed.
   * 
   * @param message
   *          the message
   */
  public void logDetailed( String message ) {
    baseStep.logDetailed( message );
  }

  /**
   * Log detailed.
   * 
   * @param message
   *          the message
   * @param arguments
   *          the arguments
   */
  public void logDetailed( String message, Object... arguments ) {
    baseStep.logDetailed( message, arguments );
  }

  /**
   * Log debug.
   * 
   * @param message
   *          the message
   */
  public void logDebug( String message ) {
    baseStep.logDebug( message );
  }

  /**
   * Log debug.
   * 
   * @param message
   *          the message
   * @param arguments
   *          the arguments
   */
  public void logDebug( String message, Object... arguments ) {
    baseStep.logDebug( message, arguments );
  }

  /**
   * Log rowlevel.
   * 
   * @param message
   *          the message
   */
  public void logRowlevel( String message ) {
    baseStep.logRowlevel( message );
  }

  /**
   * Log rowlevel.
   * 
   * @param message
   *          the message
   * @param arguments
   *          the arguments
   */
  public void logRowlevel( String message, Object... arguments ) {
    baseStep.logRowlevel( message, arguments );
  }

  /**
   * Log error.
   * 
   * @param message
   *          the message
   */
  public void logError( String message ) {
    baseStep.logError( message );
  }

  /**
   * Log error.
   * 
   * @param message
   *          the message
   * @param e
   *          the e
   */
  public void logError( String message, Throwable e ) {
    baseStep.logError( message, e );
  }

  /**
   * Log error.
   * 
   * @param message
   *          the message
   * @param arguments
   *          the arguments
   */
  public void logError( String message, Object... arguments ) {
    baseStep.logError( message, arguments );
  }

  /**
   * Check feedback.
   * 
   * @param lines
   *          the lines
   * @return true, if successful
   */
  protected boolean checkFeedback( long lines ) {
    return baseStep.checkFeedback( lines, true );
  }

  private class MoreAccessibleBaseStep extends BaseStep implements StepInterface {
    public MoreAccessibleBaseStep( StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
        TransMeta transMeta, Trans trans ) {
      super( stepMeta, stepDataInterface, copyNr, transMeta, trans );
    }

    public boolean checkFeedback( long lines, boolean dummy ) {
      return this.checkFeedback( lines );
    }
  }

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return super.getName();
  }

  @Override
  public List<StepInjectionMetaEntry> getStepInjectionMetadataEntries( Class<?> arg0 ) {
    // TODO Auto-generated method stub
    return super.getStepInjectionMetadataEntries( arg0 );
  }

  @Override
  public String getTooltip( String attributeKey ) {
    // TODO Auto-generated method stub
    return super.getTooltip( attributeKey );
  }

  @Override
  public void setDefault() {
    // TODO Auto-generated method stub
  }
}
