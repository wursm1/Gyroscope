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

  /* Define widgets */
  TextView textViewTemperature;

  /* Temperature Degrees Celsius text symbol */
  private static final String DEGREE_SYMBOL = "\u2103";

  @Override
  protected void onCreate(Bundle savedInstanceState)
   {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main_i2c);

	textViewTemperature = (TextView) findViewById(R.id.textViewTemperature);

	/* Instantiate the new i2c device */
	i2c = new I2C();

	/* Open the i2c device */
	fileHande = i2c.open(MCP9800_FILE_NAME);

	/* Set the I2C slave address for all subsequent I2C device transfers */
	i2c.SetSlaveAddress(fileHande, L3GD20_I2C_ADDR);

	/* Setup i2c buffer for the configuration register */
	//i2cCommBuffer[0] = MCP9800_CONFIG;
	//i2cCommBuffer[1] = MCP9800_12_BIT;
	//i2c.write(fileHande, i2cCommBuffer, 2);

	/* Setup mcp9800 register to read the temperature */
	i2cCommBuffer[0] = L3GD20_REGISTER_WHO_AM_I;
	i2c.write(fileHande, i2cCommBuffer, 1);

	/* Read the current temperature from the mcp9800 device */
	i2c.read(fileHande, i2cCommBuffer, 1);

	/* Assemble the temperature values */
	Device_id = i2cCommBuffer[0];

    /* Setup mcp9800 register to read the temperature */
    i2cCommBuffer[0] = L3GD20_REGISTER_CTRL_REG1;
    i2c.write(fileHande, i2cCommBuffer, 1);
    i2cCommBuffer[0] = 0x0F;
    i2c.write(fileHande, i2cCommBuffer, 1);
    /* 250DPS */
    i2cCommBuffer[0] = L3GD20_REGISTER_CTRL_REG4;
    i2c.write(fileHande, i2cCommBuffer, 1);
    i2cCommBuffer[0] = 0x00;
    i2c.write(fileHande, i2cCommBuffer, 1);
    /* get data */
    i2cCommBuffer[0] = L3GD20_REGISTER_OUT_X_L;
    i2c.write(fileHande, i2cCommBuffer, 1);
    i2c.read(fileHande, i2cCommBuffer, 6);

    /* Display actual temperature */
    textViewTemperature.setText("Temperature: " + String.format("%d", Device_id) + DEGREE_SYMBOL);

	/* Close the i2c file */
	i2c.close(fileHande);
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
}
