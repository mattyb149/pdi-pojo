package org.pentaho.di.pojo.examples;

import java.util.Date;

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
  
  @UI(label="Enter value")
  public String testString;
  
  @ExcludeMeta
  public String testExcludeString;
  
  public int testInt;
  
  public boolean testBool;
  
  @UI(hint="Checkbox")
  public boolean testBoolAsText;
  
  @UI(label="Cool bool", value="true")
  public boolean testBoolWithLabel;
  
  @UI(label="Start date", hint="Date")
  public Date startDate;
  
  @UI(label="End TOD", hint="Time")
  public Date endTime;
  
  @NewField
  public String status;

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
