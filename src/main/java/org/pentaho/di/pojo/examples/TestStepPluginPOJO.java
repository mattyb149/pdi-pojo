package org.pentaho.di.pojo.examples;

import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.pojo.StepPluginPOJO;
import org.pentaho.di.pojo.annotation.ExcludeMeta;
import org.pentaho.di.pojo.annotation.NewField;
import org.pentaho.di.pojo.annotation.UI;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepMetaInterface;

@Step( id = "TestStepPluginPOJO", image = "test-step-plugin-pojo.png", name = "StepPluginPOJO Test",
    description = "Tests the StepPluginPOJO", categoryDescription = "Experimental" )
public class TestStepPluginPOJO extends StepPluginPOJO {
  
  private String testString;
  
  @ExcludeMeta
  private String testExcludeString;
  
  private int testInt;
  
  private boolean testBool;
  
  @UI(hint="TextVar")
  private boolean testBoolAsText;
  
  @UI(label="Cool bool: ")
  private boolean testBoolWithLabel;
  
  @NewField
  private String status;

  @Override
  public boolean processRow( StepMetaInterface smi, StepDataInterface sdi ) throws KettleException {
    Object[] r = getRow(); // get row, set busy!
    // no more input to be expected...
    if ( r == null ) {
      setOutputDone();
      return false;
    }

    putRow( getInputRowMeta(), r ); // copy row to possible alternate rowset(s).

    if ( checkFeedback( getLinesRead() ) ) {
      if ( isBasic() ) {
        logBasic( "Lines read" + getLinesRead() );
      }
    }

    return true;
  }
}
