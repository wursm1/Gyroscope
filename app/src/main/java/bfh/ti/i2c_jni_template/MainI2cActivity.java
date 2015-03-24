/*
 ***************************************************************************
 * \brief   Embedded Android I2C Exercise 5.2
 *	        This sample program shows how to use the I2C library.
 *			The program reads the temperature from the MCP9802 sensor
 *			and show the value on the display  
 *
 *	        Only a minimal error handling is implemented.
 * \file    MainI2cActivity.java
 * \version 1.0
 * \date    06.03.2014
 * \author  Martin Aebersold
 *
 * \remark  Last Modifications:
 * \remark  V1.0, AOM1, 06.03.2014
 ***************************************************************************
 */

package bfh.ti.i2c_jni_template;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;

public class MainI2cActivity extends Activity
{
    /* L3GD20 Registers pointers */

    // DEFAULT TYPE
    private static final char L3GD20_REGISTER_WHO_AM_I = 0x0F; // 11010100 r
    private static final char L3GD20_REGISTER_CTRL_REG1 = 0x20; // 00000111 rw
    private static final char L3GD20_REGISTER_CTRL_REG2 = 0x21; // 00000000 rw
    private static final char L3GD20_REGISTER_CTRL_REG3 = 0x22; // 00000000 rw
    private static final char L3GD20_REGISTER_CTRL_REG4 = 0x23; // 00000000 rw
    private static final char L3GD20_REGISTER_CTRL_REG5 = 0x24; // 00000000 rw
    private static final char L3GD20_REGISTER_REFERENCE = 0x25; // 00000000 rw
    private static final char L3GD20_REGISTER_OUT_TEMP = 0x26; // r
    private static final char L3GD20_REGISTER_STATUS_REG = 0x27; // r
    private static final char L3GD20_REGISTER_OUT_X_L = 0x28; // r
    private static final char L3GD20_REGISTER_OUT_X_H = 0x29; // r
    private static final char L3GD20_REGISTER_OUT_Y_L = 0x2A; // r
    private static final char L3GD20_REGISTER_OUT_Y_H = 0x2B; // r
    private static final char L3GD20_REGISTER_OUT_Z_L = 0x2C; // r
    private static final char L3GD20_REGISTER_OUT_Z_H = 0x2D; // r
    private static final char L3GD20_REGISTER_FIFO_CTRL_REG = 0x2E; // 00000000 rw
    private static final char L3GD20_REGISTER_FIFO_SRC_REG = 0x2F; // r
    private static final char L3GD20_REGISTER_INT1_CFG = 0x30; // 00000000 rw
    private static final char L3GD20_REGISTER_INT1_SRC = 0x31; // r
    private static final char L3GD20_REGISTER_TSH_XH = 0x32; // 00000000 rw
    private static final char L3GD20_REGISTER_TSH_XL = 0x33; // 00000000 rw
    private static final char L3GD20_REGISTER_TSH_YH = 0x34; // 00000000 rw
    private static final char L3GD20_REGISTER_TSH_YL = 0x35; // 00000000 rw
    private static final char L3GD20_REGISTER_TSH_ZH = 0x36; // 00000000 rw
    private static final char L3GD20_REGISTER_TSH_ZL = 0x37; // 00000000 rw
    private static final char L3GD20_REGISTER_INT1_DURATION = 0x38; // 00000000 rw
  /* Sensor Configuration Register Bits */
  private static final char MCP9800_12_BIT = 0x60;

  /* i2c Address of MCP9802 device */
  private static final char L3GD20_I2C_ADDR = 0x6A;

     /* i2c device file name */
  private static final String MCP9800_FILE_NAME = "/dev/i2c-3";

  I2C i2c;
  int[] i2cCommBuffer = new int[16];
  int fileHande;

  int Device_id;
  int temp;
  double x_data;
  double y_data;
  double z_data;

  double x_angle;

  double drift = 0.785;

  Timer timer;
  MyTimerTask myTimerTask;

  /* Define widgets */
  TextView textViewX;
  TextView textViewY;
  TextView textViewZ;

  /* Temperature Degrees Celsius text symbol */
  private static final String DEGREE_SYMBOL = "\u2103";

  @Override
  protected void onCreate(Bundle savedInstanceState)
   {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main_i2c);

    textViewX = (TextView) findViewById(R.id.textViewX);
    textViewY = (TextView) findViewById(R.id.textViewY);
    textViewZ = (TextView) findViewById(R.id.textViewZ);

    timer = new Timer();
    myTimerTask = new MyTimerTask();
    timer.schedule(myTimerTask, 0, 10);

   }

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	 {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_i2c, menu);
		return true;
	}

   /*
    * 	(non-Javadoc)
    * @see android.app.Activity#onStop()
    */
   protected void onStop()
	{
	 android.os.Process.killProcess(android.os.Process.myPid());
	 finish();
	 super.onStop();
	}

    class MyTimerTask extends TimerTask
    {
        @Override
        public void run()
        {

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    	/* Instantiate the new i2c device */
                    i2c = new I2C();

	/* Open the i2c device */
                    //fileHande = i2c.open(MCP9800_FILE_NAME);
                    fileHande = i2c.open(MCP9800_FILE_NAME);

	/* Set the I2C slave address for all subsequent I2C device transfers */
                    //i2c.SetSlaveAddress(fileHande, MCP9800_I2C_ADDR);
                    i2c.SetSlaveAddress(fileHande, L3GD20_I2C_ADDR);

	/* Setup i2c buffer for the configuration register */
                    i2cCommBuffer[0] = L3GD20_REGISTER_CTRL_REG4;
                    i2cCommBuffer[1] = 0;
                    i2c.write(fileHande, i2cCommBuffer, 2);

                    i2cCommBuffer[0] = L3GD20_REGISTER_CTRL_REG1;
                    i2cCommBuffer[1] = 0x6F;
                    i2c.write(fileHande, i2cCommBuffer, 2);

	/* Setup mcp9800 register to read the temperature */
                    i2cCommBuffer[0] = L3GD20_REGISTER_OUT_X_L | (1 << 7);
                    i2c.write(fileHande, i2cCommBuffer, 1);


                    i2c.read(fileHande, i2cCommBuffer, 6);
                    x_data = ((i2cCommBuffer[1] << 8) | i2cCommBuffer[0]);
                    if(x_data > 32768){
                        x_data -= 65536;
                    }
                    x_data /= 250;
                    y_data = ((i2cCommBuffer[3] << 8) | i2cCommBuffer[2]);
                    if(y_data > 32768){
                        y_data -= 65536;
                    }
                    y_data /= 250;
                    z_data = ((i2cCommBuffer[5] << 8) | i2cCommBuffer[4]);
                    if(z_data > 32768){
                        z_data -= 65536;
                    }
                    z_data /= 250;
                    //x_data = i2cCommBuffer[0];

                    //Temperature = Temperature >> 4;

                    x_data -= drift;
                    x_angle += x_data / 100 * 2;



	/* Convert current temperature to float */
                    //TempC = 1.0 * Temperature * 0.0625;

    /* Display actual temperature */
                    textViewX.setText("X: " + String.format("%3.2f", x_angle) + DEGREE_SYMBOL + "/s");
                    textViewY.setText("Y: " + String.format("%3.2f", y_data) + DEGREE_SYMBOL + "/s");
                    textViewZ.setText("Z: " + String.format("%3.2f", z_data) + DEGREE_SYMBOL + "/s");
                     	/* Close the i2c file */
                    i2c.close(fileHande);
                }
            });
        }
    }
}
